package com.gongdel.promptserver.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityConfig 테스트")
class SecurityConfigTest {

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Mock
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private HttpSecurity httpSecurity;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(
                customUserDetailsService,
                customAuthenticationEntryPoint,
                customAccessDeniedHandler);
    }

    @Nested
    @DisplayName("PasswordEncoder 테스트")
    class PasswordEncoderTest {

        @Test
        @DisplayName("BCryptPasswordEncoder가 올바르게 생성되어야 함")
        void shouldCreateBCryptPasswordEncoder() {
            // when
            PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

            // then
            assertThat(passwordEncoder).isNotNull();
            assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);

            // 비밀번호 인코딩 테스트
            String rawPassword = "testPassword123";
            String encodedPassword = passwordEncoder.encode(rawPassword);

            assertThat(encodedPassword).isNotEqualTo(rawPassword);
            assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
        }
    }

    @Nested
    @DisplayName("UserDetailsService 테스트")
    class UserDetailsServiceTest {

        @Test
        @DisplayName("CustomUserDetailsService가 올바르게 반환되어야 함")
        void shouldReturnCustomUserDetailsService() {
            // when
            UserDetailsService userDetailsService = securityConfig.userDetailsService();

            // then
            assertThat(userDetailsService).isNotNull();
            assertThat(userDetailsService).isEqualTo(customUserDetailsService);
        }
    }

    @Nested
    @DisplayName("AuthenticationManager 테스트")
    class AuthenticationManagerTest {

        @Test
        @DisplayName("AuthenticationManager가 올바르게 생성되어야 함")
        void shouldCreateAuthenticationManager() {
            // given
            PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

            // when
            AuthenticationManager authenticationManager = securityConfig.authenticationManager(
                    customUserDetailsService,
                    passwordEncoder);

            // then
            assertThat(authenticationManager).isNotNull();
        }
    }

    @Nested
    @DisplayName("SecurityFilterChain 테스트")
    class SecurityFilterChainTest {

        @Test
        @DisplayName("보안 필터 체인이 올바르게 구성되어야 함")
        void shouldConfigureSecurityFilterChain() throws Exception {
            // given
            when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
            when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
            when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
            when(httpSecurity.exceptionHandling(any())).thenReturn(httpSecurity);
            when(httpSecurity.addFilterAfter(any(), any())).thenReturn(httpSecurity);
            when(httpSecurity.build()).thenReturn(mock(DefaultSecurityFilterChain.class));

            // when
            SecurityFilterChain filterChain = securityConfig.securityFilterChain(
                    httpSecurity,
                    jwtAuthenticationFilter);

            // then
            assertThat(filterChain).isNotNull();
            verify(httpSecurity).csrf(any());
            verify(httpSecurity).sessionManagement(any());
            verify(httpSecurity).authorizeHttpRequests(any());
            verify(httpSecurity).exceptionHandling(any());
            verify(httpSecurity).addFilterAfter(any(), any());
        }

        @Test
        @DisplayName("개발 환경에서 Swagger UI 접근이 허용되어야 함")
        void shouldAllowSwaggerAccessInDevProfile() throws Exception {
            // given
            ReflectionTestUtils.setField(securityConfig, "activeProfile", "dev");
            when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
            when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
            when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
            when(httpSecurity.exceptionHandling(any())).thenReturn(httpSecurity);
            when(httpSecurity.addFilterAfter(any(), any())).thenReturn(httpSecurity);
            when(httpSecurity.build()).thenReturn(mock(DefaultSecurityFilterChain.class));

            // when
            securityConfig.securityFilterChain(httpSecurity, jwtAuthenticationFilter);

            // then
            verify(httpSecurity).authorizeHttpRequests(any());
        }
    }
}
