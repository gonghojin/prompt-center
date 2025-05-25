package com.gongdel.promptserver.application.exception;

import org.springframework.http.HttpStatus;

/**
 * 프롬프트 버전 작업(생성, 수정, 삭제, 조회 등) 중 예기치 못한 오류가 발생했을 때 사용하는 애플리케이션 계층의 예외입니다.
 * <p>
 * 주로 도메인 계층 이외의 예외나 알 수 없는 시스템 오류를 감싸서 처리할 때 사용합니다.
 */
public class PromptVersionOperationFailedException extends ApplicationException {

    /**
     * 메시지로 예외를 생성합니다.
     *
     * @param message 예외 메시지
     */
    public PromptVersionOperationFailedException(String message) {
        super(message,
                ApplicationErrorCode.PROMPT_VERSION_OPERATION_FAILED,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 메시지와 원인 예외로 예외를 생성합니다.
     *
     * @param message 예외 메시지
     * @param cause   원인 예외
     */
    public PromptVersionOperationFailedException(String message, Throwable cause) {
        super(message,
                cause,
                ApplicationErrorCode.PROMPT_VERSION_OPERATION_FAILED,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
