package com.gongdel.promptserver.adapter.in.rest.advice;

import com.gongdel.promptserver.application.exception.ApplicationErrorCode;
import com.gongdel.promptserver.application.exception.PromptRegistrationException;
import com.gongdel.promptserver.domain.exception.BaseException;
import com.gongdel.promptserver.domain.exception.CategoryPersistenceException;
import com.gongdel.promptserver.domain.exception.CategoryErrorType;
import com.gongdel.promptserver.domain.exception.PromptErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 애플리케이션 전역에서 발생하는 예외를 처리하는 Advice 클래스입니다.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 기본 예외 처리 핸들러
     *
     * @param ex 발생한 예외
     * @return 에러 응답
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Map<String, Object>> handleBaseException(BaseException ex) {
        log.error("Base exception occurred: {}. Error code: {}", ex.getMessage(), ex.getErrorCode(), ex);

        Map<String, Object> errorResponse = createErrorResponse(
                ex.getErrorCode().getClass().getSimpleName(),
                ex.getMessage(),
                ex.getErrorCode().name(),
                ex.getErrorCode().getMessage());

        return new ResponseEntity<>(errorResponse, determineHttpStatus(ex));
    }

    /**
     * 프롬프트 등록 관련 예외를 처리합니다.
     *
     * @param ex 발생한 예외
     * @return 에러 응답
     */
    @ExceptionHandler(PromptRegistrationException.class)
    public ResponseEntity<Map<String, Object>> handlePromptRegistrationException(PromptRegistrationException ex) {
        log.error("Prompt registration exception occurred: {}. Error code: {}", ex.getMessage(), ex.getErrorCode(), ex);

        Map<String, Object> errorResponse = createErrorResponse(
                "PROMPT_REGISTRATION_ERROR",
                ex.getMessage(),
                ex.getErrorCode().name(),
                ex.getErrorCode().getMessage());

        return new ResponseEntity<>(errorResponse, determineHttpStatus(ex));
    }

    /**
     * 카테고리 관련 예외를 처리합니다.
     *
     * @param ex 발생한 예외
     * @return 에러 응답
     */
    @ExceptionHandler(CategoryPersistenceException.class)
    public ResponseEntity<Map<String, Object>> handleCategoryPersistenceException(CategoryPersistenceException ex) {
        log.error("Category exception occurred: {}. Error code: {}", ex.getMessage(), ex.getErrorCode(), ex);

        Map<String, Object> errorResponse = createErrorResponse(
                "CATEGORY_ERROR",
                ex.getMessage(),
                ex.getErrorCode().name(),
                ex.getErrorCode().getMessage());

        return new ResponseEntity<>(errorResponse, determineHttpStatus(ex));
    }

    /**
     * 일반 예외를 처리합니다.
     *
     * @param ex 발생한 예외
     * @return 에러 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);

        Map<String, Object> errorResponse = createErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
                "INTERNAL_ERROR",
                "서버 내부 오류");

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 오류 응답 기본 구조를 생성합니다.
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
     * 예외에 따른 HTTP 상태 코드를 결정합니다.
     *
     * @param ex 발생한 예외
     * @return HTTP 상태 코드
     */
    private HttpStatus determineHttpStatus(BaseException ex) {
        int errorCode = ex.getErrorCode().getCode();

        // 애플리케이션 오류 코드 처리
        if (ex.getErrorCode() instanceof ApplicationErrorCode) {
            if (errorCode == ApplicationErrorCode.PROMPT_VALIDATION_ERROR.getCode()) {
                return HttpStatus.BAD_REQUEST;
            } else if (errorCode == ApplicationErrorCode.PROMPT_INSUFFICIENT_PERMISSION.getCode()) {
                return HttpStatus.FORBIDDEN;
            }
        }

        // 카테고리 오류 코드 처리
        if (ex.getErrorCode() instanceof CategoryErrorType) {
            if (errorCode == CategoryErrorType.NOT_FOUND.getCode()) {
                return HttpStatus.NOT_FOUND;
            } else if (errorCode == CategoryErrorType.VALIDATION_ERROR.getCode()) {
                return HttpStatus.BAD_REQUEST;
            } else if (errorCode == CategoryErrorType.DUPLICATE_NAME.getCode()) {
                return HttpStatus.CONFLICT;
            }
        }

        // 프롬프트 오류 코드 처리
        if (ex.getErrorCode() instanceof PromptErrorType) {
            if (errorCode == PromptErrorType.NOT_FOUND.getCode()) {
                return HttpStatus.NOT_FOUND;
            } else if (errorCode == PromptErrorType.VALIDATION_ERROR.getCode()) {
                return HttpStatus.BAD_REQUEST;
            } else if (errorCode == PromptErrorType.INSUFFICIENT_PERMISSION.getCode()) {
                return HttpStatus.FORBIDDEN;
            } else if (errorCode == PromptErrorType.DUPLICATE_TITLE.getCode()) {
                return HttpStatus.CONFLICT;
            }
        }

        // 기본 상태 코드
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
