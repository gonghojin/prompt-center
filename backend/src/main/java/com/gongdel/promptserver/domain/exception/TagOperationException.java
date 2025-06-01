package com.gongdel.promptserver.domain.exception;

/**
 * 태그 도메인 작업 중 발생하는 일반적인 예외입니다.
 */
public class TagOperationException extends TagDomainException {
    public TagOperationException(TagErrorType errorType, String message) {
        super(errorType, message);
    }

    public TagOperationException(TagErrorType errorType, String message, Throwable cause) {
        super(errorType, message, cause);
    }
}
