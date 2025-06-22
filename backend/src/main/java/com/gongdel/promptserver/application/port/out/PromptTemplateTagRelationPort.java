package com.gongdel.promptserver.application.port.out;

import com.gongdel.promptserver.domain.model.PromptTemplate;
import com.gongdel.promptserver.domain.model.Tag;

import java.util.Set;

/**
 * 프롬프트 템플릿과 태그 간의 관계를 관리하는 포트 인터페이스
 */
public interface PromptTemplateTagRelationPort {

    /**
     * 프롬프트 템플릿에 태그를 연결합니다.
     *
     * @param promptTemplate 태그를 연결할 프롬프트 템플릿
     * @param tags           연결할 태그 목록
     * @return 태그가 연결된 프롬프트 템플릿
     */
    PromptTemplate connectTagsToPrompt(PromptTemplate promptTemplate, Set<Tag> tags);

    /**
     * 프롬프트의 태그를 새 태그 세트로 덮어씁니다(기존 태그 관계 모두 삭제 후 새 태그 연결).
     *
     * @param promptTemplate 프롬프트 템플릿
     * @param newTags        새로 연결할 태그 세트
     * @return 태그가 갱신된 프롬프트 템플릿
     */
    PromptTemplate updateTagsOfPrompt(PromptTemplate promptTemplate, Set<Tag> newTags);

    /**
     * 프롬프트 ID로 연결된 태그 목록을 조회합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 태그 세트
     */
    Set<Tag> findTagsByPromptTemplateId(Long promptTemplateId);
}
