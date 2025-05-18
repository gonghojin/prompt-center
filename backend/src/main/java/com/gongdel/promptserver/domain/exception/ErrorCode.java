package com.gongdel.promptserver.domain.exception;

/**
 * 도메인 예외에서 사용할 오류 코드 인터페이스입니다.
 * 모든 도메인별 오류 코드 열거형은 이 인터페이스를 구현해야 합니다.
 */
public interface ErrorCode {
    /**
     * 오류 코드를 반환합니다.
     *
     * @return 오류 코드
     */
    int getCode();

    /**
     * 오류 메시지를 반환합니다.
     *
     * @return 오류 메시지
     */
    String getMessage();

    /**
     * 오류 코드의 이름을 반환합니다.
     *
     * @return 오류 코드 이름
     */
    String name();
}
