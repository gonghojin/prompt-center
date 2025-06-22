package com.gongdel.promptserver.application.port.out.command;

/**
 * 프롬프트 버전을 삭제하는 포트입니다.
 */
public interface DeletePromptVersionPort {

    /**
     * 프롬프트 버전을 삭제합니다.
     *
     * @param id 삭제할 프롬프트 버전의 ID
     */
    void deletePromptVersion(Long id);
}
