package com.gongdel.promptserver.adapter.in.rest.response.dashboard;

import com.gongdel.promptserver.domain.view.WeeklyViewStatistics;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 주간 조회수 통계 응답 DTO입니다.
 */
@Getter
@Builder
@Schema(description = "주간 조회수 통계 응답 DTO")
public class WeeklyViewStatisticsResponse {

    @Schema(description = "이번 주 조회수", example = "3891")
    private final long thisWeekViewCount;

    @Schema(description = "지난 주 조회수", example = "3300")
    private final long lastWeekViewCount;

    @Schema(description = "증감 수치", example = "591")
    private final long changeCount;

    @Schema(description = "증감률 (%)", example = "18.0")
    private final double changeRate;

    @Schema(description = "이번 주 시작일", example = "2024-01-15")
    private final LocalDate weekStartDate;

    @Schema(description = "이번 주 종료일", example = "2024-01-21")
    private final LocalDate weekEndDate;

    /**
     * WeeklyViewStatistics 도메인 객체로부터 응답 DTO를 생성합니다.
     *
     * @param weeklyStats 주간 조회수 통계 도메인 객체
     * @return WeeklyViewStatisticsResponse
     */
    public static WeeklyViewStatisticsResponse from(WeeklyViewStatistics weeklyStats) {
        return WeeklyViewStatisticsResponse.builder()
            .thisWeekViewCount(weeklyStats.getThisWeekViews())
            .lastWeekViewCount(weeklyStats.getLastWeekViews())
            .changeCount(weeklyStats.getChangeCount())
            .changeRate(weeklyStats.getChangeRate())
            .weekStartDate(weeklyStats.getWeekStartDate())
            .weekEndDate(weeklyStats.getWeekEndDate())
            .build();
    }
}
