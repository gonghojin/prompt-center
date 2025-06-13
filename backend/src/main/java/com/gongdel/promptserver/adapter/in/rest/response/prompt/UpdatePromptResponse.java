package com.gongdel.promptserver.adapter.in.rest.response.prompt;

import com.gongdel.promptserver.adapter.in.rest.request.prompt.InputVariableDto;
import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.model.Visibility;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 프롬프트 수정 응답 DTO입니다.
 */
@Getter
public class UpdatePromptResponse {
    private final UUID uuid;
    private final String title;
    private final String content;
    private final String description;
    private final Long categoryId;
    private final Set<String> tags;
    private final List<InputVariableDto> inputVariables;
    private final Visibility visibility;
    private final PromptStatus status;
    private final LocalDateTime updatedAt;

    @Builder
    public UpdatePromptResponse(
        UUID uuid,
        String title,
        String content,
        String description,
        Long categoryId,
        Set<String> tags,
        List<InputVariableDto> inputVariables,
        Visibility visibility,
        PromptStatus status,
        LocalDateTime updatedAt) {

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
}
