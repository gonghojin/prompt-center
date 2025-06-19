package com.gongdel.promptserver.adapter.in.rest.response.prompt;

import com.gongdel.promptserver.domain.model.PromptSearchResult;
import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.model.Visibility;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 내 프롬프트 목록 조회 결과를 클라이언트에 응답하기 위한 DTO 클래스입니다.
 */
@Getter
@Builder
public class MyPromptListResponse {

    @Schema(description = "프롬프트 UUID", example = "b1a2c3d4-e5f6-7890-1234-56789abcdef0")
    private final UUID id;

    @Schema(description = "프롬프트 제목", example = "AI 추천 프롬프트")
    private final String title;

    @Schema(description = "프롬프트 설명", example = "이미지 생성에 최적화된 프롬프트")
    private final String description;

    @Schema(description = "카테고리 ID", example = "2")
    private final Long categoryId;

    @Schema(description = "즐겨찾기 수", example = "10")
    private final int favoriteCount;

    @Schema(description = "조회수", example = "123")
    private final int viewCount;

    @Schema(description = "수정일시", example = "2024-05-02T15:00:00")
    private final LocalDateTime updatedAt;

    @Schema(description = "프롬프트 상태", example = "PUBLISHED")
    private final PromptStatus status;

    @Schema(description = "공개 범위", example = "PUBLIC")
    private final Visibility visibility;

    @Schema(description = "공개 여부", example = "true")
    private final boolean isPublic;

    @Schema(description = "내가 즐겨찾기한 프롬프트 여부", example = "true")
    private final boolean isFavorite;

    @Schema(description = "내가 좋아요한 프롬프트 여부", example = "true")
    private final boolean isLiked;

    @Schema(description = "태그 목록", example = "[\"stable-diffusion\", \"ai\"]")
    private final List<String> tags;

    /**
     * PromptSearchResult 객체로부터 응답 DTO를 생성합니다.
     *
     * @param result 프롬프트 검색 결과 도메인 객체
     * @return 내 프롬프트 목록 응답 DTO
     */
    public static MyPromptListResponse from(PromptSearchResult result) {
        return MyPromptListResponse.builder()
            .id(result.getUuid())
            .title(result.getTitle())
            .description(result.getDescription())
            .categoryId(result.getCategoryId())
            .favoriteCount(result.getStats() != null ? result.getStats().getFavoriteCount() : 0)
            .viewCount(result.getStats() != null ? result.getStats().getViewCount() : 0)
            .updatedAt(result.getUpdatedAt())
            .status(result.getStatus())
            .visibility(result.getVisibility())
            .isPublic(result.getVisibility() != null && result.getVisibility() == Visibility.PUBLIC)
            .isFavorite(result.isFavorite())
            .isLiked(result.isLiked())
            .tags(result.getTags())
            .build();
    }
}
