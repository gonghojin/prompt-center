package com.gongdel.promptserver.domain.exception;

/**
 * JWT 인증/인가 과정에서 발생하는 예외의 공통 부모 클래스입니다.
 */
public class JwtException extends BaseException {
    public JwtException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public JwtException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
