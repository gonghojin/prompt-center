package com.gongdel.promptserver.application.port.out.command;

import com.gongdel.promptserver.domain.model.PromptVersion;

/**
 * 프롬프트 버전을 업데이트하는 포트입니다.
 */
public interface UpdatePromptVersionPort {

    /**
     * 프롬프트 버전을 업데이트합니다.
     *
     * @param promptVersion 업데이트할 프롬프트 버전 도메인 객체
     * @return 업데이트된 프롬프트 버전 도메인 객체
     */
    PromptVersion updatePromptVersion(PromptVersion promptVersion);
}
