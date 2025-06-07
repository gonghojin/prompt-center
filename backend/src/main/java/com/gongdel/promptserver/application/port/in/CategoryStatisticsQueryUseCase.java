package com.gongdel.promptserver.application.port.in;

import com.gongdel.promptserver.domain.model.statistics.CategoryPromptCount;

import java.util.List;

/**
 * 카테고리별 프롬프트 통계 조회 유즈케이스 포트입니다.
 */
public interface CategoryStatisticsQueryUseCase {
    /**
     * 루트 카테고리별 프롬프트 현황을 조회합니다.
     *
     * @return 루트 카테고리별 프롬프트 통계 응답
     */
    List<CategoryPromptCount> getRootCategoryPromptCounts();

    /**
     * 특정 루트의 하위 카테고리별 프롬프트 현황을 조회합니다.
     *
     * @param rootId 루트 카테고리 ID
     * @return 하위 카테고리별 프롬프트 통계 응답
     */
    List<CategoryPromptCount> getChildCategoryPromptCounts(Long rootId);
}
