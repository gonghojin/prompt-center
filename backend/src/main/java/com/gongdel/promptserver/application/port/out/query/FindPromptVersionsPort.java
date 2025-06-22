package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.model.PromptVersion;

import java.util.List;

/**
 * 프롬프트 템플릿 ID로 프롬프트 버전 목록 조회 포트
 */
public interface FindPromptVersionsPort {

    /**
     * 프롬프트 템플릿 ID로 프롬프트 버전 목록을 조회합니다.
     *
     * @param promptTemplateId 조회할 프롬프트 템플릿의 ID
     * @return 프롬프트 버전 도메인 목록
     */
    List<PromptVersion> findPromptVersionsByPromptTemplateId(Long promptTemplateId);
}
