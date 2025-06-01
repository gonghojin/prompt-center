package com.gongdel.promptserver.domain.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 프롬프트 목록 응답용 DTO 클래스입니다.
 * <p>
 * PromptTemplate 엔티티의 주요 정보를 목록 형태로 제공하기 위해 사용됩니다.
 */
@Getter
@Builder
public class PromptListItem {
    private final Long id;
    private final UUID uuid;
    private final String title;
    private final String description;
    private final Long createdById;
    private final Visibility visibility;
    private final PromptStatus status;
    private final Long categoryId;
    private final Long currentVersionId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    /**
     * PromptTemplate 를 PromptListItem DTO로 변환합니다.
     * <p>
     * 필수값이 누락된 경우 IllegalArgumentException이 발생합니다.
     *
     * @param template 변환할 PromptTemplate (null 불가)
     * @return 변환된 PromptListItem
     * @throws IllegalArgumentException 필수값이 누락된 경우
     */
    public static PromptListItem from(PromptTemplate template) {
        Assert.notNull(template, "PromptTemplate must not be null");
        Assert.notNull(template.getId(), "PromptTemplate.id must not be null");
        Assert.notNull(template.getUuid(), "PromptTemplate.uuid must not be null");
        Assert.hasText(template.getTitle(), "PromptTemplate.title must not be empty");
        Assert.notNull(template.getCreatedById(), "PromptTemplate.createdById must not be null");
        Assert.notNull(template.getVisibility(), "PromptTemplate.visibility must not be null");
        Assert.notNull(template.getStatus(), "PromptTemplate.status must not be null");
        Assert.notNull(template.getCreatedAt(), "PromptTemplate.createdAt must not be null");
        Assert.notNull(template.getUpdatedAt(), "PromptTemplate.updatedAt must not be null");

        return PromptListItem.builder()
            .id(template.getId())
            .uuid(template.getUuid())
            .title(template.getTitle())
            .description(template.getDescription())
            .createdById(template.getCreatedById())
            .visibility(template.getVisibility())
            .status(template.getStatus())
            .categoryId(template.getCategoryId())
            .currentVersionId(template.getCurrentVersionId())
            .createdAt(template.getCreatedAt())
            .updatedAt(template.getUpdatedAt())
            .build();
    }
}
