package com.gongdel.promptserver.domain.view;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * 주간 조회수 통계를 나타내는 도메인 객체입니다.
 * <p>
 * 이번 주와 지난 주의 조회수 비교 정보를 제공합니다.
 */
@Getter
@ToString
@Builder
public class WeeklyViewStatistics {
    /**
     * 이번 주 조회수
     */
    private final long thisWeekViews;

    /**
     * 지난 주 조회수
     */
    private final long lastWeekViews;

    /**
     * 증감률 (%)
     */
    private final double changeRate;

    /**
     * 증감 수치
     */
    private final long changeCount;

    /**
     * 이번 주 시작일 (월요일)
     */
    private final LocalDate weekStartDate;

    /**
     * 이번 주 종료일 (일요일)
     */
    private final LocalDate weekEndDate;

    /**
     * WeeklyViewStatistics 객체를 생성합니다.
     *
     * @param thisWeekViews 이번 주 조회수 (0 이상)
     * @param lastWeekViews 지난 주 조회수 (0 이상)
     * @param changeRate    증감률
     * @param changeCount   증감 수치
     * @param weekStartDate 이번 주 시작일 (필수)
     * @param weekEndDate   이번 주 종료일 (필수)
     */
    @Builder
    public WeeklyViewStatistics(long thisWeekViews, long lastWeekViews, double changeRate,
                                long changeCount, LocalDate weekStartDate, LocalDate weekEndDate) {
        Assert.isTrue(thisWeekViews >= 0, "thisWeekViews must be non-negative");
        Assert.isTrue(lastWeekViews >= 0, "lastWeekViews must be non-negative");
        Assert.notNull(weekStartDate, "weekStartDate must not be null");
        Assert.notNull(weekEndDate, "weekEndDate must not be null");

        this.thisWeekViews = thisWeekViews;
        this.lastWeekViews = lastWeekViews;
        this.changeRate = changeRate;
        this.changeCount = changeCount;
        this.weekStartDate = weekStartDate;
        this.weekEndDate = weekEndDate;
    }

    /**
     * 주간 통계 생성 팩토리 메서드
     *
     * @param thisWeekViews 이번 주 조회수
     * @param lastWeekViews 지난 주 조회수
     * @return WeeklyViewStatistics 객체
     */
    public static WeeklyViewStatistics of(long thisWeekViews, long lastWeekViews) {
        // 증감률 계산
        double changeRate = 0.0;
        if (lastWeekViews == 0) {
            changeRate = thisWeekViews > 0 ? 100.0 : 0.0;
        } else {
            changeRate = ((double) (thisWeekViews - lastWeekViews) / lastWeekViews) * 100.0;
        }

        long changeCount = thisWeekViews - lastWeekViews;

        // 이번 주 월요일 ~ 일요일 계산
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = today.with(DayOfWeek.SUNDAY);

        return WeeklyViewStatistics.builder()
            .thisWeekViews(thisWeekViews)
            .lastWeekViews(lastWeekViews)
            .changeRate(Math.round(changeRate * 100.0) / 100.0) // 소수점 둘째 자리까지
            .changeCount(changeCount)
            .weekStartDate(weekStart)
            .weekEndDate(weekEnd)
            .build();
    }

    /**
     * 조회수가 증가했는지 확인합니다.
     *
     * @return 조회수가 증가한 경우 true
     */
    public boolean isIncreased() {
        return changeCount > 0;
    }

    /**
     * 조회수가 감소했는지 확인합니다.
     *
     * @return 조회수가 감소한 경우 true
     */
    public boolean isDecreased() {
        return changeCount < 0;
    }

    /**
     * 조회수가 동일한지 확인합니다.
     *
     * @return 조회수가 동일한 경우 true
     */
    public boolean isStable() {
        return changeCount == 0;
    }
}
