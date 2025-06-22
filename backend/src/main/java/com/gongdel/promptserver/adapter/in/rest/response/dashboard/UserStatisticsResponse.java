package com.gongdel.promptserver.adapter.in.rest.response.dashboard;

import com.gongdel.promptserver.domain.statistics.UserStatistics;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 유저 통계 대시보드 응답 DTO입니다.
 */
@Getter
@Builder
@Schema(description = "유저 통계 대시보드 응답 DTO")
public class UserStatisticsResponse {
    @Schema(description = "전체 유저 개수", example = "1500")
    private final long totalCount;

    @Schema(description = "현재 기간 유저 개수", example = "100")
    private final long currentCount;

    @Schema(description = "이전 기간 유저 개수", example = "80")
    private final long previousCount;

    @Schema(description = "변동률(%)", example = "25.0")
    private final double percentageChange;

    /**
     * 도메인 객체로부터 응답 DTO를 생성합니다.
     *
     * @param stats 유저 통계 도메인 객체
     * @return 응답 DTO
     */
    public static UserStatisticsResponse from(UserStatistics stats) {
        return UserStatisticsResponse.builder()
            .totalCount(stats.getTotalCount())
            .currentCount(stats.getComparisonResult().getCurrentCount())
            .previousCount(stats.getComparisonResult().getPreviousCount())
            .percentageChange(stats.getComparisonResult().getPercentageChange())
            .build();
    }
}
