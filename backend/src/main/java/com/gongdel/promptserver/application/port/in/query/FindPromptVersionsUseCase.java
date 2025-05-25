package com.gongdel.promptserver.application.port.in.query;

import com.gongdel.promptserver.domain.model.PromptVersion;

import java.util.List;

/**
 * 프롬프트 템플릿 ID로 프롬프트 버전 목록을 조회하는 유스케이스
 */
public interface FindPromptVersionsUseCase {

    /**
     * 프롬프트 템플릿 ID로 프롬프트 버전 목록을 조회합니다.
     *
     * @param promptTemplateId 조회할 프롬프트 템플릿의 ID
     * @return 프롬프트 버전 도메인 목록
     */
    List<PromptVersion> findByPromptTemplateId(Long promptTemplateId);
}
