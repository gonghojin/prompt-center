package com.gongdel.promptserver.adapter.in.rest.response.view;

import com.gongdel.promptserver.domain.view.ViewCount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 조회수 조회 응답 DTO입니다.
 */
@Getter
@Builder
@Schema(description = "조회수 조회 응답 DTO")
public class ViewCountResponse {

    @Schema(description = "프롬프트 템플릿 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
    private final UUID promptTemplateUuid;

    @Schema(description = "총 조회수", example = "127")
    private final long totalViewCount;

    @Schema(description = "생성일시", example = "2024-01-15T10:30:00")
    private final LocalDateTime createdAt;

    @Schema(description = "최종 업데이트일시", example = "2024-01-15T14:20:00")
    private final LocalDateTime updatedAt;

    /**
     * ViewCount 도메인 객체로부터 응답 DTO 생성
     */
    public static ViewCountResponse from(ViewCount viewCount, UUID promptTemplateUuid) {
        return ViewCountResponse.builder()
            .promptTemplateUuid(promptTemplateUuid)
            .totalViewCount(viewCount.getTotalViewCount())
            .createdAt(viewCount.getCreatedAt())
            .updatedAt(viewCount.getUpdatedAt())
            .build();
    }

    /**
     * 조회수가 없는 경우의 기본 응답 생성
     */
    public static ViewCountResponse empty(UUID promptTemplateUuid) {
        LocalDateTime now = LocalDateTime.now();
        return ViewCountResponse.builder()
            .promptTemplateUuid(promptTemplateUuid)
            .totalViewCount(0L)
            .createdAt(now)
            .updatedAt(now)
            .build();
    }
}
