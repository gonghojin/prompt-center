package com.gongdel.promptserver.adapter.out.persistence.repository;

import com.gongdel.promptserver.adapter.out.persistence.entity.PromptVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PromptVersionRepository extends JpaRepository<PromptVersionEntity, Long> {

    /**
     * UUID로 프롬프트 버전 엔티티를 조회합니다.
     *
     * @param uuid 조회할 프롬프트 버전의 UUID
     * @return 해당 UUID를 가진 프롬프트 버전 엔티티(Optional)
     */
    Optional<PromptVersionEntity> findByUuid(UUID uuid);

    /**
     * 프롬프트 템플릿 ID로 해당 템플릿에 속한 모든 프롬프트 버전 엔티티를 조회합니다.
     *
     * @param promptTemplateId 조회할 프롬프트 템플릿의 ID
     * @return 해당 템플릿에 속한 프롬프트 버전 엔티티 목록
     */
    List<PromptVersionEntity> findAllByPromptTemplateId(Long promptTemplateId);
}
