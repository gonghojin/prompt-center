package com.gongdel.promptserver.adapter.in.rest.response.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 대시보드 전체 통계 개요 응답 DTO입니다.
 */
@Getter
@Builder
@Schema(description = "대시보드 전체 통계 개요 응답 DTO")
public class DashboardOverviewResponse {
    @Schema(description = "총 프롬프트 통계")
    private final PromptStat prompt;

    @Schema(description = "팀 멤버 통계")
    private final MemberStat member;

    @Schema(description = "이번 주 조회수 통계")
    private final WeeklyViewStat weeklyView;

    @Schema(description = "즐겨찾기 통계")
    private final FavoriteStat favorite;

    @Getter
    @Builder
    @Schema(description = "총 프롬프트 통계")
    public static class PromptStat {
        @Schema(description = "총 프롬프트 수", example = "7")
        private final long totalCount;
        @Schema(description = "전주 대비 증감률(%)", example = "100.0")
        private final double changeRate;
    }

    @Getter
    @Builder
    @Schema(description = "팀 멤버 통계")
    public static class MemberStat {
        @Schema(description = "팀 멤버 수", example = "24")
        private final long totalCount;
        @Schema(description = "전주 대비 증감수치", example = "3")
        private final int changeCount;
    }

    @Getter
    @Builder
    @Schema(description = "이번 주 조회수 통계")
    public static class WeeklyViewStat {
        @Schema(description = "이번 주 조회수", example = "3891")
        private final long count;
        @Schema(description = "전주 대비 증감률(%)", example = "18.0")
        private final double changeRate;
    }

    @Getter
    @Builder
    @Schema(description = "즐겨찾기 통계")
    public static class FavoriteStat {
        @Schema(description = "즐겨찾기 수", example = "156")
        private final long count;
        @Schema(description = "전주 대비 증감수치", example = "7")
        private final int changeCount;
    }
}
