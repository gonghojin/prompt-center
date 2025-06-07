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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 애플리케이션 전역 예외를 처리하는 Advice 클래스입니다.
 * 모든 계층에서 발생하는 예외를 일관된 구조로 응답합니다.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    // 에러 코드/타입 상수 선언
    private static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    private static final String MISSING_PARAMETER = "MISSING_PARAMETER";
    private static final String SYSTEM_ERROR = "SYSTEM_ERROR";
    private static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

    /**
     * 도메인 계층의 비즈니스 예외(BaseException)를 처리합니다.
     *
     * @param ex 도메인 예외
     * @return 표준화된 에러 응답
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Map<String, Object>> handleBaseException(BaseException ex) {
        logException("Domain exception occurred", ex);
        ErrorCode errorCode = ex.getErrorCode();
        Map<String, Object> errorResponse = createErrorResponse(
            errorCode.getClass().getSimpleName(),
            errorCode.getMessage(),
            errorCode.name(),
            errorCode.getMessage());
        return new ResponseEntity<>(errorResponse, determineHttpStatus(ex));
    }

    /**
     * 애플리케이션 계층의 비즈니스 예외(ApplicationException)를 처리합니다.
     *
     * @param ex 애플리케이션 예외
     * @return 표준화된 에러 응답
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Map<String, Object>> handleApplicationException(ApplicationException ex) {
        logException("Application exception occurred", ex);
        Map<String, Object> errorResponse = createErrorResponse(
            ex.getErrorCode().getClass().getSimpleName(),
            ex.getErrorCodeMessage(),
            ex.getErrorCodeName(),
            ex.getErrorCodeMessage());
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    /**
     * @Valid 유효성 검증 실패 예외(MethodArgumentNotValidException)를 처리합니다.
     *
     * @param ex 유효성 검증 예외
     * @return 표준화된 에러 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        logException("Validation failed", ex);
        List<String> messages = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .toList();
        Map<String, Object> errorResponse = createErrorResponseWithList(
            VALIDATION_ERROR,
            "입력값 검증 오류",
            VALIDATION_ERROR,
            "입력값 검증 오류",
            messages);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 잘못된 파라미터 입력 시 발생하는 예외(IllegalArgumentException)를 처리합니다.
     *
     * @param ex 잘못된 파라미터 예외
     * @return 표준화된 에러 응답
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logException("Invalid argument", ex);
        Map<String, Object> errorResponse = createErrorResponse(
            VALIDATION_ERROR,
            ex.getMessage(),
            VALIDATION_ERROR,
            "잘못된 요청 파라미터");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 필수 요청 파라미터가 누락된 경우 발생하는 예외(MissingServletRequestParameterException)를 처리합니다.
     *
     * @param ex 누락된 파라미터 예외
     * @return 표준화된 에러 응답
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingServletRequestParameterException(
        MissingServletRequestParameterException ex) {
        logException("Missing required parameter", ex);
        Map<String, Object> errorResponse = createErrorResponse(
            MISSING_PARAMETER,
            String.format("필수 파라미터 '%s'가 누락되었습니다.", ex.getParameterName()),
            VALIDATION_ERROR,
            "필수 파라미터 누락");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 예상치 못한 모든 예외(Exception)를 처리합니다.
     *
     * @param ex 일반 예외
     * @return 표준화된 에러 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        logException("Unexpected exception occurred", ex);
        Map<String, Object> errorResponse = createErrorResponse(
            INTERNAL_SERVER_ERROR,
            "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
            SYSTEM_ERROR,
            "서버 내부 오류");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 존재하지 않는 정적/동적 리소스 요청 시 404 반환
     *
     * @param ex NoResourceFoundException
     * @return 404 NOT_FOUND 응답
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFoundException(NoResourceFoundException ex) {
        logException("No resource found", ex);
        Map<String, Object> errorResponse = createErrorResponse(
            "NOT_FOUND",
            "요청하신 리소스를 찾을 수 없습니다.",
            "NOT_FOUND",
            "리소스 없음");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * 예외를 표준화된 방식으로 로깅합니다.
     *
     * @param message 로그 메시지 접두사
     * @param ex      발생한 예외
     */
    private void logException(String message, Exception ex) {
        if (ex instanceof BaseException baseEx) {
            log.error("{}: {}. Error code: {}", message, ex.getMessage(), baseEx.getErrorCode(), ex);
        } else if (ex instanceof ApplicationException appEx) {
            log.error("{}: {}. Error code: {}", message, ex.getMessage(), appEx.getErrorCode(), ex);
        } else {
            log.error("{}: {}", message, ex.getMessage(), ex);
        }
    }

    /**
     * 표준화된 오류 응답 구조를 생성합니다.
     * 응답 Map을 불변 객체로 반환하여 외부 변조를 방지합니다.
     *
     * @param code                 오류 코드
     * @param message              오류 메시지
     * @param errorType            오류 유형
     * @param errorTypeDescription 오류 유형 설명
     * @return 오류 응답 맵 (불변)
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
        errorResponse.put("error", Collections.unmodifiableMap(errorDetails));

        return Collections.unmodifiableMap(errorResponse);
    }

    /**
     * 표준화된 오류 응답 구조(리스트 메시지 포함)를 생성합니다.
     *
     * @param code                 오류 코드
     * @param message              오류 메시지(요약)
     * @param errorType            오류 유형
     * @param errorTypeDescription 오류 유형 설명
     * @param errors               상세 오류 리스트
     * @return 오류 응답 맵 (불변)
     */
    private Map<String, Object> createErrorResponseWithList(String code, String message, String errorType,
                                                            String errorTypeDescription, List<String> errors) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("code", code);
        errorResponse.put("message", message);
        errorResponse.put("errors", errors);

        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("type", errorType);
        errorDetails.put("description", errorTypeDescription);
        errorResponse.put("error", Collections.unmodifiableMap(errorDetails));

        return Collections.unmodifiableMap(errorResponse);
    }

    /**
     * BaseException에서 적절한 HTTP 상태 코드를 결정합니다.
     *
     * @param ex 도메인 예외
     * @return HTTP 상태 코드
     */
    private HttpStatus determineHttpStatus(BaseException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        if (errorCode instanceof PromptErrorType promptErrorType) {
            return mapPromptErrorToHttpStatus(promptErrorType);
        }
        if (errorCode instanceof CategoryErrorType categoryErrorType) {
            return mapCategoryErrorToHttpStatus(categoryErrorType);
        }
        if (errorCode instanceof ApplicationErrorCode applicationErrorCode) {
            return mapApplicationErrorToHttpStatus(applicationErrorCode);
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    /**
     * ApplicationErrorCode에 따른 HTTP 상태 코드 매핑
     *
     * @param errorCode 애플리케이션 에러 코드
     * @return HTTP 상태 코드
     */
    private HttpStatus mapApplicationErrorToHttpStatus(ApplicationErrorCode errorCode) {
        return switch (errorCode) {
            case PROMPT_VALIDATION_ERROR, CATEGORY_INVALID, CATEGORY_CIRCULAR_REFERENCE -> HttpStatus.BAD_REQUEST;
            case PROMPT_INSUFFICIENT_PERMISSION -> HttpStatus.FORBIDDEN;
            case CATEGORY_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CATEGORY_DUPLICATE_NAME -> HttpStatus.CONFLICT;
            case PROMPT_PERSISTENCE_ERROR, PROMPT_TAG_PROCESSING_ERROR, CATEGORY_OPERATION_FAILED,
                 CATEGORY_UNKNOWN_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    /**
     * PromptErrorType에 따른 HTTP 상태 코드 매핑
     *
     * @param errorType 프롬프트 에러 타입
     * @return HTTP 상태 코드
     */
    private HttpStatus mapPromptErrorToHttpStatus(PromptErrorType errorType) {
        return switch (errorType) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case VALIDATION_ERROR, TEMPLATE_PARSE_ERROR -> HttpStatus.BAD_REQUEST;
            case INSUFFICIENT_PERMISSION -> HttpStatus.FORBIDDEN;
            case DUPLICATE_TITLE -> HttpStatus.CONFLICT;
            case PERSISTENCE_ERROR, TAG_PROCESSING_ERROR, UNKNOWN_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    /**
     * CategoryErrorType에 따른 HTTP 상태 코드 매핑
     *
     * @param errorType 카테고리 에러 타입
     * @return HTTP 상태 코드
     */
    private HttpStatus mapCategoryErrorToHttpStatus(CategoryErrorType errorType) {
        return switch (errorType) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case INVALID_CATEGORY, CIRCULAR_REFERENCE -> HttpStatus.BAD_REQUEST;
            case DUPLICATE_NAME -> HttpStatus.CONFLICT;
            case OPERATION_FAILED, UNKNOWN_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
