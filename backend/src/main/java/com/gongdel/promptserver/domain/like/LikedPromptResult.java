package com.gongdel.promptserver.domain.like;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 내가 좋아요한 프롬프트 정보를 담는 불변 객체입니다.
 */
@Getter
@Builder
@ToString
public class LikedPromptResult {
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
     * 좋아요한 일시
     */
    @NonNull
    private final LocalDateTime likedAt;

    @Builder
    public LikedPromptResult(
        Long id, UUID uuid,
        String title, String description,
        Long createdById, String createdByName,
        LocalDateTime likedAt) {
        Assert.notNull(id, "id must not be null");
        Assert.notNull(uuid, "uuid must not be null");
        Assert.notNull(createdById, "createdById must not be null");
        Assert.notNull(likedAt, "likedAt must not be null");
        this.id = id;
        this.uuid = uuid;
        this.title = title == null ? "" : title;
        this.description = description == null ? "" : description;
        this.createdById = createdById;
        this.createdByName = createdByName == null ? "" : createdByName;
        this.likedAt = likedAt;
    }
}
