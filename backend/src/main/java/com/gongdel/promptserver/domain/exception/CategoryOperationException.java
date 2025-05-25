package com.gongdel.promptserver.domain.exception;

/**
 * 카테고리 작업(생성, 수정, 삭제 등) 중 발생하는 일반적인 오류를 처리하는 클래스입니다.
 */
public class CategoryOperationException extends CategoryDomainException {

    /**
     * 지정된 오류 유형과 메시지로 새 예외를 생성합니다.
     *
     * @param errorType 오류 유형
     * @param message   예외 메시지
     */
    public CategoryOperationException(CategoryErrorType errorType, String message) {
        super(errorType, message);
    }

    /**
     * 지정된 오류 유형, 메시지, 원인 예외와 함께 새 예외를 생성합니다.
     *
     * @param errorType 오류 유형
     * @param message   예외 메시지
     * @param cause     원인 예외
     */
    public CategoryOperationException(CategoryErrorType errorType, String message, Throwable cause) {
        super(errorType, message, cause);
    }
}
