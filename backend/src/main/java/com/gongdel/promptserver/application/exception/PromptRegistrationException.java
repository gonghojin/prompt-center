package com.gongdel.promptserver.application.exception;

import com.gongdel.promptserver.domain.exception.BaseException;

/**
 * 프롬프트 등록 과정에서 발생하는 예외를 표현하는 클래스입니다. 도메인 모델의 유효성 검증 실패, 데이터 저장 실패 등의 이유로 발생할 수 있습니다.
 */
public class PromptRegistrationException extends BaseException {

    /**
     * 지정된 오류 유형과 메시지로 새 예외를 생성합니다.
     *
     * @param errorType 오류 유형
     * @param message   예외 메시지
     */
    public PromptRegistrationException(PromptErrorType errorType, String message) {
        super(errorType, message);
    }

    /**
     * 지정된 오류 유형, 메시지, 원인 예외와 함께 새 예외를 생성합니다.
     *
     * @param errorType 오류 유형
     * @param message   예외 메시지
     * @param cause     원인 예외
     */
    public PromptRegistrationException(PromptErrorType errorType, String message, Throwable cause) {
        super(errorType, message, cause);
    }
}
