package com.gongdel.promptserver.domain.exception;

/**
 * 유효하지 않은 JWT 토큰일 때 발생하는 예외입니다.
 */
public class InvalidJwtException extends JwtException {
    public InvalidJwtException(String message) {
        super(JwtErrorType.INVALID_JWT, message);
    }

    public InvalidJwtException(String message, Throwable cause) {
        super(JwtErrorType.INVALID_JWT, message, cause);
    }
}
