package com.gongdel.promptserver.adapter.in.rest.response.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 카테고리별 프롬프트 통계 대시보드 응답 DTO입니다.
 */
@Getter
@Builder
@Schema(description = "카테고리별 프롬프트 통계 대시보드 응답 DTO")
public class CategoryStatisticsResponse {

    @Schema(description = "카테고리별 통계 목록")
    private final List<CategoryStat> categories;

    @Getter
    @Builder
    @Schema(description = "카테고리별 통계")
    public static class CategoryStat {
        @Schema(description = "카테고리 ID", example = "1")
        private final Long categoryId;

        @Schema(description = "카테고리명", example = "Backend")
        private final String categoryName;

        @Schema(description = "프롬프트 개수", example = "342")
        private final long promptCount;
    }
}
