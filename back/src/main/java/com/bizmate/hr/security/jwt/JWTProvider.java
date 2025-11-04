package com.bizmate.hr.security.jwt;

import com.bizmate.hr.security.CustomUserDetailsService;
import com.bizmate.hr.security.UserPrincipal;
import io.jsonwebtoken.*;

import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

import java.util.stream.Collectors;

/**
 * [JWTProvider]
 * - JWT 생성, 검증 및 정보 추출을 담당하는 클래스.
 * - ★★★ 실습 환경 효율을 위해 설정값을 코드 내부에 작성 (운영 환경에서는 외부 설정 사용 필수) ★★★
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JWTProvider {

    // ★★★ 1. 설정값 (코드 내장) ★★★
    // 비밀 키: 보안상 32바이트 이상 권장. (테스트용)
    private static final String SECRET_KEY = "1234567890123456789012345678901234567890";
    private static final Key ks = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    private final CustomUserDetailsService userDetailsService;



    private final long accessTokenValidityMillis = 1000L * 60 * 60;    // 1시간
    private final long refreshTokenValidityMillis = 1000L * 60 * 60 * 24 * 7; // 7일
    // ★★★ --------------------- ★★★

    // --- 1. 토큰 생성 메서드 ---

    /**
     * Access Token을 생성합니다.
     */
    public String createAccessToken(UserPrincipal principal) {
        log.info("jwt생성 직전 권한 목록 : {}, user : {}",principal.getAuthorities(), principal.getUsername());
        return createToken(principal, accessTokenValidityMillis);
    }

    /**
     * Refresh Token을 생성합니다.
     */
    // --- Refresh Token 생성 (재발급용) ---
    public String createRefreshToken(UserPrincipal principal) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", principal.getUserId());
        claims.put("type", "refresh");

        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenValidityMillis);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(principal.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(ks, SignatureAlgorithm.HS256 )
                .compact();
    }

    private String createToken(UserPrincipal principal, long validityMillis) {
        log.info("🔍 createAccessToken() principal 정보 확인:");
        log.info(" - userId: {}", principal.getUserId());
        log.info(" - username: {}", principal.getUsername());
        log.info(" - empName: {}", principal.getEmpName());
        log.info(" - email: {}", principal.getEmail());

        Map<String, Object> claims = new HashMap<>();

        claims.put("uid", principal.getUserId());
        claims.put("username", principal.getUsername());
        claims.put("empName", principal.getEmpName());
        claims.put("email", principal.getEmail());
        claims.put("empId",principal.getEmpId());
        claims.put("deptCode", principal.getDeptCode());
        claims.put("deptName", principal.getDeptName());
        claims.put("roles", principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        claims.put("type", "access");

        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityMillis);

        return Jwts.builder()
                .setSubject(principal.getUsername())
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(ks,SignatureAlgorithm.HS256)
                .compact();
    }

    // --- 2. 토큰 검증 및 정보 추출 메서드 (변경 없음) ---

    /**
     * 주어진 토큰이 유효한지 검증하고 Claims(Payload)를 반환합니다.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(ks).parseClaimsJws(token);
            log.debug("✅ 토큰 검증 성공");
            return true;
        } catch (JwtException e) {
            log.warn("❌ 토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    public Claims parseClaims(String token) {

        return Jwts.parser().setSigningKey(ks).parseClaimsJws(token).getBody();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        String username = claims.get("username", String.class);
        if (username == null) {
            username = claims.getSubject();
        }

        Long userId = claims.get("uid", Long.class);
        Long empId = claims.get("empId", Long.class);
        String empName = claims.get("empName", String.class);
        String email = claims.get("email", String.class);
        String deptName = claims.get("deptName", String.class);
        String deptCode = claims.get("deptCode", String.class);

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.getOrDefault("roles", Collections.emptyList());

        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserPrincipal principal = new UserPrincipal(
                userId,
                empId,
                username,
                "",  // 비밀번호는 JWT 안에 없음
                true,
                false,
                authorities
        );
        principal.setEmpName(empName);
        principal.setEmail(email);
        principal.setDeptCode(deptCode);
        principal.setDeptName(deptName);

        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    // ✅ Refresh Token 추출
    public String extractRefreshToken(HttpServletRequest request) {
        // 1. Authorization 헤더에서 가져오기 시도
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        // 2. 쿠키에서 가져오기 시도
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // ✅ Refresh Token 재발급 시 AccessToken 생성
    public String generateAccessTokenFromRefresh(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new JwtException("Refresh Token is invalid or expired");
        }

        Claims claims = parseClaims(refreshToken);
        String username = claims.getSubject();

        // ★ username 기반 UserPrincipal 재구성은 Service 단에서 수행해도 무방
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidityMillis);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(ks, SignatureAlgorithm.HS256)
                .compact();
    }

    public UserPrincipal rebuildPrincipal(String username) {
        // DB조회 대신, username 기반 principal 재생성
        var userDetails = userDetailsService.loadUserByUsername(username);
        return (UserPrincipal) userDetails;
    }



}
