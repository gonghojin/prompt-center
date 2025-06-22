package com.gongdel.promptserver.domain.view;

import com.gongdel.promptserver.domain.exception.BaseException;

/**
 * 조회수 도메인 관련 예외의 기본 클래스입니다.
 * 조회수 기록, 통계, 캐싱 등 조회수 관련 예외들의 기본 클래스로 사용됩니다.
 */
public abstract class ViewDomainException extends BaseException {

    /**
     * 지정된 오류 타입과 메시지로 새 예외를 생성합니다.
     *
     * @param errorType 오류 타입
     * @param message   예외 메시지
     */
    protected ViewDomainException(ViewErrorType errorType, String message) {
        super(errorType, message);
    }

    /**
     * 지정된 오류 타입, 메시지, 원인 예외와 함께 새 예외를 생성합니다.
     *
     * @param errorType 오류 타입
     * @param message   예외 메시지
     * @param cause     원인 예외
     */
    protected ViewDomainException(ViewErrorType errorType, String message, Throwable cause) {
        super(errorType, message, cause);
    }
}
