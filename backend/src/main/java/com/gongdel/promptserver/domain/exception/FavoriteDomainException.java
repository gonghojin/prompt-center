package com.gongdel.promptserver.domain.exception;

/**
 * 즐겨찾기 도메인 예외의 최상위 추상 클래스입니다.
 */
public abstract class FavoriteDomainException extends RuntimeException {
    private final PromptErrorType errorType;

    public FavoriteDomainException(PromptErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public FavoriteDomainException(PromptErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    public PromptErrorType getErrorType() {
        return errorType;
    }
}
