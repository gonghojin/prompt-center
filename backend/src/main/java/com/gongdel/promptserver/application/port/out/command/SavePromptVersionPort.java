package com.gongdel.promptserver.application.port.out.command;

import com.gongdel.promptserver.domain.model.PromptVersion;

/**
 * 프롬프트 버전을 저장하는 포트입니다.
 */
public interface SavePromptVersionPort {

    /**
     * 프롬프트 버전을 저장합니다.
     *
     * @param promptVersion 저장할 프롬프트 버전 도메인 객체
     * @return 저장된 프롬프트 버전 도메인 객체
     */
    PromptVersion savePromptVersion(PromptVersion promptVersion);
}
