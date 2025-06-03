package com.gongdel.promptserver.domain.exception;

import lombok.Getter;

/**
 * 토큰 작업 중 발생할 수 있는 오류 유형을 정의합니다.
 */
@Getter
public enum TokenErrorType implements ErrorCode {
    INVALID_INPUT_VALUE(2000, "잘못된 입력값입니다"),
    TOKEN_NOT_FOUND(2001, "토큰을 찾을 수 없습니다"),
    TOKEN_EXPIRED(2002, "토큰이 만료되었습니다"),
    TOKEN_ALREADY_BLACKLISTED(2003, "이미 블랙리스트에 등록된 토큰입니다"),
    TOKEN_SAVE_FAILED(2004, "토큰 저장에 실패했습니다"),
    TOKEN_DELETE_FAILED(2005, "토큰 삭제에 실패했습니다"),
    TOKEN_BLACKLIST_FAILED(2006, "토큰 블랙리스트 등록에 실패했습니다"),
    UNKNOWN_TOKEN_ERROR(2999, "알 수 없는 토큰 관련 오류가 발생했습니다");

    private final int code;
    private final String message;

    TokenErrorType(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
