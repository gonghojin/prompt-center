package com.gongdel.promptserver.domain.exception;

/**
 * 데이터 영속성 작업 중 발생하는 예외
 */
public class PersistenceException extends BaseException {

    /**
     * 지정된 오류 메시지로 새 예외를 생성합니다.
     *
     * @param message 예외 메시지
     */
    public PersistenceException(String message) {
        super(PromptErrorType.PERSISTENCE_ERROR, message);
    }

    /**
     * 지정된 오류 메시지와 원인 예외로 새 예외를 생성합니다.
     *
     * @param message 예외 메시지
     * @param cause   원인 예외
     */
    public PersistenceException(String message, Throwable cause) {
        super(PromptErrorType.PERSISTENCE_ERROR, message, cause);
    }
}
