package com.bizmate.hr.controller;

import com.bizmate.hr.security.UserPrincipal;
import com.bizmate.hr.security.jwt.JWTProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * [APIRefreshController]
 * - Access Token이 만료되었을 때 Refresh Token을 이용하여 토큰을 재발급하는 엔드포인트
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class APIRefreshController {

    private final JWTProvider jwtProvider;
    private final long REFRESH_TOKEN_ROTATION_THRESHOLD_DAYS = 1;

    @RequestMapping(value = "/api/member/refresh", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, Object> refresh(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("refreshToken") String refreshToken) {

        // 1️⃣ Access Token 추출 및 유효성 확인
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new JwtException("Bearer 헤더 형식이 올바르지 않습니다.");
        }
        String accessToken = authHeader.substring(7);

        // 2️⃣ Access Token 만료 여부 확인
        if (!checkExpiredToken(accessToken)) {
            log.info("Access Token이 아직 유효하므로 재발급하지 않습니다.");
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }

        // 3️⃣ Refresh Token 검증 및 Claims 추출
        Claims claims = jwtProvider.parseClaims(refreshToken);

        // 4️⃣ Claims → UserPrincipal 복원
        UserPrincipal principal = createPrincipalFromClaims(claims);

        // 5️⃣ 새 Access Token 발급 (리팩터 버전)
        String newAccessToken = jwtProvider.createAccessToken(principal);

        // 6️⃣ Refresh Token 갱신 여부 판단 및 발급
        String newRefreshToken = refreshToken;
        Date expirationDate = claims.getExpiration();

        if (checkTimeForRotation(expirationDate)) {
            newRefreshToken = jwtProvider.createRefreshToken(principal);
            log.info("Refresh Token 만료일 임박 → 새 Refresh Token 발급");
        } else {
            log.info("Refresh Token이 충분히 유효하여 기존 토큰 유지");
        }

        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
    }

    /**
     * Access Token 만료 여부 확인
     */
    private boolean checkExpiredToken(String accessToken) {
        try {
            jwtProvider.validateToken(accessToken);
            return false; // 유효함
        } catch (ExpiredJwtException e) {
            return true; // 만료됨
        } catch (JwtException e) {
            log.error("Access Token 검증 오류: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Refresh Token 갱신 임계치 확인 (예: 1일 이하 남으면 교체)
     */
    private boolean checkTimeForRotation(Date expirationDate) {
        long remainingMillis = expirationDate.getTime() - System.currentTimeMillis();
        long threshold = REFRESH_TOKEN_ROTATION_THRESHOLD_DAYS * 24 * 60 * 60 * 1000;
        return remainingMillis < threshold;
    }

    /**
     * ✅ Claims 정보를 기반으로 UserPrincipal 복원
     */
    private UserPrincipal createPrincipalFromClaims(Claims claims) {
        Long userId = claims.get("uid", Long.class);
        Long empId = claims.get("empId", Long.class);
        String username = claims.getSubject();

        @SuppressWarnings("unchecked")
        List<String> roleNames = (List<String>) claims.getOrDefault("roles", Collections.emptyList());

        List<GrantedAuthority> authorities = roleNames.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UserPrincipal(
                userId,
                empId,
                username,
                "", // password는 JWT에 없음
                true,
                false,
                authorities
        );
    }
}
