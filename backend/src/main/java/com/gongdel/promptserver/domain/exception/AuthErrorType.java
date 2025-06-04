package com.gongdel.promptserver.domain.exception;

/**
 * 인증 관련 에러 타입을 정의하는 enum입니다.
 */
public enum AuthErrorType implements ErrorCode {
    VALIDATION_ERROR(2000, "인증 입력값 검증 실패"),
    TOKEN_EXPIRED(2001, "토큰이 만료되었습니다"),
    TOKEN_INVALID(2002, "유효하지 않은 토큰입니다"),
    TOKEN_NOT_FOUND(2003, "토큰을 찾을 수 없습니다"),
    TOKEN_SAVE_FAILED(2004, "토큰 저장에 실패했습니다"),
    TOKEN_DELETE_FAILED(2005, "토큰 삭제에 실패했습니다"),
    TOKEN_BLACKLIST_FAILED(2006, "토큰 블랙리스트 추가에 실패했습니다"),
    TOKEN_ALREADY_BLACKLISTED(2007, "이미 블랙리스트에 등록된 토큰입니다"),
    LOGIN_FAILED(2008, "로그인에 실패했습니다"),
    UNAUTHORIZED(2009, "인증되지 않은 접근입니다"),
    FORBIDDEN(2010, "접근 권한이 없습니다");

    private final int code;
    private final String message;

    AuthErrorType(int code, String message) {
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
