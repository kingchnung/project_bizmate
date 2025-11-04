//package com.bizmate.groupware.board.security;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.web.access.AccessDeniedHandler;
//
//import java.io.IOException;
//
//public class JsonAccessDeniedHandler implements AccessDeniedHandler {
//    @Override
//    public void handle(HttpServletRequest request, HttpServletResponse response,
//                       AccessDeniedException accessDeniedException) throws IOException {
//        response.setStatus(HttpStatus.FORBIDDEN.value());
//        response.setContentType("application/json;charset=UTF-8");
//        response.getWriter().write("{\"code\":\"FORBIDDEN\",\"message\":\"권한이 없습니다.\"}");
//    }
//}