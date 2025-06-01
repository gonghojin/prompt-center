package com.gongdel.promptserver.domain.exception;

/**
 * 태그 도메인 예외의 최상위 추상 클래스입니다.
 * 도메인 계층에서 발생하는 태그 관련 예외의 공통 부모입니다.
 */
public abstract class TagDomainException extends BaseException {
    public TagDomainException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public TagDomainException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
