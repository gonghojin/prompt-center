package com.gongdel.promptserver.domain.view;

import com.gongdel.promptserver.domain.statistics.ComparisonPeriod;
import com.gongdel.promptserver.domain.statistics.ComparisonResult;
import lombok.Getter;
import lombok.ToString;

/**
 * 조회수 통계 도메인 객체입니다.
 * 특정 기간의 총 조회수와 이전 기간 대비 증감 정보를 포함합니다.
 */
@Getter
@ToString
public class ViewStatistics {

    /**
     * 전체 누적 조회수
     */
    private final long totalViewCount;

    /**
     * 통계 기간 정보
     */
    private final ComparisonPeriod period;

    /**
     * 이전 기간 대비 비교 결과 (증감 수치, 증감률)
     */
    private final ComparisonResult comparisonResult;

    /**
     * ViewStatistics를 생성합니다.
     *
     * @param totalViewCount   전체 누적 조회수
     * @param period           통계 기간
     * @param comparisonResult 비교 결과
     */
    public ViewStatistics(long totalViewCount, ComparisonPeriod period, ComparisonResult comparisonResult) {
        this.totalViewCount = totalViewCount;
        this.period = period;
        this.comparisonResult = comparisonResult;
    }

    /**
     * 현재 기간의 조회수를 반환합니다.
     *
     * @return 현재 기간 조회수
     */
    public long getCurrentPeriodViewCount() {
        return comparisonResult.getCurrentCount();
    }

    /**
     * 이전 기간의 조회수를 반환합니다.
     *
     * @return 이전 기간 조회수
     */
    public long getPreviousPeriodViewCount() {
        return comparisonResult.getPreviousCount();
    }

    /**
     * 증감 수치를 반환합니다.
     *
     * @return 증감 수치
     */
    public long getChangeCount() {
        return comparisonResult.getCurrentCount() - comparisonResult.getPreviousCount();
    }

    /**
     * 증감률을 반환합니다.
     *
     * @return 증감률 (%)
     */
    public double getChangeRate() {
        return comparisonResult.getPercentageChange();
    }
}
