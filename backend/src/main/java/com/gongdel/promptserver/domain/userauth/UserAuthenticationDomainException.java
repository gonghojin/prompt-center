package com.gongdel.promptserver.domain.userauth;

// TODO : BaseException 구현으로 개선하기
/**
 * 사용자 인증 도메인 예외
 */
public class UserAuthenticationDomainException extends RuntimeException {
    public UserAuthenticationDomainException(String message) {
        super(message);
    }

    public UserAuthenticationDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
