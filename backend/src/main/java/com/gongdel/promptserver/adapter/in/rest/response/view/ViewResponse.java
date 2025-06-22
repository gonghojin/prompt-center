package com.gongdel.promptserver.adapter.in.rest.response.view;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 조회수 기록 응답 DTO입니다.
 */
@Getter
@Builder
@Schema(description = "조회수 기록 응답 DTO")
public class ViewResponse {

    @Schema(description = "요청 성공 여부", example = "true")
    private final boolean success;

    @Schema(description = "현재 총 조회수", example = "127")
    private final long totalViewCount;

    @Schema(description = "새로운 조회인지 여부 (중복 조회가 아닌 경우 true)", example = "true")
    private final boolean isNewView;

    /**
     * 정적 팩토리 메서드
     */
    public static ViewResponse of(boolean success, long totalViewCount, boolean isNewView) {
        return ViewResponse.builder()
            .success(success)
            .totalViewCount(totalViewCount)
            .isNewView(isNewView)
            .build();
    }

    /**
     * 성공 응답 생성 팩토리 메서드
     */
    public static ViewResponse success(long totalViewCount, boolean isNewView) {
        return of(true, totalViewCount, isNewView);
    }
}
