package com.gongdel.promptserver.domain.exception;

/**
 * 인증 관련 예외를 처리하는 클래스입니다.
 */
public class AuthException extends AuthDomainException {

    public AuthException(AuthErrorType errorType, String message) {
        super(errorType, message);
    }

    public AuthException(AuthErrorType errorType, String message, Throwable cause) {
        super(errorType, message, cause);
    }

    public static AuthException invalidCredentials() {
        return new AuthException(
                AuthErrorType.LOGIN_FAILED,
                "이메일 또는 비밀번호가 올바르지 않습니다");
    }

    public static AuthException invalidRefreshToken() {
        return new AuthException(
                AuthErrorType.TOKEN_INVALID,
                "유효하지 않은 리프레시 토큰입니다");
    }

    public static AuthException invalidAccessToken() {
        return new AuthException(
                AuthErrorType.TOKEN_INVALID,
                "유효하지 않은 액세스 토큰입니다");
    }

    public static AuthException userNotFound(String userId) {
        return new AuthException(
                AuthErrorType.UNAUTHORIZED,
                String.format("사용자를 찾을 수 없습니다: %s", userId));
    }

    public static AuthException duplicateEmail(String email) {
        return new AuthException(
                AuthErrorType.VALIDATION_ERROR,
                String.format("이미 사용 중인 이메일입니다: %s", email));
    }
}
