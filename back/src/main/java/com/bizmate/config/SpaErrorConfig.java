package com.bizmate.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class SpaErrorConfig {

    /**
     * React 라우트(예: /dashboard, /mypage)에 직접 접근 시 404 오류가 발생합니다.
     * 이 404 오류(HttpStatus.NOT_FOUND)를 가로채서
     * React 앱의 진입점인 /index.html 로 포워딩(전달)합니다.
     */
    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> containerCustomizer() {
        return factory -> {
            ErrorPage error404 = new ErrorPage(HttpStatus.NOT_FOUND, "/index.html");
            factory.addErrorPages(error404);
        };
    }
}
