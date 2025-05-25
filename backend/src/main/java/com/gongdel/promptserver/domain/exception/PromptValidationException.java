package com.gongdel.promptserver.domain.exception;

/**
 * 프롬프트 템플릿의 유효성 검증 실패 시 발생하는 예외
 */
public class PromptValidationException extends BaseException {

    /**
     * 지정된 오류 메시지로 새 예외를 생성합니다.
     *
     * @param message 예외 메시지
     */
    public PromptValidationException(String message) {
        super(PromptErrorType.VALIDATION_ERROR, message);
    }

    /**
     * 지정된 오류 메시지와 원인 예외로 새 예외를 생성합니다.
     *
     * @param message 예외 메시지
     * @param cause   원인 예외
     */
    public PromptValidationException(String message, Throwable cause) {
        super(PromptErrorType.VALIDATION_ERROR, message, cause);
    }
}
