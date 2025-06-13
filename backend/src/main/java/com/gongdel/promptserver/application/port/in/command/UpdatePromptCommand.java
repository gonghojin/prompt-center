package com.gongdel.promptserver.application.port.in.command;

import com.gongdel.promptserver.domain.model.InputVariable;
import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.model.Visibility;
import com.gongdel.promptserver.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 프롬프트 수정 유스케이스용 커맨드 객체입니다.
 * editor는 User 도메인 객체를 사용합니다.
 */
@Getter
public class UpdatePromptCommand {
    private final UUID promptTemplateId;
    private final User editor;
    private final String title;
    private final String content;
    private final String description;
    private final Long categoryId;
    private final Set<String> tags;
    private final List<InputVariable> inputVariables;
    private final Visibility visibility;
    private final PromptStatus status;

    /**
     * UpdatePromptCommand 생성자에서 유효성 검사 수행 (Assert 사용, 메서드 분리)
     */
    @Builder
    public UpdatePromptCommand(
        UUID promptTemplateId,
        User editor,
        String title,
        String content,
        String description,
        Long categoryId,
        Set<String> tags,
        List<InputVariable> inputVariables,
        Visibility visibility,
        PromptStatus status) {
        validatePromptTemplateId(promptTemplateId);
        validateEditor(editor);
        validateTitle(title);
        validateContent(content);
        validateCategoryId(categoryId);
        validateVisibility(visibility);
        validateStatus(status);

        this.promptTemplateId = promptTemplateId;
        this.editor = editor;
        this.title = title;
        this.content = content;
        this.description = description;
        this.categoryId = categoryId;
        this.tags = tags != null ? tags : Collections.emptySet();
        this.inputVariables = inputVariables != null ? inputVariables : Collections.emptyList();
        this.visibility = visibility;
        this.status = status;
    }

    private void validatePromptTemplateId(UUID promptTemplateId) {
        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");
    }

    private void validateEditor(User editor) {
        Assert.notNull(editor, "editor must not be null");
    }

    private void validateTitle(String title) {
        Assert.hasText(title, "title must not be blank");
    }

    private void validateContent(String content) {
        Assert.hasText(content, "content must not be blank");
    }

    private void validateCategoryId(Long categoryId) {
        Assert.notNull(categoryId, "categoryId must not be null");
    }

    private void validateVisibility(Visibility visibility) {
        Assert.notNull(visibility, "visibility must not be null");
    }

    private void validateStatus(PromptStatus status) {
        Assert.notNull(status, "status must not be null");
    }
}
