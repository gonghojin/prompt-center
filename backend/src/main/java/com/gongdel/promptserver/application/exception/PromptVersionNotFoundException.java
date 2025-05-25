package com.gongdel.promptserver.application.exception;

import com.gongdel.promptserver.domain.exception.PromptVersionNotFoundDomainException;
import org.springframework.http.HttpStatus;

/**
 * 프롬프트 버전을 찾을 수 없을 때 발생하는 애플리케이션 계층의 예외입니다.
 */
public class PromptVersionNotFoundException extends ApplicationException {

    private final PromptVersionNotFoundDomainException domainException;

    /**
     * ID로 프롬프트 버전을 찾을 수 없을 때 예외를 생성합니다.
     *
     * @param versionId 찾을 수 없는 프롬프트 버전의 ID
     */
    public PromptVersionNotFoundException(Long versionId) {
        super(String.format("Prompt version not found. ID: %d", versionId),
            ApplicationErrorCode.PROMPT_VERSION_NOT_FOUND,
            HttpStatus.NOT_FOUND);
        this.domainException = new PromptVersionNotFoundDomainException(versionId);
    }

    /**
     * UUID로 프롬프트 버전을 찾을 수 없을 때 예외를 생성합니다.
     *
     * @param uuid 찾을 수 없는 프롬프트 버전의 UUID
     */
    public PromptVersionNotFoundException(java.util.UUID uuid) {
        super(String.format("Prompt version not found. UUID: %s", uuid),
            ApplicationErrorCode.PROMPT_VERSION_NOT_FOUND,
            HttpStatus.NOT_FOUND);
        this.domainException = new PromptVersionNotFoundDomainException(uuid);
    }

}
