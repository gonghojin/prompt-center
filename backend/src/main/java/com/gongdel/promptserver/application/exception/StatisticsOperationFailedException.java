package com.gongdel.promptserver.application.exception;

import org.springframework.http.HttpStatus;

/**
 * 통계 작업(조회, 계산 등) 중 발생하는 일반적인 오류를 처리하는 애플리케이션 예외
 */
public class StatisticsOperationFailedException extends ApplicationException {

    /**
     * 지정된 오류 메시지로 새 예외를 생성합니다.
     *
     * @param message 예외 메시지
     */
    public StatisticsOperationFailedException(String message) {
        super(message,
            ApplicationErrorCode.STATISTICS_OPERATION_FAILED,
            HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 지정된 오류 메시지와 원인 예외로 새 예외를 생성합니다.
     *
     * @param message 예외 메시지
     * @param cause   원인 예외
     */
    public StatisticsOperationFailedException(String message, Throwable cause) {
        super(message,
            cause,
            ApplicationErrorCode.STATISTICS_OPERATION_FAILED,
            HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
