package com.bizmate.hr.security.handler;

import com.google.gson.Gson;
import com.bizmate.hr.security.UserPrincipal;
import com.bizmate.hr.security.jwt.JWTProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * [APILoginSuccessHandler]
 * - 로그인 성공 시 JWT Access/Refresh Token을 생성하고 JSON 형태로 응답
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class APILoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        log.info("✅ 로그인 성공 → JWT 토큰 발급 시작");

        // 1️⃣ 인증된 사용자 정보 가져오기
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        // 2️⃣ JWT Access / Refresh 토큰 생성
        String accessToken = jwtProvider.createAccessToken(principal);
        String refreshToken = jwtProvider.createRefreshToken(principal);

        // 3️⃣ 응답 데이터 구성
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("userId", principal.getUserId());
        responseBody.put("username", principal.getUsername());
        responseBody.put("roles", principal.getAuthorities());
        responseBody.put("accessToken", accessToken);
        responseBody.put("refreshToken", refreshToken);

        // 4️⃣ JSON 응답 전송
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter writer = response.getWriter();
        new Gson().toJson(responseBody, writer);
        writer.close();

        log.info("🎉 JWT 발급 완료: {}", principal.getUsername());
    }
}
