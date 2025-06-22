package com.gongdel.promptserver.application.port.out;

import com.gongdel.promptserver.domain.model.PromptTemplate;

/**
 * 프롬프트 저장을 위한 포트 인터페이스
 */
public interface SavePromptPort {

    /**
     * 프롬프트를 저장합니다.
     *
     * @param promptTemplate 저장할 프롬프트
     * @return 저장된 프롬프트
     */
    PromptTemplate savePrompt(PromptTemplate promptTemplate);
}
