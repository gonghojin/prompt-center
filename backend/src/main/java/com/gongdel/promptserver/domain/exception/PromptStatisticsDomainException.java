package com.gongdel.promptserver.domain.exception;

/**
 * 프롬프트 통계 도메인 관련 예외의 기본 클래스입니다.
 * 통계 조회, 집계 등 프롬프트 통계 관련 예외들의 기본 클래스로 사용됩니다.
 */
public abstract class PromptStatisticsDomainException extends BaseException {

    protected PromptStatisticsDomainException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    protected PromptStatisticsDomainException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
