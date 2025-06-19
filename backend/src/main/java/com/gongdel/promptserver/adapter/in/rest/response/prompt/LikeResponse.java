package com.gongdel.promptserver.adapter.in.rest.response.prompt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 좋아요 액션 응답 DTO입니다.
 */
@Getter
@Builder
@Schema(description = "좋아요 액션 응답 DTO")
public class LikeResponse {
    @Schema(description = "요청 성공 여부", example = "true")
    private final boolean success;
    @Schema(description = "현재 좋아요 수", example = "12")
    private final long likeCount;

    public static LikeResponse of(boolean success, long likeCount) {
        return LikeResponse.builder()
            .success(success)
            .likeCount(likeCount)
            .build();
    }
}
