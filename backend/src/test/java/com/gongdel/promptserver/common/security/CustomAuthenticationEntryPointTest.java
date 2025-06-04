package com.gongdel.promptserver.common.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomAuthenticationEntryPoint 테스트")
class CustomAuthenticationEntryPointTest {

    private CustomAuthenticationEntryPoint entryPoint;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        entryPoint = new CustomAuthenticationEntryPoint();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Nested
    @DisplayName("commence 메서드 테스트")
    class CommenceTest {

        @Test
        @DisplayName("정상적인 인증 실패 응답을 반환한다")
        void givenValidRequest_whenCommence_thenReturnsUnauthorizedResponse() throws IOException, ServletException {
            // Given
            AuthenticationException authException = new BadCredentialsException("Invalid credentials");
            request.setRequestURI("/api/test");

            // When
            entryPoint.commence(request, response, authException);

            // Then
            assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
            assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");
            String responseBody = response.getContentAsString();
            assertThat(responseBody)
                    .contains("\"code\":\"UNAUTHORIZED\"")
                    .contains("\"message\":\"인증에 실패하였습니다.\"")
                    .contains("\"type\":\"AUTHENTICATION_FAILED\"");
        }

        @Test
        @DisplayName("보안 헤더가 올바르게 설정된다")
        void givenValidRequest_whenCommence_thenSetsSecurityHeaders() throws IOException, ServletException {
            // Given
            AuthenticationException authException = new BadCredentialsException("Invalid credentials");

            // When
            entryPoint.commence(request, response, authException);

            // Then
            assertThat(response.getHeader("X-Content-Type-Options")).isEqualTo("nosniff");
            assertThat(response.getHeader("X-Frame-Options")).isEqualTo("DENY");
            assertThat(response.getHeader("X-XSS-Protection")).isEqualTo("1; mode=block");
            assertThat(response.getHeader("Cache-Control")).isEqualTo("no-store, no-cache, must-revalidate, max-age=0");
            assertThat(response.getHeader("Pragma")).isEqualTo("no-cache");
        }
    }

    @Nested
    @DisplayName("응답 형식 테스트")
    class ResponseFormatTest {

        @Test
        @DisplayName("응답에 필수 필드가 모두 포함되어 있다")
        void givenValidRequest_whenCommence_thenResponseContainsAllRequiredFields()
                throws IOException, ServletException {
            // Given
            AuthenticationException authException = new BadCredentialsException("Invalid credentials");

            // When
            entryPoint.commence(request, response, authException);
            String responseBody = response.getContentAsString();

            // Then
            assertThat(responseBody)
                    .contains("\"timestamp\"")
                    .contains("\"code\"")
                    .contains("\"message\"")
                    .contains("\"error\"")
                    .contains("\"type\"")
                    .contains("\"description\"");
        }

        @Test
        @DisplayName("응답의 Content-Type이 올바르게 설정된다")
        void givenValidRequest_whenCommence_thenSetsCorrectContentType() throws IOException, ServletException {
            // Given
            AuthenticationException authException = new BadCredentialsException("Invalid credentials");

            // When
            entryPoint.commence(request, response, authException);

            // Then
            assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");
        }
    }
}
