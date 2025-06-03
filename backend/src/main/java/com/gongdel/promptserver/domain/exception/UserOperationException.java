package com.gongdel.promptserver.domain.exception;

import com.gongdel.promptserver.domain.user.UserDomainException;

/**
 * 사용자 작업(생성, 수정, 삭제 등) 중 발생하는 일반적인 오류를 처리하는 예외입니다.
 *
 * <p>
 * 이 예외는 사용자 관련 도메인 계층에서 발생하는 일반적인 작업 실패(예: 저장, 수정, 삭제 등)에 사용됩니다.
 * </p>
 */
public class UserOperationException extends UserDomainException {

    /**
     * 지정된 오류 메시지로 새 예외를 생성합니다.
     *
     * @param message 예외 메시지
     */
    public UserOperationException(String message) {
        super(message);
    }

    /**
     * 지정된 메시지와 원인 예외와 함께 새 예외를 생성합니다.
     *
     * @param message 예외 메시지
     * @param cause   원인 예외
     */
    public UserOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
