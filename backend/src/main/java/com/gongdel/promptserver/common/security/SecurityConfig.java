package com.gongdel.promptserver.common.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.http.HttpMethod;

/**
 * Spring Security 및 JWT 인증 설정을 담당하는 클래스입니다.
 *
 * 주요 기능:
 * - 보안 필터 체인 설정
 * - 인증 관리자 설정
 * - 비밀번호 인코더 설정
 * - 사용자 상세 서비스 설정
 *
 * @author gongdel
 * @version 1.0
 */
@Slf4j
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/**",
            "/api/public/health",
            "/api/public/version"
    };

    protected static final String[] SWAGGER_ENDPOINTS = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/api-docs/**"
    };

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Value("${spring.profiles.active:prod}")
    private String activeProfile;

    /**
     * 보안 필터 체인을 구성합니다.
     *
     * @param http                    HttpSecurity 객체
     * @param jwtAuthenticationFilter JWT 인증 필터
     * @return SecurityFilterChain
     * @throws Exception 보안 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        log.info("Configuring security filter chain for profile: {}", activeProfile);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    // 공개 엔드포인트 설정
                    auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // CORS preflight 요청 허용
                            .requestMatchers(PUBLIC_ENDPOINTS).permitAll();

                    // 개발 환경에서만 Swagger UI 접근 허용
                    if ("dev".equals(activeProfile) || "local".equals(activeProfile)) {
                        auth.requestMatchers(SWAGGER_ENDPOINTS).permitAll();
                        log.info("Swagger UI access enabled for profile: {}", activeProfile);
                    }

                    // 나머지 요청은 인증 필요
                    auth.anyRequest().authenticated();
                })
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        log.debug("Security filter chain configuration completed");
        return http.build();
    }

    /**
     * 인증 관리자를 구성합니다.
     *
     * @param userDetailsService 사용자 상세 서비스
     * @param passwordEncoder    비밀번호 인코더
     * @return AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        log.info("Configuring authentication manager");

        try {
            DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
            provider.setUserDetailsService(userDetailsService);
            provider.setPasswordEncoder(passwordEncoder);
            provider.setHideUserNotFoundExceptions(true); // 보안을 위해 사용자 존재 여부 숨김

            return new ProviderManager(provider);
        } catch (Exception e) {
            log.error("Failed to configure authentication manager: {}", e.getMessage(), e);
            throw new RuntimeException("인증 관리자 설정 실패", e);
        }
    }

    /**
     * 비밀번호 암호화를 위한 PasswordEncoder를 생성합니다.
     * BCrypt 알고리즘을 사용하여 비밀번호를 안전하게 해시화합니다.
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("Creating BCrypt password encoder with strength 12");
        return new BCryptPasswordEncoder(12); // 강도 12로 설정
    }

    /**
     * 사용자 인증 정보를 제공하는 UserDetailsService를 등록합니다.
     *
     * @return UserDetailsService
     */
    @Bean
    public UserDetailsService userDetailsService() {
        log.info("Registering custom user details service");
        return customUserDetailsService;
    }
}
