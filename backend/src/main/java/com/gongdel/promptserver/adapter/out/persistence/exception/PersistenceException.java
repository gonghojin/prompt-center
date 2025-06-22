package com.gongdel.promptserver.adapter.out.persistence.exception;

/**
 * 데이터 영속성 작업 중 발생하는 예외 어댑터 계층 내부에서만 사용되고, 도메인 계층으로 전파되지 않습니다.
 */
public class PersistenceException extends RuntimeException {

    /**
     * 지정된 오류 메시지로 새 예외를 생성합니다.
     *
     * @param message 예외 메시지
     */
    public PersistenceException(String message) {
        super(message);
    }

    /**
     * 지정된 오류 메시지와 원인 예외로 새 예외를 생성합니다.
     *
     * @param message 예외 메시지
     * @param cause   원인 예외
     */
    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
