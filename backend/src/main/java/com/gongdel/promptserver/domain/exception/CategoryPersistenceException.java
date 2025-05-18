package com.gongdel.promptserver.domain.exception;

import lombok.Getter;

/**
 * 카테고리 영속성 작업 중 발생하는 예외를 처리하는 클래스입니다.
 */
public class CategoryPersistenceException extends BaseException {

    /**
     * 지정된 오류 유형과 메시지로 새 예외를 생성합니다.
     *
     * @param errorType 오류 유형
     * @param message   예외 메시지
     */
    public CategoryPersistenceException(CategoryErrorType errorType, String message) {
        super(errorType, message);
    }

    /**
     * 지정된 오류 유형, 메시지, 원인 예외와 함께 새 예외를 생성합니다.
     *
     * @param errorType 오류 유형
     * @param message   예외 메시지
     * @param cause     원인 예외
     */
    public CategoryPersistenceException(CategoryErrorType errorType, String message, Throwable cause) {
        super(errorType, message, cause);
    }
}
