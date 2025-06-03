package com.gongdel.promptserver.domain.team;

// TODO : BaseException 구현으로 개선하기
/**
 * 팀 도메인 계층에서 발생하는 예외입니다.
 */
public class TeamDomainException extends RuntimeException {
    public TeamDomainException(String message) {
        super(message);
    }

    public TeamDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
