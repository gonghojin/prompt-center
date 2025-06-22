package com.gongdel.promptserver.application.port.out.query;

import java.util.Optional;
import java.util.UUID;

/**
 * 프롬프트 템플릿 UUID로 PK(Long id)를 조회하는 쿼리 포트입니다.
 */
public interface LoadPromptTemplateIdPort {
    /**
     * 프롬프트 템플릿 UUID로 PK(Long id)를 조회합니다.
     *
     * @param uuid 프롬프트 템플릿 UUID
     * @return PK(Long id) Optional
     */
    Optional<Long> findIdByUuid(UUID uuid);
}
