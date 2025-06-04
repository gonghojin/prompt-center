package com.gongdel.promptserver.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 인증 실패(401 Unauthorized) 시 JSON 응답을 반환하는 EntryPoint입니다.
 * Spring Security의 인증 실패 시 호출되는 컴포넌트로, 클라이언트에게 일관된 형식의 에러 응답을 제공합니다.
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String ERROR_CODE = "UNAUTHORIZED";
    private static final String ERROR_TYPE = "AUTHENTICATION_FAILED";
    private static final String ERROR_MESSAGE = "인증에 실패하였습니다.";
    private static final String ERROR_DESCRIPTION = "인증 실패";
    private static final String CONTENT_TYPE = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8";
    private static final String FALLBACK_ERROR_RESPONSE = "{\"error\":\"Internal Server Error\"}";

    // 미리 생성된 에러 상세 정보 (불변 객체)
    private static final Map<String, String> ERROR_DETAILS = Map.of(
            "type", ERROR_TYPE,
            "description", ERROR_DESCRIPTION);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        try {
            log.debug("Authentication failed for request: {} - Error type: {}",
                    request.getRequestURI(),
                    authException.getClass().getSimpleName());

            // 응답 헤더 설정 순서 최적화
            response.setContentType(CONTENT_TYPE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            setSecurityHeaders(response);

            String errorResponse = OBJECT_MAPPER.writeValueAsString(createErrorResponse());
            writeResponse(response, errorResponse);
        } catch (Exception e) {
            log.error("Failed to process error response", e);
            handleErrorResponseError(response);
        }
    }

    /**
     * 보안 관련 HTTP 헤더를 설정합니다.
     */
    private void setSecurityHeaders(HttpServletResponse response) {
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
    }

    /**
     * 응답을 안전하게 작성합니다.
     */
    private void writeResponse(HttpServletResponse response, String content) throws IOException {
        try (PrintWriter writer = response.getWriter()) {
            writer.write(content);
        }
    }

    /**
     * 에러 응답 생성 중 발생한 오류를 처리합니다.
     */
    private void handleErrorResponseError(HttpServletResponse response) throws IOException {
        try {
            response.setContentType(CONTENT_TYPE);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeResponse(response, FALLBACK_ERROR_RESPONSE);
        } catch (IOException e) {
            log.error("Failed to write fallback error response", e);
            throw e;
        }
    }

    /**
     * 인증 실패에 대한 에러 응답을 생성합니다.
     * 불변 객체를 반환하여 스레드 안전성을 보장합니다.
     *
     * @return 에러 응답을 담은 불변 Map 객체
     */
    private Map<String, Object> createErrorResponse() {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "code", ERROR_CODE,
                "message", ERROR_MESSAGE,
                "error", ERROR_DETAILS);
    }
}
