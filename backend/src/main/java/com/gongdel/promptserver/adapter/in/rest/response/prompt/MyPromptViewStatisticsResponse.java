package com.gongdel.promptserver.adapter.in.rest.response.prompt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 내 프롬프트 총 조회수 통계 응답 DTO입니다.
 */
@Getter
@Builder
@Schema(description = "내 프롬프트 총 조회수 통계 응답 DTO")
public class MyPromptViewStatisticsResponse {
    @Schema(description = "내 프롬프트 총 조회수", example = "1234")
    private final long totalViewCount;

    public static MyPromptViewStatisticsResponse of(long totalViewCount) {
        return MyPromptViewStatisticsResponse.builder()
            .totalViewCount(totalViewCount)
            .build();
    }
}
