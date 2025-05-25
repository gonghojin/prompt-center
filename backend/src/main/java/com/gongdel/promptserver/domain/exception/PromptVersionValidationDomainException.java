package com.gongdel.promptserver.domain.exception;

/**
 * 프롬프트 버전 유효성 검증에 실패했을 때 발생하는 도메인 예외입니다.
 */
public class PromptVersionValidationDomainException extends PromptVersionDomainException {

    public PromptVersionValidationDomainException(String message) {
        super(PromptErrorType.VERSION_VALIDATION_ERROR, message);
    }

    public PromptVersionValidationDomainException(String message, Throwable cause) {
        super(PromptErrorType.VERSION_VALIDATION_ERROR, message, cause);
    }
}
