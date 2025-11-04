package com.bizmate.common.config;

import com.bizmate.hr.security.UserPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditorAwareConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of("SYSTEM");
            }
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserPrincipal user) {
                return Optional.of(user.getEmpName());
            }
            return Optional.of("UNKNOWN");
        };
    }

    @Primary
    @Bean
    public AuditorAware<String> auditorProvider1() {
        return () -> {
            // Spring Security 컨텍스트에서 인증 정보 가져오기
            return Optional.ofNullable(SecurityContextHolder.getContext())
                    .map(SecurityContext::getAuthentication)
                    .filter(Authentication::isAuthenticated)
                    .map(Authentication::getPrincipal)
                    .map(principal -> {
                        // 1. Principal이 UserPrincipal 타입인지 확인
                        if (principal instanceof UserPrincipal) {
                            UserPrincipal userPrincipal = (UserPrincipal) principal;

                            // 2. "이름 (ID)" 형식으로 조합하여 반환
                            // 예: "홍길동 (hong.gildong)"
                            return String.format("%s (%s)",
                                    userPrincipal.getEmpName(),
                                    userPrincipal.getUsername()
                            );
                        }

                        // 3. UserPrincipal이 아닌 경우 (예: 테스트 또는 비로그인)
                        return principal.toString();
                    });
        };
    }
}
