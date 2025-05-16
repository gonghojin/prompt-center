package com.gongdel.promptserver.domain.model;

/**
 * 프롬프트 템플릿의 유효성 검증 실패 시 발생하는 예외
 */
public class PromptValidationException extends Exception {

    /**
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public PromptValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message 예외 메시지
     */
    public PromptValidationException(String message) {
        super(message);
    }
}
