package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.model.PromptVersion;

import java.util.Optional;
import java.util.UUID;

/**
 * 프롬프트 버전 단건 조회 포트
 */
public interface LoadPromptVersionPort {

    /**
     * UUID로 프롬프트 버전을 조회합니다.
     *
     * @param uuid 조회할 프롬프트 버전의 UUID
     * @return 프롬프트 버전 도메인(Optional)
     */
    Optional<PromptVersion> loadPromptVersionByUuid(UUID uuid);

    /**
     * 버전 ID로 프롬프트 버전을 조회합니다.
     *
     * @param id 버전 PK
     * @return 프롬프트 버전 도메인 Optional
     */
    Optional<PromptVersion> loadPromptVersionById(Long id);
}
