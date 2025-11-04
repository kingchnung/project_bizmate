package com.bizmate.hr.service;

import com.bizmate.hr.advice.LoginFailedException;
import com.bizmate.hr.domain.UserEntity;
import com.bizmate.hr.dto.member.*;
import com.bizmate.hr.repository.UserRepository;
import com.bizmate.hr.security.UserPrincipal;
import com.bizmate.hr.security.jwt.JWTProvider;
import com.bizmate.hr.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final UserService userService;

    // ==========================================================
    // ✅ 1. 로그인
    // ==========================================================
    public Map<String, Object> login(LoginRequestDTO request) {
        log.info("🔐 로그인 시도: {}", request.getUsername());

        UserEntity user = userRepository.findActiveUserWithDetails(request.getUsername())
                .orElseThrow(() -> new LoginFailedException("사용자를 찾을 수 없습니다."));

        if ("N".equalsIgnoreCase(user.getIsActive())) {
            throw new LoginFailedException("비활성화된 계정입니다. 관리자에게 문의하세요.");
        }

        if ("Y".equalsIgnoreCase(user.getIsLocked())) {
            throw new LoginFailedException("계정이 잠금 상태입니다. 관리자에게 문의하세요.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPwHash())) {
            int newFailCount = userService.processLoginFailure(request.getUsername());
            int remaining = Math.max(0, 5 - newFailCount);
            throw new LoginFailedException("비밀번호가 일치하지 않습니다. (남은 시도: " + remaining + "회)");
        }

        userService.processLoginSuccess(request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        String accessToken = jwtProvider.createAccessToken(principal);
        String refreshToken = jwtProvider.createRefreshToken(principal);

        Map<String, Object> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        tokens.put("username", principal.getUsername());
        tokens.put("roles", principal.getAuthorities());
        tokens.put("userId", principal.getUserId());
        tokens.put("empId", principal.getEmpId());

        log.info("✅ 로그인 성공: {} (토큰 발급 완료)", principal.getUsername());
        return tokens;
    }

    // ==========================================================
    // ✅ 2. 토큰 재발급
    // ==========================================================
    public Map<String, Object> refresh(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 Refresh Token입니다.");
        }

        Authentication authentication = jwtProvider.getAuthentication(refreshToken);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        String newAccessToken = jwtProvider.createAccessToken(principal);

        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", newAccessToken);
        result.put("refreshToken", refreshToken);
        return result;
    }

    // ==========================================================
    // ✅ 3. 비로그인 사용자 비밀번호 초기화
    // ==========================================================
    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest dto) {
        UserEntity user = userRepository.findByUsername(dto.getUsername())
                .filter(u -> u.getEmail().equalsIgnoreCase(dto.getEmail()))
                .orElseThrow(() -> new RuntimeException("계정을 찾을 수 없습니다."));

        String tempPw = RandomStringUtils.randomAlphanumeric(10);
        user.setPwHash(passwordEncoder.encode(tempPw));
        userRepository.save(user);

        mailService.sendPasswordResetMail(user.getEmail(), tempPw);
        log.info("📧 비로그인 사용자 임시비밀번호 발송 완료 (username: {})", dto.getUsername());
    }
}
