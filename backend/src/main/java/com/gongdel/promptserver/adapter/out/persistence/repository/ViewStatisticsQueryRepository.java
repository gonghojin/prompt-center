package com.gongdel.promptserver.adapter.out.persistence.repository;

import com.gongdel.promptserver.domain.model.statistics.DailyViewStatistics;
import com.gongdel.promptserver.domain.model.statistics.TopViewedPrompt;
import com.gongdel.promptserver.domain.model.statistics.ViewCountDistribution;
import com.gongdel.promptserver.domain.model.statistics.ViewCountRange;

import java.time.LocalDateTime;
import java.util.List;

/**
 * QueryDSL 기반 조회수 통계/집계 전용 Repository 인터페이스입니다.
 * <p>
 * 기존 JPA Repository의 복잡한 @Query 메서드들을 QueryDSL로 개선하여
 * 타입 안전성과 동적 쿼리 지원을 제공합니다.
 */
public interface ViewStatisticsQueryRepository {

    /**
     * 인기 프롬프트 목록을 조회합니다. (특정 기간 내, 동적 필터링 지원)
     * <p>
     * 기존 PromptViewLogJpaRepository.findTopViewedPrompts() 메서드를 개선
     *
     * @param startDate   시작 날짜
     * @param endDate     종료 날짜
     * @param categoryIds 카테고리 ID 목록 (빈 리스트면 전체)
     * @param limit       조회할 개수
     * @return 인기 프롬프트 목록
     */
    List<TopViewedPrompt> findTopViewedPromptsWithFilters(
        LocalDateTime startDate,
        LocalDateTime endDate,
        List<Long> categoryIds,
        int limit);

    /**
     * 일별 조회수 통계를 조회합니다. (동적 시간 단위 지원)
     * <p>
     * 기존 PromptViewLogJpaRepository.findDailyViewStatistics() 메서드를 개선
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @param startDate        시작 날짜
     * @param endDate          종료 날짜
     * @return 일별 조회수 통계
     */
    List<DailyViewStatistics> findDailyViewStatistics(
        Long promptTemplateId,
        LocalDateTime startDate,
        LocalDateTime endDate);

    /**
     * 조회수 분포 통계를 조회합니다. (동적 구간 설정 지원)
     * <p>
     * 기존 PromptViewCountJpaRepository.getViewCountDistribution() 메서드를 개선
     *
     * @param ranges      조회수 구간 설정 (null이면 기본 구간 사용)
     * @param categoryIds 카테고리 ID 목록 (빈 리스트면 전체)
     * @return 조회수 분포 통계
     */
    List<ViewCountDistribution> getViewCountDistributionWithFilters(
        ViewCountRange[] ranges,
        List<Long> categoryIds);

    /**
     * 특정 기간 내 조회수 합계를 조회합니다. (동적 필터링 지원)
     * <p>
     * 기존 PromptViewCountJpaRepository.getTotalViewCountByPeriod() 메서드를 개선
     *
     * @param startDate   시작 날짜
     * @param endDate     종료 날짜
     * @param categoryIds 카테고리 ID 목록 (빈 리스트면 전체)
     * @return 기간 내 조회수 합계
     */
    long getTotalViewCountByPeriodWithFilters(
        LocalDateTime startDate,
        LocalDateTime endDate,
        List<Long> categoryIds);

    /**
     * 프롬프트별 조회수를 집계합니다. (동적 필터링 지원)
     * <p>
     * 기존 PromptViewLogJpaRepository.countByPromptTemplateId() 관련 메서드들을 개선
     *
     * @param promptTemplateIds 프롬프트 템플릿 ID 목록
     * @param startDate         시작 날짜 (null이면 전체 기간)
     * @param endDate           종료 날짜 (null이면 전체 기간)
     * @return 프롬프트별 조회수 맵 (promptTemplateId -> viewCount)
     */
    List<TopViewedPrompt> getViewCountsByPromptIds(
        List<Long> promptTemplateIds,
        LocalDateTime startDate,
        LocalDateTime endDate);
}
