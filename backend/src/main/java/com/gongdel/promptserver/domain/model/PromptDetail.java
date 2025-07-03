package com.gongdel.promptserver.domain.model;

import com.gongdel.promptserver.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 프롬프트 상세 정보를 담는 도메인 모델입니다.
 */
@Getter
@Builder
public class PromptDetail {
    private final UUID id;
    private final String title;
    private final String description;
    private final String content;
    private final User author;
    private final Set<String> tags;
    private final boolean isPublic;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final int viewCount;
    private final int favoriteCount;
    private final Long categoryId;
    private final String visibility;
    private final String status;
    private final boolean isFavorite;
    private final boolean isLiked;
    private final List<InputVariable> inputVariables;

    @Builder
    public PromptDetail(
        UUID id,
        String title,
        String description,
        String content,
        User author,
        Set<String> tags,
        boolean isPublic,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        int viewCount,
        int favoriteCount,
        Long categoryId,
        String visibility,
        String status,
        boolean isFavorite,
        boolean isLiked,
        List<InputVariable> inputVariables) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.content = content;
        this.author = author;
        this.tags = tags;
        this.isPublic = isPublic;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.viewCount = viewCount;
        this.favoriteCount = favoriteCount;
        this.categoryId = categoryId;
        this.visibility = visibility;
        this.status = status;
        this.isFavorite = isFavorite;
        this.isLiked = isLiked;
        this.inputVariables = inputVariables != null ? inputVariables : Collections.emptyList();
    }
}
