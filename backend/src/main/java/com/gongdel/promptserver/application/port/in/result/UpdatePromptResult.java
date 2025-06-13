package com.gongdel.promptserver.application.port.in.result;

import com.gongdel.promptserver.domain.model.InputVariable;
import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.model.Visibility;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 프롬프트 수정 결과(애플리케이션 계층용) DTO입니다.
 */
@Getter
public class UpdatePromptResult {
    private final UUID uuid;
    private final String title;
    private final String content;
    private final String description;
    private final Long categoryId;
    private final Set<String> tags;
    private final List<InputVariable> inputVariables;
    private final Visibility visibility;
    private final PromptStatus status;
    private final LocalDateTime updatedAt;

    @Builder
    public UpdatePromptResult(
        UUID uuid,
        String title,
        String content,
        String description,
        Long categoryId,
        Set<String> tags,
        List<InputVariable> inputVariables,
        Visibility visibility,
        PromptStatus status,
        LocalDateTime updatedAt) {
        Assert.notNull(uuid, "uuid must not be null");
        Assert.hasText(title, "title must not be blank");
        Assert.hasText(content, "content must not be blank");
        Assert.notNull(categoryId, "categoryId must not be null");
        Assert.notNull(visibility, "visibility must not be null");
        Assert.notNull(status, "status must not be null");
        Assert.notNull(updatedAt, "updatedAt must not be null");

        this.uuid = uuid;
        this.title = title;
        this.content = content;
        this.description = description;
        this.categoryId = categoryId;
        this.tags = tags != null ? tags : Collections.emptySet();
        this.inputVariables = inputVariables != null ? inputVariables : Collections.emptyList();
        this.visibility = visibility;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    public static UpdatePromptResult from(com.gongdel.promptserver.domain.model.PromptTemplate template,
                                          com.gongdel.promptserver.domain.model.PromptVersion version,
                                          java.util.Set<com.gongdel.promptserver.domain.model.Tag> tags) {
        java.util.Set<String> tagNames = tags == null ? java.util.Collections.emptySet()
            : tags.stream().map(com.gongdel.promptserver.domain.model.Tag::getName)
            .collect(java.util.stream.Collectors.toSet());
        return UpdatePromptResult.builder()
            .uuid(template.getUuid())
            .title(template.getTitle())
            .content(version.getContent())
            .description(template.getDescription())
            .categoryId(template.getCategoryId())
            .tags(tagNames)
            .inputVariables(version.getInputVariables())
            .visibility(template.getVisibility())
            .status(template.getStatus())
            .updatedAt(template.getUpdatedAt())
            .build();
    }
}
