package com.gongdel.promptserver.domain.exception;

/**
 * 프롬프트 좋아요 관련 오류 코드 정의
 */
public enum LikeErrorType implements ErrorCode {
    INTERNAL_SERVER_ERROR(1500, "Internal server error"),
    LIKE_NOT_FOUND(1501, "Like not found"),
    DUPLICATE_LIKE(1502, "Like already exists");

    private final int code;
    private final String message;

    LikeErrorType(int code, String message) {
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
