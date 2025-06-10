package com.gongdel.promptserver.adapter.in.rest.response.favorite;

import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.model.Visibility;
import com.gongdel.promptserver.domain.model.favorite.FavoritePromptResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 즐겨찾기 프롬프트 목록 조회 응답 DTO입니다.
 */
@Getter
@Builder
@Schema(description = "즐겨찾기 프롬프트 목록 조회 응답 DTO")
public class FavoritePromptResponse {
    @Schema(description = "즐겨찾기 고유 식별자", example = "1")
    private final Long favoriteId;
    @Schema(description = "프롬프트 UUID", example = "b1a2c3d4-e5f6-7890-1234-56789abcdef0")
    private final UUID promptUuid;
    @Schema(description = "프롬프트 제목", example = "AI 추천 프롬프트")
    private final String title;
    @Schema(description = "프롬프트 설명", example = "이미지 생성에 최적화된 프롬프트")
    private final String description;
    @Schema(description = "태그 목록", example = "[\"stable-diffusion\", \"ai\"]")
    private final List<String> tags;
    @Schema(description = "작성자 ID", example = "1")
    private final Long createdById;
    @Schema(description = "작성자명", example = "홍길동")
    private final String createdByName;
    @Schema(description = "카테고리 ID", example = "2")
    private final Long categoryId;

    @Schema(description = "공개 범위", example = "PUBLIC")
    private final Visibility visibility;
    @Schema(description = "프롬프트 상태", example = "PUBLISHED")
    private final PromptStatus status;
    @Schema(description = "프롬프트 생성일시", example = "2024-05-01T12:34:56")
    private final LocalDateTime promptCreatedAt;
    @Schema(description = "프롬프트 수정일시", example = "2024-05-02T15:00:00")
    private final LocalDateTime promptUpdatedAt;
    @Schema(description = "즐겨찾기 추가 일시", example = "2024-05-03T10:00:00")
    private final LocalDateTime favoriteCreatedAt;
    @Schema(description = "프롬프트 조회수", example = "123")
    private final int viewCount;
    @Schema(description = "프롬프트 즐겨찾기 수", example = "10")
    private final int favoriteCount;

    /**
     * 도메인 객체(FavoritePromptResult)로부터 응답 DTO를 생성합니다.
     *
     * @param result 즐겨찾기 프롬프트 도메인 객체
     * @return FavoritePromptResponse
     */
    public static FavoritePromptResponse from(FavoritePromptResult result) {
        return FavoritePromptResponse.builder()
            .favoriteId(result.getFavoriteId())
            .promptUuid(result.getPromptUuid())
            .title(result.getTitle())
            .description(result.getDescription())
            .tags(result.getTags())
            .createdById(result.getCreatedById())
            .createdByName(result.getCreatedByName())
            .categoryId(result.getCategoryId())
            .visibility(result.getVisibility())
            .status(result.getStatus())
            .promptCreatedAt(result.getPromptCreatedAt())
            .promptUpdatedAt(result.getPromptUpdatedAt())
            .favoriteCreatedAt(result.getFavoriteCreatedAt())
            .viewCount(result.getStats() != null ? result.getStats().getViewCount() : 0)
            .favoriteCount(result.getStats() != null ? result.getStats().getFavoriteCount() : 0)
            .build();
    }
}
