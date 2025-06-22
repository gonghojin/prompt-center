package com.gongdel.promptserver.adapter.in.rest.response.view;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 조회수 통계 응답 DTO들의 컬렉션입니다.
 */
public class ViewStatisticsResponse {

    /**
     * 일별 조회수 통계 응답 DTO
     */
    @Getter
    @Builder
    @Schema(description = "일별 조회수 통계 응답 DTO")
    public static class DailyViewStatistics {

        @Schema(description = "날짜", example = "2024-01-15")
        private final LocalDate date;

        @Schema(description = "해당 날짜의 조회수", example = "42")
        private final long viewCount;

        /**
         * DailyViewStatisticsDto로부터 응답 DTO 생성
         */
        public static DailyViewStatistics from(com.gongdel.promptserver.domain.model.statistics.DailyViewStatistics dto) {
            return ViewStatisticsResponse.DailyViewStatistics.builder()
                .date(dto.getViewDate())
                .viewCount(dto.getViewCount() != null ? dto.getViewCount() : 0L)
                .build();
        }

        /**
         * DailyViewStatistics 리스트를 응답 DTO 리스트로 변환
         */
        public static List<DailyViewStatistics> fromList(List<com.gongdel.promptserver.domain.model.statistics.DailyViewStatistics> dtoList) {
            return dtoList.stream()
                .map(ViewStatisticsResponse.DailyViewStatistics::from)
                .collect(Collectors.toList());
        }
    }

    /**
     * 조회수 분포 통계 응답 DTO
     */
    @Getter
    @Builder
    @Schema(description = "조회수 분포 통계 응답 DTO")
    public static class ViewCountDistribution {

        @Schema(description = "조회수 구간 (예: '1-10', '11-100')", example = "1-10")
        private final String range;

        @Schema(description = "해당 구간에 속하는 프롬프트 수", example = "25")
        private final long promptCount;

        /**
         * ViewCountDistributionDto로부터 응답 DTO 생성
         */
        public static ViewCountDistribution from(com.gongdel.promptserver.domain.model.statistics.ViewCountDistribution dto) {
            return ViewStatisticsResponse.ViewCountDistribution.builder()
                .range(dto.getViewRange())
                .promptCount(dto.getCount() != null ? dto.getCount() : 0L)
                .build();
        }

        /**
         * ViewCountDistribution 리스트를 응답 DTO 리스트로 변환
         */
        public static List<ViewCountDistribution> fromList(List<com.gongdel.promptserver.domain.model.statistics.ViewCountDistribution> dtoList) {
            return dtoList.stream()
                .map(ViewStatisticsResponse.ViewCountDistribution::from)
                .collect(Collectors.toList());
        }
    }
}
