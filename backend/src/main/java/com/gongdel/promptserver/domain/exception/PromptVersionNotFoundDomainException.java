package com.gongdel.promptserver.domain.exception;

/**
 * 프롬프트 버전을 찾을 수 없을 때 발생하는 도메인 예외입니다.
 */
public class PromptVersionNotFoundDomainException extends PromptVersionDomainException {

    /**
     * ID로 프롬프트 버전을 찾을 수 없을 때 발생하는 예외를 생성합니다.
     *
     * @param id 찾을 수 없는 프롬프트 버전 ID
     */
    public PromptVersionNotFoundDomainException(Long id) {
        super(PromptErrorType.VERSION_NOT_FOUND, "Prompt version with ID " + id + " cannot be found.");
    }

    /**
     * UUID로 프롬프트 버전을 찾을 수 없을 때 발생하는 예외를 생성합니다.
     *
     * @param uuid 찾을 수 없는 프롬프트 버전 UUID
     */
    public PromptVersionNotFoundDomainException(java.util.UUID uuid) {
        super(PromptErrorType.VERSION_NOT_FOUND, "Prompt version with UUID '" + uuid + "' cannot be found.");
    }
}
