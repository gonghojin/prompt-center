package com.gongdel.promptserver.domain.exception;

/**
 * JWT 관련 오류 코드 정의
 */
public enum JwtErrorType implements ErrorCode {
    INVALID_JWT(4000, "Invalid JWT token"),
    EXPIRED_JWT(4001, "JWT token expired"),
    UNSUPPORTED_JWT(4002, "Unsupported JWT token"),
    MALFORMED_JWT(4003, "Malformed JWT token"),
    JWT_SIGNATURE_ERROR(4004, "Invalid JWT signature");

    private final int code;
    private final String message;

    JwtErrorType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
