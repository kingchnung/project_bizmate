package com.bizmate.hr.security.handler;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * [CustomAuthenticationEntryPoint]
 * - JWT가 없거나 유효하지 않아 인증이 필요한 리소스 접근 시 호출됨 (401 Unauthorized 처리)
 * - HTML 로그인 페이지 대신, JSON 형태의 오류 메시지를 응답합니다.
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        log.warn("인증되지 않은 접근 시도: {} - {}", request.getRequestURI(), authException.getMessage());

        // 1. 응답 설정: 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        // 2. 응답 내용: JSON 오류 메시지
        Map<String, String> responseMap = Map.of(
                "error", "UNAUTHORIZED_ACCESS",
                "message", "유효한 인증 정보(JWT)가 필요합니다."
        );

        // 3. JSON 변환 및 출력
        new Gson().toJson(responseMap, response.getWriter());
    }
}