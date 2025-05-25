package com.gongdel.promptserver.domain.exception;

import lombok.Getter;

/**
 * 카테고리 도메인에서 발생할 수 있는 비즈니스 오류 유형을 정의합니다.
 * 기술적 세부사항은 포함하지 않고 순수한 도메인 관점의 오류만 포함합니다.
 */
@Getter
public enum CategoryErrorType implements ErrorCode {
    NOT_FOUND(2000, "카테고리를 찾을 수 없습니다"),
    DUPLICATE_NAME(2001, "동일한 이름의 카테고리가 이미 존재합니다"),
    INVALID_CATEGORY(2002, "카테고리 데이터가 유효하지 않습니다"),
    CIRCULAR_REFERENCE(2003, "카테고리에 순환 참조가 발생했습니다"),
    OPERATION_FAILED(2004, "카테고리 작업을 수행할 수 없습니다"),
    UNKNOWN_ERROR(2999, "알 수 없는 오류가 발생했습니다");

    private final int code;
    private final String message;

    CategoryErrorType(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
