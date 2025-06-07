package com.gongdel.promptserver.adapter.in.rest.advice;

import com.gongdel.promptserver.application.exception.ApplicationErrorCode;
import com.gongdel.promptserver.application.exception.ApplicationException;
import com.gongdel.promptserver.domain.exception.BaseException;
import com.gongdel.promptserver.domain.exception.ErrorCode;
import com.gongdel.promptserver.domain.exception.PromptErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler 테스트")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Nested
    @DisplayName("handleBaseException(BaseException) 메서드는")
    class HandleBaseExceptionTest {
        @Test
        @DisplayName("도메인 예외를 표준화된 응답과 상태코드로 반환한다")
        void givenBaseException_whenHandle_thenReturnsExpectedResponse() {
            // Given
            ErrorCode errorCode = PromptErrorType.NOT_FOUND;
            String message = "프롬프트를 찾을 수 없습니다";
            BaseException ex = mock(BaseException.class);
            when(ex.getErrorCode()).thenReturn(errorCode);
            when(ex.getMessage()).thenReturn("내부 상세 메시지(노출X)");

            // When
            ResponseEntity<Map<String, Object>> response = handler.handleBaseException(ex);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).containsEntry("code", errorCode.getClass().getSimpleName());
            assertThat(response.getBody()).containsEntry("message", errorCode.getMessage());
        }
    }

    @Nested
    @DisplayName("handleApplicationException(ApplicationException) 메서드는")
    class HandleApplicationExceptionTest {
        @Test
        @DisplayName("애플리케이션 예외를 표준화된 응답과 상태코드로 반환한다")
        void givenApplicationException_whenHandle_thenReturnsExpectedResponse() {
            // Given
            ApplicationException ex = mock(ApplicationException.class);
            when(ex.getErrorCode()).thenReturn(ApplicationErrorCode.CATEGORY_NOT_FOUND);
            when(ex.getMessage()).thenReturn("내부 상세 메시지(노출X)");
            when(ex.getErrorCodeName()).thenReturn(ApplicationErrorCode.CATEGORY_NOT_FOUND.name());
            when(ex.getErrorCodeMessage()).thenReturn(ApplicationErrorCode.CATEGORY_NOT_FOUND.getMessage());
            when(ex.getHttpStatus()).thenReturn(HttpStatus.NOT_FOUND);

            // When
            ResponseEntity<Map<String, Object>> response = handler.handleApplicationException(ex);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).containsEntry("code",
                ApplicationErrorCode.CATEGORY_NOT_FOUND.getClass().getSimpleName());
            assertThat(response.getBody()).containsEntry("message",
                ApplicationErrorCode.CATEGORY_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("handleValidationException(MethodArgumentNotValidException) 메서드는")
    class HandleValidationExceptionTest {
        @Test
        @DisplayName("유효성 검증 실패 예외를 400 상태와 표준 메시지로 반환한다")
        void givenValidationException_whenHandle_thenReturnsBadRequest() {
            // Given
            MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
            when(ex.getBindingResult()).thenReturn(Mockito.mock(org.springframework.validation.BindingResult.class));
            when(ex.getBindingResult().getFieldErrors()).thenReturn(java.util.List.of(
                new org.springframework.validation.FieldError("object", "field", "필수값입니다")));

            // When
            ResponseEntity<Map<String, Object>> response = handler.handleValidationException(ex);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).containsEntry("code", "VALIDATION_ERROR");
            assertThat(response.getBody()).containsEntry("message", "입력값 검증 오류");
            assertThat(response.getBody()).containsKey("errors");
            Object errorsObj = response.getBody().get("errors");
            assertThat(errorsObj).isInstanceOf(java.util.List.class);
            @SuppressWarnings("unchecked")
            java.util.List<String> errors = (java.util.List<String>) errorsObj;
            assertThat(errors).contains("field: 필수값입니다");
        }
    }

    @Nested
    @DisplayName("handleIllegalArgumentException(IllegalArgumentException) 메서드는")
    class HandleIllegalArgumentExceptionTest {
        @Test
        @DisplayName("잘못된 파라미터 예외를 400 상태와 표준 메시지로 반환한다")
        void givenIllegalArgumentException_whenHandle_thenReturnsBadRequest() {
            // Given
            IllegalArgumentException ex = new IllegalArgumentException("잘못된 요청 파라미터");

            // When
            ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgumentException(ex);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).containsEntry("code", "VALIDATION_ERROR");
            assertThat(response.getBody()).containsEntry("message", "잘못된 요청 파라미터");
        }
    }

    @Nested
    @DisplayName("handleGenericException(Exception) 메서드는")
    class HandleGenericExceptionTest {
        @Test
        @DisplayName("일반 예외를 500 상태와 표준 메시지로 반환한다")
        void givenGenericException_whenHandle_thenReturnsInternalServerError() {
            // Given
            Exception ex = new Exception("예상치 못한 오류");

            // When
            ResponseEntity<Map<String, Object>> response = handler.handleGenericException(ex);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).containsEntry("code", "INTERNAL_SERVER_ERROR");
            assertThat(response.getBody()).containsEntry("message", "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    @Nested
    @DisplayName("handleMissingServletRequestParameterException(MissingServletRequestParameterException) 메서드는")
    class HandleMissingServletRequestParameterExceptionTest {
        @Test
        @DisplayName("필수 파라미터 누락 예외를 400 상태와 표준 메시지로 반환한다")
        void givenMissingServletRequestParameterException_whenHandle_thenReturnsBadRequest() {
            // Given
            String parameterName = "testParam";
            String parameterType = "String";
            MissingServletRequestParameterException ex = new MissingServletRequestParameterException(parameterName,
                parameterType);

            // When
            ResponseEntity<Map<String, Object>> response = handler.handleMissingServletRequestParameterException(ex);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).containsEntry("code", "MISSING_PARAMETER");
            assertThat(response.getBody()).containsEntry("message",
                String.format("필수 파라미터 '%s'가 누락되었습니다.", parameterName));
        }
    }
}
