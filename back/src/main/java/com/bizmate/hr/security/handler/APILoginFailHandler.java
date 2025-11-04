package com.bizmate.hr.security.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component; // ★ 변경점: Bean 등록
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * [APILoginFailHandler]
 * - 로그인 실패 시 예외 메시지를 JSON 형태로 응답하는 핸들러
 */
@Slf4j
@Component // ★ Spring Bean으로 등록
public class APILoginFailHandler implements AuthenticationFailureHandler  {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        log.info("▶▶▶ APILoginFailHandler 실행: 로그인 실패. {}", exception.getMessage());

        // 학원에서 배우신 로직 그대로 사용: JSON 응답 생성
        Gson gson = new Gson();
        String jsonStr = gson.toJson(Map.of("error", "ERROR_LOGIN", "message", exception.getMessage()));

        // 응답 상태 코드는 200 대신 401 Unauthorized를 사용하는 것이 RESTful API 관례에 더 적합합니다.
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // HTTP 401
        response.setContentType("application/json");

        PrintWriter printWriter = response.getWriter();
        printWriter.println(jsonStr);
        printWriter.close();
    }
}