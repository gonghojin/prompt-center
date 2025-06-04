package com.gongdel.promptserver.domain.exception;

/**
 * 인증 도메인 관련 예외의 기본 클래스입니다.
 * 토큰, 로그인, 인증 등 인증 관련 예외들의 기본 클래스로 사용됩니다.
 */
public abstract class AuthDomainException extends BaseException {

    protected AuthDomainException(AuthErrorType errorType, String message) {
        super(errorType, message);
    }

    protected AuthDomainException(AuthErrorType errorType, String message, Throwable cause) {
        super(errorType, message, cause);
    }
}
