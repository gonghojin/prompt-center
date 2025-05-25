package com.gongdel.promptserver.domain.exception;

/**
 * 카테고리 도메인에서 발생하는 모든 예외의 기본 클래스입니다.
 */
public abstract class CategoryDomainException extends RuntimeException {

    private final CategoryErrorType errorType;

    /**
     * 카테고리 도메인 예외를 생성합니다.
     *
     * @param errorType 에러 유형
     * @param message   에러 메시지
     */
    protected CategoryDomainException(CategoryErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    /**
     * 카테고리 도메인 예외를 생성합니다.
     *
     * @param errorType 에러 유형
     * @param message   에러 메시지
     * @param cause     원인 예외
     */
    protected CategoryDomainException(CategoryErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    /**
     * 에러 유형을 반환합니다.
     *
     * @return 에러 유형
     */
    public CategoryErrorType getErrorType() {
        return errorType;
    }
}
