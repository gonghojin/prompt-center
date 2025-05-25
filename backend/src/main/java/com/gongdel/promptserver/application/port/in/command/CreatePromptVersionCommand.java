package com.gongdel.promptserver.application.port.in.command;

import com.gongdel.promptserver.domain.model.PromptVersionActionType;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * 프롬프트 버전 생성 명령 객체
 */
@Getter
@Builder
public class CreatePromptVersionCommand {

    /**
     * 프롬프트 템플릿 ID
     */
    @NonNull
    private final Long promptTemplateId;

    /**
     * 버전 번호
     */
    @NonNull
    private final Integer versionNumber;

    /**
     * 프롬프트 내용
     */
    @NonNull
    private final String content;

    /**
     * 변경 사항 설명
     */
    private final String changes;

    /**
     * 생성자(유저) ID
     */
    @NonNull
    private final Long createdById;

    /**
     * 변수 맵
     */
    private final Map<String, Object> variables;

    /**
     * 액션 타입
     */
    @NonNull
    private final PromptVersionActionType actionType;

    /**
     * UUID
     */
    private final UUID uuid;

    /**
     * 필수 필드만으로 생성하는 팩토리 메서드
     */
    public static CreatePromptVersionCommand create(
        @NonNull Long promptTemplateId,
        @NonNull Integer versionNumber,
        @NonNull String content,
        @NonNull Long createdById,
        @NonNull PromptVersionActionType actionType) {
        return CreatePromptVersionCommand.builder()
            .promptTemplateId(promptTemplateId)
            .versionNumber(versionNumber)
            .content(content)
            .createdById(createdById)
            .actionType(actionType)
            .build();
    }

    /**
     * 변수와 변경사항을 포함하여 생성하는 팩토리 메서드
     */
    public static CreatePromptVersionCommand createWithDetails(
        @NonNull Long promptTemplateId,
        @NonNull Integer versionNumber,
        @NonNull String content,
        String changes,
        Map<String, Object> variables,
        @NonNull Long createdById,
        @NonNull PromptVersionActionType actionType) {
        return CreatePromptVersionCommand.builder()
            .promptTemplateId(promptTemplateId)
            .versionNumber(versionNumber)
            .content(content)
            .changes(changes)
            .variables(variables)
            .createdById(createdById)
            .actionType(actionType)
            .build();
    }
}
