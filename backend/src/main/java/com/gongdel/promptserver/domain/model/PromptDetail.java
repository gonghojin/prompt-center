package com.gongdel.promptserver.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
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
}
