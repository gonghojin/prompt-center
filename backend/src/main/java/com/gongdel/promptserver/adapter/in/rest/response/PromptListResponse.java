package com.gongdel.promptserver.adapter.in.rest.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 프롬프트 목록 조회 결과를 클라이언트에 응답하기 위한 DTO 클래스입니다.
 */
@Getter
@Builder
public class PromptListResponse {
    @Schema(description = "프롬프트 UUID", example = "b1a2c3d4-e5f6-7890-1234-56789abcdef0")
    private final UUID id;
    @Schema(description = "프롬프트 제목", example = "AI 추천 프롬프트")
    private final String title;
    @Schema(description = "프롬프트 설명", example = "이미지 생성에 최적화된 프롬프트")
    private final String description;
    @Schema(description = "작성자 ID", example = "1")
    private final Long authorId;
    @Schema(description = "카테고리 ID", example = "2")
    private final Long categoryId;
    @Schema(description = "카테고리명", example = "이미지 생성")
    private final String categoryName;
    @Schema(description = "작성자명", example = "홍길동")
    private final String createdByName;
    @Schema(description = "태그 목록", example = "[\"stable-diffusion\", \"ai\"]")
    private final java.util.List<String> tags;

    @Schema(description = "공개 여부", example = "true")
    private final boolean isPublic;
    @Schema(description = "생성일시", example = "2024-05-01T12:34:56")
    private final LocalDateTime createdAt;
    @Schema(description = "수정일시", example = "2024-05-02T15:00:00")
    private final LocalDateTime updatedAt;
    @Schema(description = "즐겨찾기 수", example = "10")
    private final int favoriteCount;
    @Schema(description = "조회수", example = "123")
    private final int viewCount;

    /**
     * PromptSearchResult 객체로부터 응답 DTO를 생성합니다.
     *
     * @param result 프롬프트 검색 결과 도메인 객체
     * @return 프롬프트 목록 응답 DTO
     */
    public static PromptListResponse from(com.gongdel.promptserver.domain.model.PromptSearchResult result) {
        return PromptListResponse.builder()
            .id(result.getUuid())
            .title(result.getTitle())
            .description(result.getDescription())
            .authorId(result.getCreatedById())
            .categoryId(result.getCategoryId())
            .categoryName(result.getCategoryName())
            .createdByName(result.getCreatedByName())
            .tags(result.getTags())
            .isPublic(result.getVisibility() != null
                && result.getVisibility() == com.gongdel.promptserver.domain.model.Visibility.PUBLIC)
            .createdAt(result.getCreatedAt())
            .updatedAt(result.getUpdatedAt())
            .favoriteCount(0) // TODO: 실제 값으로 교체 필요
            .viewCount(0) // TODO: 실제 값으로 교체 필요
            .build();
    }
}
