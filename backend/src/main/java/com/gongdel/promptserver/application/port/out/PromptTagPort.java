package com.gongdel.promptserver.application.port.out;

import com.gongdel.promptserver.domain.model.PromptTemplate;

import java.util.Set;

/**
 * 프롬프트 템플릿과 태그 연결을 위한 아웃바운드 포트 인터페이스
 */
public interface PromptTagPort {

    /**
     * 프롬프트 템플릿에 태그를 연결합니다.
     *
     * @param promptId 프롬프트 템플릿 ID
     * @param tagIds   연결할 태그 ID 목록
     * @return 업데이트된 프롬프트 템플릿
     */
    PromptTemplate connectTags(Long promptId, Set<Long> tagIds);

    /**
     * 태그 이름으로 프롬프트 템플릿에 태그를 연결합니다.
     *
     * @param promptId 프롬프트 템플릿 ID
     * @param tagNames 연결할 태그 이름 목록
     * @return 업데이트된 프롬프트 템플릿
     */
    PromptTemplate connectTagsByName(Long promptId, Set<String> tagNames);

    /**
     * 프롬프트 템플릿에서 태그를 제거합니다.
     *
     * @param promptId 프롬프트 템플릿 ID
     * @param tagId    제거할 태그 ID
     * @return 업데이트된 프롬프트 템플릿
     */
    PromptTemplate removeTag(Long promptId, Long tagId);

    /**
     * 프롬프트 템플릿에서 모든 태그를 제거합니다.
     *
     * @param promptId 프롬프트 템플릿 ID
     * @return 업데이트된 프롬프트 템플릿
     */
    PromptTemplate clearTags(Long promptId);
}
