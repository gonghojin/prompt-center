package com.gongdel.promptserver.domain.model.favorite;

import com.gongdel.promptserver.domain.model.PromptStats;
import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.model.Visibility;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 즐겨찾기 프롬프트 목록 조회 결과 정보를 담는 불변 객체입니다.
 * 프롬프트 통계 정보(PromptStats)도 포함합니다.
 */
@Getter
@Builder
@ToString
public class FavoritePromptResult {
    /**
     * 즐겨찾기 고유 식별자
     */
    @NonNull
    private final Long favoriteId;
    /**
     * 프롬프트 고유 식별자
     */
    @NonNull
    private final Long promptId;
    /**
     * 프롬프트 UUID
     */
    @NonNull
    private final UUID promptUuid;
    /**
     * 프롬프트 제목
     */
    @NonNull
    private final String title;
    /**
     * 프롬프트 설명
     */
    @NonNull
    private final String description;
    /**
     * 태그 목록
     */
    @NonNull
    private final List<String> tags;
    /**
     * 작성자 ID
     */
    @NonNull
    private final Long createdById;
    /**
     * 작성자 이름
     */
    @NonNull
    private final String createdByName;
    /**
     * 카테고리 ID
     */
    @NonNull
    private final Long categoryId;
    /**
     * 카테고리 이름
     */
    @NonNull
    private final String categoryName;
    /**
     * 공개 범위
     */
    @NonNull
    private final Visibility visibility;
    /**
     * 프롬프트 상태
     */
    @NonNull
    private final PromptStatus status;
    /**
     * 프롬프트 생성일시
     */
    @NonNull
    private final LocalDateTime promptCreatedAt;
    /**
     * 프롬프트 수정일시
     */
    @NonNull
    private final LocalDateTime promptUpdatedAt;
    /**
     * 즐겨찾기 추가 일시
     */
    @NonNull
    private final LocalDateTime favoriteCreatedAt;
    /**
     * 프롬프트 통계 정보 (조회수, 좋아요 수 등)
     */
    @NonNull
    private final PromptStats stats;

    @Builder
    public FavoritePromptResult(Long favoriteId, Long promptId, UUID promptUuid, String title, String description,
                                List<String> tags, Long createdById, String createdByName, Long categoryId, String categoryName,
                                Visibility visibility, PromptStatus status, LocalDateTime promptCreatedAt, LocalDateTime promptUpdatedAt,
                                LocalDateTime favoriteCreatedAt, PromptStats stats) {
        this.favoriteId = java.util.Objects.requireNonNull(favoriteId, "favoriteId must not be null");
        this.promptId = java.util.Objects.requireNonNull(promptId, "promptId must not be null");
        this.promptUuid = java.util.Objects.requireNonNull(promptUuid, "promptUuid must not be null");
        this.title = title == null ? "" : title;
        this.description = description == null ? "" : description;
        this.tags = tags == null ? Collections.emptyList() : tags;
        this.createdById = createdById == null ? 0L : createdById;
        this.createdByName = createdByName == null ? "" : createdByName;
        this.categoryId = categoryId == null ? 0L : categoryId;
        this.categoryName = categoryName == null ? "" : categoryName;
        this.visibility = java.util.Objects.requireNonNull(visibility, "visibility must not be null");
        this.status = java.util.Objects.requireNonNull(status, "status must not be null");
        this.promptCreatedAt = java.util.Objects.requireNonNull(promptCreatedAt, "promptCreatedAt must not be null");
        this.promptUpdatedAt = java.util.Objects.requireNonNull(promptUpdatedAt, "promptUpdatedAt must not be null");
        this.favoriteCreatedAt = java.util.Objects.requireNonNull(favoriteCreatedAt,
            "favoriteCreatedAt must not be null");
        this.stats = java.util.Objects.requireNonNull(stats, "stats must not be null");
    }
}
