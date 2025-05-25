package com.gongdel.promptserver.application.port.in.query;

import com.gongdel.promptserver.domain.model.PromptVersion;
import java.util.UUID;

/**
 * 프롬프트 버전 UUID로 단건 조회하는 유스케이스
 */
public interface GetPromptVersionUseCase {

    /**
     * UUID로 프롬프트 버전을 조회합니다.
     *
     * @param uuid 조회할 프롬프트 버전의 UUID
     * @return 프롬프트 버전 도메인
     */
    PromptVersion getByUuid(UUID uuid);
}
