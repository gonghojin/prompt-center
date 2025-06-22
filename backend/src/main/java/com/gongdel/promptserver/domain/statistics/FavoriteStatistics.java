package com.gongdel.promptserver.domain.statistics;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * 즐겨찾기 통계 정보를 담는 도메인 객체입니다.
 * <p>
 * 총 즐겨찾기 개수, 비교 기간, 비교 결과를 포함합니다.
 */
@Getter
@Builder
@Slf4j
public class FavoriteStatistics {
    private final long totalCount;
    private final ComparisonPeriod comparisonPeriod;
    private final ComparisonResult comparisonResult;

    /**
     * FavoriteStatistics 객체를 생성합니다.
     *
     * @param totalCount       총 즐겨찾기 개수 (0 이상)
     * @param comparisonPeriod 비교 기간 (null 불가)
     * @param comparisonResult 비교 결과 (null 불가)
     * @throws IllegalArgumentException 유효하지 않은 값이 전달된 경우
     */
    public FavoriteStatistics(long totalCount, ComparisonPeriod comparisonPeriod, ComparisonResult comparisonResult) {
        Assert.isTrue(totalCount >= 0, "totalCount must be non-negative");
        Assert.notNull(comparisonPeriod, "comparisonPeriod must not be null");
        Assert.notNull(comparisonResult, "comparisonResult must not be null");
        this.totalCount = totalCount;
        this.comparisonPeriod = comparisonPeriod;
        this.comparisonResult = comparisonResult;
        log.debug("FavoriteStatistics created: totalCount={}, period={}, result={}", totalCount, comparisonPeriod,
            comparisonResult);
    }
}
