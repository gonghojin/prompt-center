package com.gongdel.promptserver.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 프롬프트 검색 결과 정보를 담는 불변 객체입니다.
 */

@Getter
@Builder
@ToString
public class PromptSearchResult {
    /**
     * 프롬프트 고유 식별자
     */
    @NonNull
    private final Long id;
    /**
     * 프롬프트 UUID
     */
    @NonNull
    private final UUID uuid;
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
     * 현재 버전 ID
     */
    @NonNull
    private final Long currentVersionId;
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
     * 생성자(사용자) ID
     */
    @NonNull
    private final Long createdById;
    /**
     * 생성자(사용자) 이름
     */
    @NonNull
    private final String createdByName;
    /**
     * 태그 목록
     */
    @NonNull
    private final List<String> tags;
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
     * 생성일시
     */
    @NonNull
    private final LocalDateTime createdAt;
    /**
     * 수정일시
     */
    @NonNull
    private final LocalDateTime updatedAt;

    @Builder
    public PromptSearchResult(
        Long id, UUID uuid,
        String title, String description,
        Long currentVersionId,
        Long categoryId, String categoryName,
        Long createdById, String createdByName,
        List<String> tags, Visibility visibility, PromptStatus status,
        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = java.util.Objects.requireNonNull(id, "id must not be null");
        this.uuid = java.util.Objects.requireNonNull(uuid, "uuid must not be null");
        this.title = title == null ? "" : title;
        this.description = description == null ? "" : description;
        this.currentVersionId = java.util.Objects.requireNonNull(currentVersionId, "currentVersionId must not be null");
        this.categoryId = categoryId == null ? 0L : categoryId;
        this.categoryName = categoryName == null ? "" : categoryName;
        this.createdById = createdById == null ? 0L : createdById;
        this.createdByName = createdByName == null ? "" : createdByName;
        this.tags = tags == null ? Collections.emptyList() : tags;
        this.visibility = java.util.Objects.requireNonNull(visibility, "visibility must not be null");
        this.status = java.util.Objects.requireNonNull(status, "status must not be null");
        this.createdAt = java.util.Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = java.util.Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }
}
