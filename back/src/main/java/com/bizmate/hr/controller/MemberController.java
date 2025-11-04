package com.bizmate.hr.controller;

import com.bizmate.hr.dto.member.LoginRequestDTO;
import com.bizmate.hr.dto.member.ResetPasswordRequest;
import com.bizmate.hr.security.jwt.JWTProvider;
import com.bizmate.hr.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class MemberController {

    private final AuthService authService;
    private final JWTProvider jwtProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // ✅ Refresh Token으로 새 Access Token 발급
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        log.info("♻️ Refresh 요청 들어옴");

        String refreshToken = jwtProvider.extractRefreshToken(request);
        if (refreshToken == null) {
            log.warn("❌ Refresh token not found in request");
            return ResponseEntity.badRequest().body(Map.of("error", "Refresh token not found"));
        }

        try {
            // RefreshToken 유효성 검증
            if (!jwtProvider.validateToken(refreshToken)) {
                log.warn("❌ Invalid Refresh Token");
                return ResponseEntity.status(401).body(Map.of("error", "Invalid refresh token"));
            }

            Claims claims = jwtProvider.parseClaims(refreshToken);
            String username = claims.getSubject();
            log.info("✅ RefreshToken 인증 성공: username={}", username);

            // username으로 새 AccessToken 생성
            var principal = jwtProvider.rebuildPrincipal(username);
            String newAccessToken = jwtProvider.createAccessToken(principal);

            Map<String, Object> userInfo = Map.of(
                    "userId", principal.getUserId(),
                    "username", principal.getUsername(),
                    "empName", principal.getEmpName(),
                    "email", principal.getEmail(),
                    "deptCode", principal.getDeptCode(),
                    "deptName", principal.getDeptName(),
                    "roles", principal.getAuthorities()
            );

            return ResponseEntity.ok(Map.of(
                    "accessToken", newAccessToken,
                    "user", userInfo,
                    "message", "새 Access Token이 발급되었습니다."
            ));
        } catch (JwtException e) {
            log.error("❌ Refresh 토큰 처리 중 오류: {}", e.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", "Refresh token expired or invalid"));
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest dto) {
        authService.resetPassword(dto);
        return ResponseEntity.ok().body(
                "임시 비밀번호가 등록된 이메일(" + dto.getEmail() + ")로 발송되었습니다.");
    }


}
