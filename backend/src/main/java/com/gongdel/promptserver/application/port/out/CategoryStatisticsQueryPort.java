package com.gongdel.promptserver.application.port.out;

import com.gongdel.promptserver.domain.model.statistics.CategoryPromptCount;

import java.util.List;

/**
 * 카테고리별 프롬프트 개수 집계용 포트(Repository 인터페이스)입니다.
 */
public interface CategoryStatisticsQueryPort {
    /**
     * 루트 카테고리별 프롬프트 개수 목록을 조회합니다.
     *
     * @return 루트 카테고리별 프롬프트 개수 목록
     */
    List<CategoryPromptCount> getRootCategoryPromptCounts();

    /**
     * 특정 루트의 하위 카테고리별 프롬프트 개수 목록을 조회합니다.
     *
     * @param rootId 루트 카테고리 ID
     * @return 하위 카테고리별 프롬프트 개수 목록
     */
    List<CategoryPromptCount> getChildCategoryPromptCounts(Long rootId);
}
