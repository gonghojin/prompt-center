package com.gongdel.promptserver.domain.exception;

import lombok.Getter;

/**
 * 애플리케이션의 모든 비즈니스 예외의 기본 클래스입니다.
 * 모든 도메인 및 애플리케이션 예외는 이 클래스를 상속해야 합니다.
 */
@Getter
public abstract class BaseException extends RuntimeException {
    private final ErrorCode errorCode;

    /**
     * 지정된 오류 코드와 메시지로 새 예외를 생성합니다.
     *
     * @param errorCode 오류 코드
     * @param message   예외 메시지
     */
    protected BaseException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 지정된 오류 코드, 메시지, 원인 예외와 함께 새 예외를 생성합니다.
     *
     * @param errorCode 오류 코드
     * @param message   예외 메시지
     * @param cause     원인 예외
     */
    protected BaseException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
