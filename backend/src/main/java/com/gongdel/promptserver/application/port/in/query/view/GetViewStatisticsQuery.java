package com.gongdel.promptserver.application.port.in.query.view;

import com.gongdel.promptserver.domain.model.statistics.ViewCountRange;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 조회수 통계 조회 쿼리 객체입니다.
 * 다양한 필터링 옵션을 지원합니다.
 */
@Getter
public class GetViewStatisticsQuery {
    /**
     * 시작 날짜 (null이면 전체 기간)
     */
    private final LocalDateTime startDate;

    /**
     * 종료 날짜 (null이면 전체 기간)
     */
    private final LocalDateTime endDate;

    /**
     * 카테고리 ID 목록 (빈 리스트면 전체 카테고리)
     */
    private final List<Long> categoryIds;

    /**
     * 조회할 최대 개수 (기본값: 10)
     */
    private final int limit;

    /**
     * 조회수 구간 설정 (null이면 기본 구간 사용)
     */
    private final ViewCountRange[] viewCountRanges;

    /**
     * 프롬프트 템플릿 ID 목록 (특정 프롬프트들만 조회할 때 사용)
     */
    private final List<Long> promptTemplateIds;

    @Builder
    private GetViewStatisticsQuery(LocalDateTime startDate, LocalDateTime endDate,
                                   List<Long> categoryIds, Integer limit,
                                   ViewCountRange[] viewCountRanges, List<Long> promptTemplateIds) {
        // 기간 검증
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must not be after endDate");
        }

        this.startDate = startDate;
        this.endDate = endDate;
        this.categoryIds = categoryIds != null ? categoryIds : List.of();
        this.limit = limit != null && limit > 0 ? limit : 10;
        this.viewCountRanges = viewCountRanges;
        this.promptTemplateIds = promptTemplateIds != null ? promptTemplateIds : List.of();
    }

    /**
     * 인기 프롬프트 조회용 쿼리 생성 팩토리 메서드
     */
    public static GetViewStatisticsQuery forTopPrompts(LocalDateTime startDate, LocalDateTime endDate,
                                                       List<Long> categoryIds, int limit) {
        return GetViewStatisticsQuery.builder()
            .startDate(startDate)
            .endDate(endDate)
            .categoryIds(categoryIds)
            .limit(limit)
            .build();
    }

    /**
     * 조회수 분포 조회용 쿼리 생성 팩토리 메서드
     */
    public static GetViewStatisticsQuery forDistribution(ViewCountRange[] ranges, List<Long> categoryIds) {
        return GetViewStatisticsQuery.builder()
            .viewCountRanges(ranges)
            .categoryIds(categoryIds)
            .build();
    }

    /**
     * 기간별 총 조회수 조회용 쿼리 생성 팩토리 메서드
     */
    public static GetViewStatisticsQuery forPeriodTotal(LocalDateTime startDate, LocalDateTime endDate,
                                                        List<Long> categoryIds) {
        Assert.notNull(startDate, "startDate must not be null");
        Assert.notNull(endDate, "endDate must not be null");

        return GetViewStatisticsQuery.builder()
            .startDate(startDate)
            .endDate(endDate)
            .categoryIds(categoryIds)
            .build();
    }

    /**
     * 특정 프롬프트들의 조회수 조회용 쿼리 생성 팩토리 메서드
     */
    public static GetViewStatisticsQuery forSpecificPrompts(List<Long> promptTemplateIds,
                                                            LocalDateTime startDate, LocalDateTime endDate) {
        Assert.notEmpty(promptTemplateIds, "promptTemplateIds must not be empty");

        return GetViewStatisticsQuery.builder()
            .promptTemplateIds(promptTemplateIds)
            .startDate(startDate)
            .endDate(endDate)
            .build();
    }

    /**
     * 기간 필터링이 적용되는지 확인합니다.
     */
    public boolean hasPeriodFilter() {
        return startDate != null && endDate != null;
    }

    /**
     * 카테고리 필터링이 적용되는지 확인합니다.
     */
    public boolean hasCategoryFilter() {
        return !categoryIds.isEmpty();
    }

    /**
     * 특정 프롬프트 필터링이 적용되는지 확인합니다.
     */
    public boolean hasPromptFilter() {
        return !promptTemplateIds.isEmpty();
    }
}
