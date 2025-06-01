package com.gongdel.promptserver.adapter.in.rest.response;

import com.gongdel.promptserver.domain.model.PromptDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * <b>프롬프트 상세 조회 결과 정보를 클라이언트에 응답하기 위한 DTO 클래스입니다.
 */
@Getter
@Builder
@Schema(description = "프롬프트 상세 조회 결과 응답 DTO")
public class PromptDetailResponse {
    @Schema(description = "프롬프트 UUID", example = "b7e6c1a2-3f4d-4e2a-9c1a-123456789abc")
    private final UUID id;

    @Schema(description = "프롬프트 제목", example = "ChatGPT 활용법")
    private final String title;

    @Schema(description = "프롬프트 설명", example = "이 프롬프트는 ChatGPT를 효과적으로 활용하는 방법을 안내합니다.")
    private final String description;

    @Schema(description = "프롬프트 본문(최신 버전)", example = "ChatGPT를 사용할 때 다음과 같이 질문하세요...")
    private final String content;

    @Schema(description = "작성자 정보")
    private final UserResponse author;

    @Schema(description = "태그 목록", example = "[\"AI\", \"ChatGPT\", \"활용법\"]")
    private final Set<String> tags;

    @Schema(description = "공개 여부", example = "true")
    private final boolean isPublic;

    @Schema(description = "생성일시", example = "2024-05-01T12:34:56")
    private final LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2024-05-02T15:00:00")
    private final LocalDateTime updatedAt;

    @Schema(description = "조회수", example = "123")
    private final int viewCount;

    @Schema(description = "좋아요 수", example = "45")
    private final int favoriteCount;

    @Schema(description = "카테고리 ID", example = "1")
    private final Long categoryId;

    @Schema(description = "공개 범위", example = "PUBLIC")
    private final String visibility;

    @Schema(description = "프롬프트 상태", example = "ACTIVE")
    private final String status;

    /**
     * <b>여러 도메인 객체로부터 상세 조회 응답 DTO를 생성합니다.</b><br>
     * 이 메서드는 다양한 소스에서 받은 데이터를 명확하게 매핑하여, API 응답에 필요한 모든 정보를 담은 DTO를 생성합니다.
     *
     * @param id            프롬프트 UUID
     * @param title         제목
     * @param description   설명
     * @param content       본문(최신 버전)
     * @param author        작성자 정보
     * @param tags          태그 목록
     * @param isPublic      공개 여부
     * @param createdAt     생성일
     * @param updatedAt     수정일
     * @param viewCount     조회수
     * @param favoriteCount 좋아요 수
     * @param categoryId    카테고리 ID
     * @param visibility    공개 범위
     * @param status        상태
     * @return 프롬프트 상세 응답 DTO
     */
    public static PromptDetailResponse from(
        UUID id,
        String title,
        String description,
        String content,
        UserResponse author,
        Set<String> tags,
        boolean isPublic,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        int viewCount,
        int favoriteCount,
        Long categoryId,
        String visibility,
        String status) {
        return PromptDetailResponse.builder()
            .id(id)
            .title(title)
            .description(description)
            .content(content)
            .author(author)
            .tags(tags)
            .isPublic(isPublic)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .viewCount(viewCount)
            .favoriteCount(favoriteCount)
            .categoryId(categoryId)
            .visibility(visibility)
            .status(status)
            .build();
    }

    /**
     * <b>도메인 객체(PromptDetail)로부터 상세 조회 응답 DTO를 생성합니다.</b><br>
     * 이 메서드는 도메인 계층의 정보를 API 응답에 맞는 형태로 변환할 때 사용합니다.<br>
     * 도메인 객체의 변경이 있을 경우에도, API 응답 구조를 안정적으로 유지할 수 있습니다.
     *
     * @param detail 프롬프트 상세 도메인 객체
     * @return 프롬프트 상세 응답 DTO
     */
    public static PromptDetailResponse from(PromptDetail detail) {
        return new PromptDetailResponse(
            detail.getId(),
            detail.getTitle(),
            detail.getDescription(),
            detail.getContent(),
            UserResponse.from(detail.getAuthor()),
            detail.getTags(),
            detail.isPublic(),
            detail.getCreatedAt(),
            detail.getUpdatedAt(),
            detail.getViewCount(),
            detail.getFavoriteCount(),
            detail.getCategoryId(),
            detail.getVisibility(),
            detail.getStatus());
    }
}
