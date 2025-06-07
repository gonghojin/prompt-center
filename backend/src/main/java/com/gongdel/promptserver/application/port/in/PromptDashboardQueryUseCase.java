package com.gongdel.promptserver.application.port.in;

import com.gongdel.promptserver.domain.model.PromptSearchResult;

import java.util.List;

/**
 * 대시보드 전용 최근 프롬프트 조회 유스케이스 포트입니다.
 */
public interface PromptDashboardQueryUseCase {
    /**
     * 최근 프롬프트를 조회합니다.
     *
     * @param limit 최대 개수
     * @return 최근 프롬프트 목록
     */
    List<PromptSearchResult> getRecentPrompts(int limit);
}
