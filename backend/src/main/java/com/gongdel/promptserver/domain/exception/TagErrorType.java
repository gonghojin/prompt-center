package com.gongdel.promptserver.domain.exception;

/**
 * 태그 도메인 예외의 에러 타입을 정의합니다.
 */
public enum TagErrorType implements ErrorCode {
    NOT_FOUND(4001, "Tag not found"),
    DUPLICATE_NAME(4002, "Tag name already exists"),
    PERSISTENCE_ERROR(4003, "Tag persistence error"),
    OPERATION_ERROR(4004, "Tag operation error");

    private final int code;
    private final String message;

    TagErrorType(int code, String message) {
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
