package com.gongdel.promptserver.domain.exception;

import lombok.Getter;

/**
 * 프롬프트 통계 관련 에러 유형을 정의합니다.
 */
@Getter
public enum PromptStatisticsErrorType implements ErrorCode {
    GENERAL(3000, "일반 통계 오류"),
    INVALID_PERIOD(3001, "잘못된 기간 파라미터"),
    DATABASE_ERROR(3002, "DB 접근 오류");

    private final int code;
    private final String message;

    PromptStatisticsErrorType(int code, String message) {
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
