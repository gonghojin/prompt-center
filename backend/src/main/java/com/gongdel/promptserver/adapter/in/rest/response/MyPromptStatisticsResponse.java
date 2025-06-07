package com.gongdel.promptserver.adapter.in.rest.response;

import com.gongdel.promptserver.domain.model.statistics.PromptStatisticsResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 내 프롬프트 통계 응답 DTO입니다.
 */
@Getter
@Builder
@Schema(description = "내 프롬프트 통계 응답 DTO")
public class MyPromptStatisticsResponse {
    @Schema(description = "전체 개수", example = "10")
    private final long totalCount;
    @Schema(description = "임시저장 개수", example = "2")
    private final long draftCount;
    @Schema(description = "발행 개수", example = "6")
    private final long publishedCount;
    @Schema(description = "보관 개수", example = "2")
    private final long archivedCount;

    /**
     * 도메인 통계 결과로부터 응답 DTO를 생성합니다.
     *
     * @param result 도메인 통계 결과
     * @return 응답 DTO
     */
    public static MyPromptStatisticsResponse from(PromptStatisticsResult result) {
        return MyPromptStatisticsResponse.builder()
            .totalCount(result.getTotalCount())
            .draftCount(result.getDraftCount())
            .publishedCount(result.getPublishedCount())
            .archivedCount(result.getArchivedCount())
            .build();
    }
}
