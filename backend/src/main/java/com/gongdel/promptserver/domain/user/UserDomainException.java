package com.gongdel.promptserver.domain.user;

// TODO : BaseException 구현으로 개선하기
/**
 * 사용자 도메인 계층에서 발생하는 예외입니다.
 */
public class UserDomainException extends RuntimeException {
    public UserDomainException(String message) {
        super(message);
    }

    public UserDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
