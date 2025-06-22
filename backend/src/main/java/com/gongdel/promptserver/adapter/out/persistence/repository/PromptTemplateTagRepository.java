package com.gongdel.promptserver.adapter.out.persistence.repository;

import com.gongdel.promptserver.adapter.out.persistence.entity.PromptTemplateEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.PromptTemplateTagEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 프롬프트 템플릿과 태그 간의 연결을 관리하는 레포지토리
 */
@Repository
public interface PromptTemplateTagRepository extends JpaRepository<PromptTemplateTagEntity, Long> {

    /**
     * 프롬프트 템플릿과 태그가 연결되어 있는지 확인합니다.
     *
     * @param promptTemplate 프롬프트 템플릿
     * @param tag            태그
     * @return 연결 여부
     */
    boolean existsByPromptTemplateAndTag(PromptTemplateEntity promptTemplate, TagEntity tag);

    /**
     * 프롬프트 템플릿과 연결된 모든 태그 관계를 삭제합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     */
    void deleteAllByPromptTemplateId(Long promptTemplateId);

    /**
     * 프롬프트 템플릿과 연결된 모든 태그 관계를 조회합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 태그 관계 엔티티 리스트
     */
    List<PromptTemplateTagEntity> findAllByPromptTemplateId(Long promptTemplateId);
}
