package com.bizmate.hr.config;

import com.bizmate.hr.security.filter.JWTCheckFilter;
import com.bizmate.hr.security.handler.APILoginFailHandler;
import com.bizmate.hr.security.handler.APILoginSuccessHandler;
import com.bizmate.hr.security.handler.CustomAccessDeniedHandler;
import com.bizmate.hr.security.handler.CustomAuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class CustomSecurityConfig {

    private final JWTCheckFilter jwtCheckFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final APILoginSuccessHandler apiLoginSuccessHandler;
    private final APILoginFailHandler apiLoginFailHandler;

    /**
     * PasswordEncoder 등록 (비밀번호 해싱)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Spring Security 필터 체인
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("▶▶▶ CustomSecurityConfig FilterChain 설정 시작");

        http
                // 1️⃣ 기본 보안 기능 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2️⃣ 세션 사용 안 함 (JWT 기반)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3️⃣ 인가 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능한 경로들
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/assets/**",
                                "/favicon.ico",
                                "/manifest.json",
                                "/api/auth/**",       // 로그인, 회원가입
                                "/api/auth/refresh",// 토큰 재발급
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/h2-console/**",
                                "/api/approvals/attachments/preview/**",
                                "/api/approvals/attachments/download/**",
                                "/h2-console/**",
                                "/api/assignments/**"
                        ).permitAll()


                        .requestMatchers("/api/employees/me").authenticated()
                        // OPTIONS 메서드 허용 (CORS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )

                // 4️⃣ JWT 필터 등록
                .addFilterBefore(jwtCheckFilter, UsernamePasswordAuthenticationFilter.class)

                // 5️⃣ 예외 처리 (JWT 인증 예외 대응)
                .exceptionHandling(exception -> exception
                        // 인증 실패 (토큰 없음/잘못됨)
                        .authenticationEntryPoint((req, res, ex) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("""
                    {"error": "UNAUTHORIZED", "message": "인증이 필요합니다."}
                    """);
                        })
                        // 인가 실패 (권한 부족)
                        .accessDeniedHandler((req, res, ex) -> {
                            res.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("""
                    {"error": "ACCESS_DENIED", "message": "접근 권한이 없습니다."}
                    """);
                        })
                );

        return http.build();
    }

    /**
     * CORS 설정 (React 등 클라이언트와 통신 허용)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("▶▶▶ CustomSecurityConfig CORS 설정");

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * RoleHierarchy 설정 (기존 유지)
     * - JWT 기반이라도, @PreAuthorize("hasRole('ROLE_MANAGER')") 같은 곳에서 역할 계층이 적용됨
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        // === [1️⃣ 최상위 계층] ===
        // CEO → 시스템 관리자 전체 권한 포함
        StringBuilder hierarchy = new StringBuilder("""
        ROLE_CEO > ROLE_ADMIN
        ROLE_ADMIN > ROLE_MANAGER > ROLE_EMPLOYEE
        ROLE_ADMIN > sys:admin
        """);

        // === [2️⃣ 시스템 관리 계층] ===
        hierarchy.append("""
        sys:admin > sys:manage
        sys:manage > data:write:all
        sys:manage > data:read:all
        """);

        // === [3️⃣ 일반 역할 계층] ===
        hierarchy.append("""
        ROLE_MANAGER > ROLE_EMPLOYEE
        """);

        // === [4️⃣ 읽기 권한 계층] ===
        hierarchy.append("""
        data:read:all > data:read:self
        data:read:all > emp:read
        data:read:all > dept:read
        data:read:all > pos:read
        data:read:all > grade:read
        """);

        // === [5️⃣ 쓰기 권한 계층] ===
        hierarchy.append("""
        data:write:all > data:write:self
        data:write:all > emp:create
        data:write:all > emp:update
        data:write:all > emp:delete
        data:write:all > project:write
        """);

        roleHierarchy.setHierarchy(hierarchy.toString());
        return roleHierarchy;
    }
}
