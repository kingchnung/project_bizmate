package com.bizmate.hr.security.handler;

import java.io.IOException;
import java.util.Map;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * [CustomAccessDeniedHandler]
 * - 권한이 부족하여 접근이 거부될 경우 (403 Forbidden) JSON 응답을 전송합니다.
 */
@Component // Spring Bean으로 등록
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // 1. 응답 상태를 403 Forbidden으로 설정
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // HTTP 403

        // 2. 응답 Content-Type 설정
        response.setContentType("application/json; charset=UTF-8");

        // 3. JSON 응답 생성 (학원 예제와 동일)
        Gson gson = new Gson();
        String jsonStr = gson.toJson(Map.of(
                "error", "ERROR_ACCESSDENIED",
                "message", accessDeniedException.getMessage()
        ));

        response.getWriter().write(jsonStr);
        response.getWriter().flush();
    }
}