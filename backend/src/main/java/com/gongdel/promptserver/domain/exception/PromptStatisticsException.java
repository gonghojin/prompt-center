package com.gongdel.promptserver.domain.exception;

/**
 * 프롬프트 통계 관련 예외를 처리하는 클래스입니다.
 */
public class PromptStatisticsException extends PromptStatisticsDomainException {

    public PromptStatisticsException(PromptStatisticsErrorType errorType, String message) {
        super((ErrorCode) errorType, message);
    }

    public PromptStatisticsException(PromptStatisticsErrorType errorType, String message, Throwable cause) {
        super((ErrorCode) errorType, message, cause);
    }

    /**
     * 잘못된 기간 파라미터 예외 생성
     */
    public static PromptStatisticsException invalidPeriod() {
        return new PromptStatisticsException(
            PromptStatisticsErrorType.INVALID_PERIOD,
            "유효하지 않은 기간 파라미터입니다.");
    }

    /**
     * DB 접근 오류 예외 생성
     */
    public static PromptStatisticsException databaseError(Throwable cause) {
        return new PromptStatisticsException(
            PromptStatisticsErrorType.DATABASE_ERROR,
            "프롬프트 통계 DB 접근 중 오류가 발생했습니다.",
            cause);
    }

    /**
     * 일반 오류 예외 생성
     */
    public static PromptStatisticsException general(String message) {
        return new PromptStatisticsException(
            PromptStatisticsErrorType.GENERAL,
            message);
    }
}
