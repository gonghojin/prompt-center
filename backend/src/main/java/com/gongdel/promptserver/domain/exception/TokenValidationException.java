package com.gongdel.promptserver.domain.exception;

/**
 * 토큰 관련 입력값 검증 실패 시 발생하는 도메인 예외입니다.
 */
public class TokenValidationException extends TokenException {

    public TokenValidationException(String message) {
        super(AuthErrorType.VALIDATION_ERROR, message);
    }

    public TokenValidationException(String message, Throwable cause) {
        super(AuthErrorType.VALIDATION_ERROR, message, cause);
    }

    public static TokenValidationException nullRefreshToken() {
        return new TokenValidationException("리프레시 토큰은 null일 수 없습니다.");
    }

    public static TokenValidationException nullUserId() {
        return new TokenValidationException("사용자 ID는 null일 수 없습니다.");
    }

    public static TokenValidationException emptyToken() {
        return new TokenValidationException("토큰은 비어있을 수 없습니다.");
    }

    public static TokenValidationException nullExpiresAt() {
        return new TokenValidationException("만료 시간은 null일 수 없습니다.");
    }

    public static TokenValidationException invalidTokenFormat() {
        return new TokenValidationException("토큰 형식이 올바르지 않습니다.");
    }

    public static TokenValidationException invalidExpirationTime() {
        return new TokenValidationException("만료 시간이 올바르지 않습니다.");
    }
}
