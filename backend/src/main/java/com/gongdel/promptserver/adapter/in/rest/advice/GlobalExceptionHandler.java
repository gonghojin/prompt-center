package com.gongdel.promptserver.adapter.in.rest.advice;

import com.gongdel.promptserver.application.exception.ApplicationErrorCode;
import com.gongdel.promptserver.application.exception.ApplicationException;
import com.gongdel.promptserver.domain.exception.BaseException;
import com.gongdel.promptserver.domain.exception.CategoryErrorType;
import com.gongdel.promptserver.domain.exception.ErrorCode;
import com.gongdel.promptserver.domain.exception.PromptErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 애플리케이션 전역에서 발생하는 예외를 처리하는 Advice 클래스
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * BaseException을 처리하는 핸들러
     * 도메인 계층의 모든 비즈니스 예외를 처리
     * (일반적으로 모든 도메인 예외는 애플리케이션 계층에서 애플리케이션 예외로 변환되어야 하지만,
     * 누락된 경우를 대비해 포괄적으로 처리합니다)
     *
     * @param ex 발생한 예외
     * @return 에러 응답
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Map<String, Object>> handleBaseException(BaseException ex) {
        logException("Domain exception occurred", ex);

        ErrorCode errorCode = ex.getErrorCode();
        Map<String, Object> errorResponse = createErrorResponse(
                errorCode.getClass().getSimpleName(),
                ex.getMessage(),
                errorCode.name(),
                errorCode.getMessage());

        return new ResponseEntity<>(errorResponse, determineHttpStatus(ex));
    }

    /**
     * ApplicationException을 처리하는 핸들러
     * 애플리케이션 계층의 모든 비즈니스 예외를 처리
     *
     * @param ex 발생한 예외
     * @return 에러 응답
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Map<String, Object>> handleApplicationException(ApplicationException ex) {
        logException("Application exception occurred", ex);

        Map<String, Object> errorResponse = createErrorResponse(
                ex.getErrorCode().getClass().getSimpleName(),
                ex.getMessage(),
                ex.getErrorCodeName(),
                ex.getErrorCodeMessage());

        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    /**
     * 일반 예외를 처리하는 핸들러
     * 모든 예상치 못한 예외를 처리
     *
     * @param ex 발생한 예외
     * @return 에러 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        logException("Unexpected exception occurred", ex);

        Map<String, Object> errorResponse = createErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
                "SYSTEM_ERROR",
                "서버 내부 오류");

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * @Valid 유효성 검증 실패 예외를 처리하는 핸들러
     *
     * @param ex 발생한 예외
     * @return 에러 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        logException("Validation failed", ex);
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        Map<String, Object> errorResponse = createErrorResponse(
                "VALIDATION_ERROR",
                message,
                "VALIDATION_ERROR",
                "입력값 검증 오류");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 표준화된 방식으로 예외를 로깅
     *
     * @param message 로그 메시지 접두사
     * @param ex      발생한 예외
     */
    private void logException(String message, Exception ex) {
        if (ex instanceof BaseException) {
            BaseException baseEx = (BaseException) ex;
            log.error("{}: {}. Error code: {}", message, ex.getMessage(), baseEx.getErrorCode(), ex);
        } else if (ex instanceof ApplicationException) {
            ApplicationException appEx = (ApplicationException) ex;
            log.error("{}: {}. Error code: {}", message, ex.getMessage(), appEx.getErrorCode(), ex);
        } else {
            log.error("{}: {}", message, ex.getMessage(), ex);
        }
    }

    /**
     * 오류 응답 기본 구조를 생성
     *
     * @param code                 오류 코드
     * @param message              오류 메시지
     * @param errorType            오류 유형
     * @param errorTypeDescription 오류 유형 설명
     * @return 오류 응답 맵
     */
    private Map<String, Object> createErrorResponse(String code, String message, String errorType,
            String errorTypeDescription) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("code", code);
        errorResponse.put("message", message);

        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("type", errorType);
        errorDetails.put("description", errorTypeDescription);
        errorResponse.put("error", errorDetails);

        return errorResponse;
    }

    /**
     * BaseException에서 적절한 HTTP 상태 코드를 결정
     *
     * @param ex 발생한 예외
     * @return HTTP 상태 코드
     */
    private HttpStatus determineHttpStatus(BaseException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        // PromptErrorType 처리
        if (errorCode instanceof PromptErrorType) {
            return mapPromptErrorToHttpStatus((PromptErrorType) errorCode);
        }

        // CategoryErrorType 처리
        if (errorCode instanceof CategoryErrorType) {
            return mapCategoryErrorToHttpStatus((CategoryErrorType) errorCode);
        }

        // ApplicationErrorCode 처리
        if (errorCode instanceof ApplicationErrorCode) {
            return mapApplicationErrorToHttpStatus((ApplicationErrorCode) errorCode);
        }

        // 기본 상태 코드
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    /**
     * ApplicationErrorCode에 따른 HTTP 상태 코드 매핑
     */
    private HttpStatus mapApplicationErrorToHttpStatus(ApplicationErrorCode errorCode) {
        switch (errorCode) {
            case PROMPT_VALIDATION_ERROR:
                return HttpStatus.BAD_REQUEST;
            case PROMPT_INSUFFICIENT_PERMISSION:
                return HttpStatus.FORBIDDEN;
            case PROMPT_PERSISTENCE_ERROR:
            case PROMPT_TAG_PROCESSING_ERROR:
                return HttpStatus.INTERNAL_SERVER_ERROR;
            case CATEGORY_NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            case CATEGORY_DUPLICATE_NAME:
                return HttpStatus.CONFLICT;
            case CATEGORY_INVALID:
            case CATEGORY_CIRCULAR_REFERENCE:
                return HttpStatus.BAD_REQUEST;
            case CATEGORY_OPERATION_FAILED:
            case CATEGORY_UNKNOWN_ERROR:
                return HttpStatus.INTERNAL_SERVER_ERROR;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * PromptErrorType에 따른 HTTP 상태 코드 매핑
     */
    private HttpStatus mapPromptErrorToHttpStatus(PromptErrorType errorType) {
        switch (errorType) {
            case NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            case VALIDATION_ERROR:
            case TEMPLATE_PARSE_ERROR:
                return HttpStatus.BAD_REQUEST;
            case INSUFFICIENT_PERMISSION:
                return HttpStatus.FORBIDDEN;
            case DUPLICATE_TITLE:
                return HttpStatus.CONFLICT;
            case PERSISTENCE_ERROR:
            case TAG_PROCESSING_ERROR:
            case UNKNOWN_ERROR:
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * CategoryErrorType에 따른 HTTP 상태 코드 매핑
     */
    private HttpStatus mapCategoryErrorToHttpStatus(CategoryErrorType errorType) {
        switch (errorType) {
            case NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            case INVALID_CATEGORY:
            case CIRCULAR_REFERENCE:
                return HttpStatus.BAD_REQUEST;
            case DUPLICATE_NAME:
                return HttpStatus.CONFLICT;
            case OPERATION_FAILED:
            case UNKNOWN_ERROR:
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
