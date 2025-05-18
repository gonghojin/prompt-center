package com.gongdel.promptserver.domain.exception;

import lombok.Getter;

/**
 * 카테고리 작업 중 발생할 수 있는 오류 유형을 정의합니다.
 */
@Getter
public enum CategoryErrorType implements ErrorCode {
    NOT_FOUND(2000, "카테고리를 찾을 수 없습니다"),
    DUPLICATE_NAME(2001, "동일한 이름의 카테고리가 이미 존재합니다"),
    VALIDATION_ERROR(2002, "카테고리 데이터가 유효하지 않습니다"),
    PERSISTENCE_ERROR(2003, "카테고리 저장 중 오류가 발생했습니다"),
    DELETION_ERROR(2004, "카테고리 삭제 중 오류가 발생했습니다"),
    QUERY_ERROR(2005, "카테고리 조회 중 오류가 발생했습니다"),
    UNKNOWN_ERROR(2999, "알 수 없는 오류가 발생했습니다");

    private final int code;
    private final String message;

    CategoryErrorType(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
