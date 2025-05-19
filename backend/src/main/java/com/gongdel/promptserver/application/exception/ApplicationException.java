package com.gongdel.promptserver.application.exception;

import org.springframework.http.HttpStatus;

/**
 * 애플리케이션 계층에서 발생하는 모든 예외의 기본 클래스입니다.
 */
public abstract class ApplicationException extends RuntimeException {

    private final ApplicationErrorCode errorCode;
    private final HttpStatus httpStatus;

    /**
     * 지정된 메시지, 에러 코드, HTTP 상태 코드로 새 애플리케이션 예외를 생성합니다.
     *
     * @param message    예외 메시지
     * @param errorCode  에러 코드
     * @param httpStatus HTTP 상태 코드
     */
    protected ApplicationException(String message, ApplicationErrorCode errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    /**
     * 지정된 메시지, 원인 예외, 에러 코드, HTTP 상태 코드로 새 애플리케이션 예외를 생성합니다.
     *
     * @param message    예외 메시지
     * @param cause      원인 예외
     * @param errorCode  에러 코드
     * @param httpStatus HTTP 상태 코드
     */
    protected ApplicationException(String message, Throwable cause, ApplicationErrorCode errorCode,
            HttpStatus httpStatus) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    /**
     * 에러 코드를 반환합니다.
     *
     * @return 에러 코드
     */
    public ApplicationErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * HTTP 상태 코드를 반환합니다.
     *
     * @return HTTP 상태 코드
     */
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    /**
     * 에러 코드 이름을 반환합니다.
     *
     * @return 에러 코드 이름
     */
    public String getErrorCodeName() {
        return errorCode.name();
    }

    /**
     * 에러 코드 메시지를 반환합니다.
     *
     * @return 에러 코드 메시지
     */
    public String getErrorCodeMessage() {
        return errorCode.getMessage();
    }
}
