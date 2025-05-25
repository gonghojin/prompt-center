package com.gongdel.promptserver.application.port.in;

import com.gongdel.promptserver.domain.model.PromptTemplate;
import com.gongdel.promptserver.domain.model.Tag;

import java.util.Set;

/**
 * 프롬프트 템플릿과 태그 연결을 관리하는 유스케이스 인터페이스
 */
public interface PromptTagUseCase {

    /**
     * 프롬프트 템플릿에 태그를 연결합니다.
     *
     * @param promptTemplate 태그를 연결할 프롬프트 템플릿
     * @param tags           연결할 태그 목록
     * @return 태그가 연결된 프롬프트 템플릿
     */
    PromptTemplate connectTags(PromptTemplate promptTemplate, Set<Tag> tags);

    /**
     * 프롬프트 템플릿에서 특정 태그를 제거합니다.
     *
     * @param promptTemplate 태그를 제거할 프롬프트 템플릿
     * @param tag            제거할 태그
     * @return 태그가 제거된 프롬프트 템플릿
     */
    PromptTemplate removeTag(PromptTemplate promptTemplate, Tag tag);

    /**
     * 프롬프트 템플릿에서 모든 태그를 제거합니다.
     *
     * @param promptTemplate 태그를 모두 제거할 프롬프트 템플릿
     * @return 태그가 모두 제거된 프롬프트 템플릿
     */
    PromptTemplate clearTags(PromptTemplate promptTemplate);
}
