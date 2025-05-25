package com.gongdel.promptserver.application.port.in.command;

import com.gongdel.promptserver.domain.model.PromptVersionActionType;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

/**
 * 프롬프트 버전 업데이트 요청을 전달하는 커맨드 객체입니다.
 * <p>
 * 프롬프트 버전의 수정 작업에 필요한 모든 정보를 담아 서비스 계층에 전달합니다.
 */
@Getter
@Builder
public class UpdatePromptVersionCommand {

    /**
     * 프롬프트 버전 ID (필수)
     */
    @NotNull
    private final Long id;
    /**
     * 프롬프트 템플릿 ID (필수)
     */
    @NotNull
    private final Long promptTemplateId;
    /**
     * 버전 번호 (필수, 1 이상)
     */
    @NotNull
    @Min(1)
    private final Integer versionNumber;
    /**
     * 프롬프트 내용 (필수)
     */
    @NotBlank
    private final String content;
    /**
     * 변경 사항 설명 (선택)
     */
    private final String changes;
    /**
     * 생성자(수정자) ID (필수)
     */
    @NotNull
    private final Long createdById;
    /**
     * 프롬프트 변수 정의(JSON 스키마, 선택)
     */
    private final Map<String, Object> variables;
    /**
     * 프롬프트 버전 작업 유형 (필수)
     */
    @NotNull
    private final PromptVersionActionType actionType;
    /**
     * 고유 식별자 (필수)
     */
    @NotNull
    private final UUID uuid;

    /**
     * 필수 필드만으로 업데이트 커맨드 생성
     */
    public static UpdatePromptVersionCommand create(
        @NotNull Long id,
        @NotNull Long promptTemplateId,
        @NotNull @Min(1) Integer versionNumber,
        @NotBlank String content,
        String changes,
        @NotNull Long createdById,
        @NotNull PromptVersionActionType actionType,
        @NotNull UUID uuid) {
        return UpdatePromptVersionCommand.builder()
            .id(id)
            .promptTemplateId(promptTemplateId)
            .versionNumber(versionNumber)
            .content(content)
            .changes(changes)
            .createdById(createdById)
            .actionType(actionType)
            .uuid(uuid)
            .build();
    }

    /**
     * 변수 정의를 포함한 업데이트 커맨드 생성
     */
    public static UpdatePromptVersionCommand createWithVariables(
        @NotNull Long id,
        @NotNull Long promptTemplateId,
        @NotNull @Min(1) Integer versionNumber,
        @NotBlank String content,
        String changes,
        @NotNull Long createdById,
        Map<String, Object> variables,
        @NotNull PromptVersionActionType actionType,
        @NotNull UUID uuid) {
        return UpdatePromptVersionCommand.builder()
            .id(id)
            .promptTemplateId(promptTemplateId)
            .versionNumber(versionNumber)
            .content(content)
            .changes(changes)
            .createdById(createdById)
            .variables(variables)
            .actionType(actionType)
            .uuid(uuid)
            .build();
    }
}
