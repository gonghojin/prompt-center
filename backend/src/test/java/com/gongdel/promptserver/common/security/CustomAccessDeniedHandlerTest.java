package com.gongdel.promptserver.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomAccessDeniedHandler 테스트")
class CustomAccessDeniedHandlerTest {

    private CustomAccessDeniedHandler handler;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        handler = new CustomAccessDeniedHandler();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("handle 메서드 테스트")
    class HandleMethodTest {

        @Test
        @DisplayName("정상적인 접근 거부 응답을 반환한다")
        void givenValidRequest_whenHandle_thenReturnsForbiddenResponse() throws IOException, ServletException {
            // Given
            AccessDeniedException exception = new AccessDeniedException("Access denied");
            request.setRequestURI("/api/test");

            // When
            handler.handle(request, response, exception);

            // Then
            assertThat(response.getStatus()).isEqualTo(FORBIDDEN.value());
            assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
            String responseBody = response.getContentAsString();
            assertThat(responseBody)
                    .contains("\"code\":\"FORBIDDEN\"")
                    .contains("\"message\":\"접근 권한이 없습니다.\"")
                    .contains("\"type\":\"ACCESS_DENIED\"");
        }

        @Test
        @DisplayName("보안 헤더가 올바르게 설정된다")
        void givenValidRequest_whenHandle_thenSetsSecurityHeaders() throws IOException, ServletException {
            // Given
            AccessDeniedException exception = new AccessDeniedException("Access denied");

            // When
            handler.handle(request, response, exception);

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
        void givenValidRequest_whenHandle_thenResponseContainsAllRequiredFields()
                throws IOException, ServletException {
            // Given
            AccessDeniedException exception = new AccessDeniedException("Access denied");

            // When
            handler.handle(request, response, exception);
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
        void givenValidRequest_whenHandle_thenSetsCorrectContentType() throws IOException, ServletException {
            // Given
            AccessDeniedException exception = new AccessDeniedException("Access denied");

            // When
            handler.handle(request, response, exception);

            // Then
            assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");
        }
    }
}
