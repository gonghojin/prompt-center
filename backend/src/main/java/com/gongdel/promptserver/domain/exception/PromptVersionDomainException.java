package com.gongdel.promptserver.domain.exception;

/**
 * 프롬프트 버전 도메인 계층의 기본 예외 클래스입니다.
 */
public abstract class PromptVersionDomainException extends RuntimeException {

    private final PromptErrorType errorType;

    protected PromptVersionDomainException(PromptErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    protected PromptVersionDomainException(PromptErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    public PromptErrorType getErrorType() {
        return errorType;
    }
}
