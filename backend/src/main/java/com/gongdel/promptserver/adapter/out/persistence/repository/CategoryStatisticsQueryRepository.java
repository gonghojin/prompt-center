package com.gongdel.promptserver.adapter.out.persistence.repository;

import com.gongdel.promptserver.domain.model.statistics.CategoryPromptCount;

import java.util.List;

/**
 * QueryDSL 기반 카테고리별 프롬프트 개수 집계용 통계/집계 전용 Repository 인터페이스입니다.
 */
public interface CategoryStatisticsQueryRepository {
    /**
     * 루트 카테고리별 프롬프트 개수 목록을 집계합니다.
     *
     * @return 루트 카테고리별 프롬프트 개수 목록
     */
    List<CategoryPromptCount> findRootCategoryPromptCounts();

    /**
     * 특정 루트의 하위 카테고리별 프롬프트 개수 목록을 집계합니다.
     *
     * @param rootId 루트 카테고리 ID
     * @return 하위 카테고리별 프롬프트 개수 목록
     */
    List<CategoryPromptCount> findChildCategoryPromptCounts(Long rootId);
}
