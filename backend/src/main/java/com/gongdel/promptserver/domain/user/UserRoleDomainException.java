package com.gongdel.promptserver.domain.user;

// TODO : BaseException 구현으로 개선하기
/**
 * 사용자-역할 도메인 예외
 * <p>
 * 사용자-역할 매핑 도메인 계층에서 발생하는 예외를 처리합니다.
 * </p>
 */
public class UserRoleDomainException extends RuntimeException {
    /**
     * 메시지 기반 예외 생성자
     *
     * @param message 예외 메시지
     */
    public UserRoleDomainException(String message) {
        super(message);
    }

    /**
     * 메시지 및 원인 기반 예외 생성자
     *
     * @param message 예외 메시지
     * @param cause   원인 예외
     */
    public UserRoleDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
