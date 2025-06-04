package com.gongdel.promptserver.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 인가 실패(403 Forbidden) 시 JSON 응답을 반환하는 AccessDeniedHandler입니다.
 *
 * @author gongdel
 * @version 1.0
 */
@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String ERROR_CODE = "FORBIDDEN";
    private static final String ERROR_MESSAGE = "접근 권한이 없습니다.";
    private static final String ERROR_TYPE = "ACCESS_DENIED";
    private static final String ERROR_DESCRIPTION = "인가(권한) 실패";
    private static final String CONTENT_TYPE = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8";
    private static final String FALLBACK_ERROR_RESPONSE = "{\"error\":\"Internal Server Error\"}";

    // 미리 생성된 에러 상세 정보 (불변 객체)
    private static final Map<String, String> ERROR_DETAILS = Map.of(
            "type", ERROR_TYPE,
            "description", ERROR_DESCRIPTION);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        try {
            log.warn("Access denied for request URI: {}, Message: {}",
                    request.getRequestURI(), accessDeniedException.getMessage());

            // 응답 헤더 설정 순서 최적화
            response.setContentType(CONTENT_TYPE);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            setSecurityHeaders(response);

            Map<String, Object> errorResponse = createErrorResponse();
            writeResponse(response, OBJECT_MAPPER.writeValueAsString(errorResponse));

        } catch (Exception e) {
            log.error("Error handling access denied exception", e);
            handleErrorResponseError(response);
        }
    }

    /**
     * 보안 헤더를 설정합니다.
     *
     * @param response HTTP 응답 객체
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
     * 에러 응답 객체를 생성합니다.
     *
     * @return 에러 응답을 담은 Map 객체
     */
    private Map<String, Object> createErrorResponse() {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "code", ERROR_CODE,
                "message", ERROR_MESSAGE,
                "error", ERROR_DETAILS);
    }
}
