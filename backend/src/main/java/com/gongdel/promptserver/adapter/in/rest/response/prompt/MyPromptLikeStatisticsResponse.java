package com.gongdel.promptserver.adapter.in.rest.response.prompt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 내 프롬프트 총 좋아요 수 통계 응답 DTO입니다.
 */
@Getter
@Builder
@Schema(description = "내 프롬프트 총 좋아요 수 통계 응답 DTO")
public class MyPromptLikeStatisticsResponse {
    @Schema(description = "내 프롬프트 총 좋아요 수", example = "57")
    private final long totalLikeCount;

    public static MyPromptLikeStatisticsResponse of(long totalLikeCount) {
        return MyPromptLikeStatisticsResponse.builder()
            .totalLikeCount(totalLikeCount)
            .build();
    }
}
