package com.gongdel.promptserver.application.usecase.command;

import com.gongdel.promptserver.application.exception.PromptVersionExceptionConverter;
import com.gongdel.promptserver.application.exception.PromptVersionOperationFailedException;
import com.gongdel.promptserver.application.port.in.command.CreatePromptVersionCommand;
import com.gongdel.promptserver.application.port.in.command.PromptVersionCommandUseCase;
import com.gongdel.promptserver.application.port.in.command.UpdatePromptVersionCommand;
import com.gongdel.promptserver.application.port.out.command.DeletePromptVersionPort;
import com.gongdel.promptserver.application.port.out.command.SavePromptVersionPort;
import com.gongdel.promptserver.application.port.out.command.UpdatePromptVersionPort;
import com.gongdel.promptserver.domain.exception.PromptVersionDomainException;
import com.gongdel.promptserver.domain.model.PromptVersion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 프롬프트 버전 명령 유즈케이스 구현체입니다. 프롬프트 버전의 생성, 수정, 삭제 작업을 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PromptVersionCommandService implements PromptVersionCommandUseCase {

    private final SavePromptVersionPort savePromptVersionPort;
    private final UpdatePromptVersionPort updatePromptVersionPort;
    private final DeletePromptVersionPort deletePromptVersionPort;

    /**
     * 새 프롬프트 버전을 생성합니다.
     *
     * @param command 프롬프트 버전 생성 명령 객체
     * @return 생성된 프롬프트 버전의 ID
     * @throws PromptVersionOperationFailedException 프롬프트 버전 생성 중 오류가 발생한 경우
     */
    @Override
    public Long createPromptVersion(CreatePromptVersionCommand command) {
        validateCreateCommand(command);
        log.debug("Starting prompt version creation for template ID: {}", command.getPromptTemplateId());

        try {
            // 도메인 객체 생성 (유효성 검증 포함)
            PromptVersion promptVersion = PromptVersion.builder()
                .id(null)
                .promptTemplateId(command.getPromptTemplateId())
                .versionNumber(command.getVersionNumber())
                .content(command.getContent())
                .changes(command.getChanges())
                .createdById(command.getCreatedById())
                .inputVariables(command.getInputVariables())
                .actionType(command.getActionType())
                .uuid(command.getUuid())
                .build();

            // 포트를 통해 저장
            PromptVersion saved = savePromptVersionPort.savePromptVersion(promptVersion);
            log.info("PromptVersion created successfully. ID: {}", saved.getId());
            return saved.getId();
        } catch (PromptVersionDomainException e) {
            // 도메인 예외를 애플리케이션 예외로 변환
            throw PromptVersionExceptionConverter.convertToApplicationException(e, command.getPromptTemplateId());
        } catch (Exception e) {
            log.error("Failed to create PromptVersion", e);
            throw new PromptVersionOperationFailedException("프롬프트 버전 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 기존 프롬프트 버전을 업데이트합니다.
     *
     * @param command 프롬프트 버전 업데이트 명령 객체
     * @return 업데이트된 프롬프트 버전의 ID
     * @throws PromptVersionOperationFailedException 프롬프트 버전 업데이트 중 오류가 발생한 경우
     */
    @Override
    public Long updatePromptVersion(UpdatePromptVersionCommand command) {
        validateUpdateCommand(command);
        log.debug("Starting prompt version update. ID: {}", command.getId());

        try {
            // 도메인 객체 생성 및 유효성 검증
            PromptVersion promptVersion = PromptVersion.builder()
                .id(command.getId())
                .promptTemplateId(command.getPromptTemplateId())
                .versionNumber(command.getVersionNumber())
                .content(command.getContent())
                .changes(command.getChanges())
                .createdById(command.getCreatedById())
                .inputVariables(command.getInputVariables())
                .actionType(command.getActionType())
                .uuid(command.getUuid())
                .build();

            // 업데이트 수행
            PromptVersion updated = updatePromptVersionPort.updatePromptVersion(promptVersion);
            log.info("PromptVersion updated successfully. ID: {}", updated.getId());
            return updated.getId();
        } catch (PromptVersionDomainException e) {
            // 도메인 예외를 애플리케이션 예외로 변환
            throw PromptVersionExceptionConverter.convertToApplicationException(e, command.getId());
        } catch (Exception e) {
            log.error("Failed to update PromptVersion. ID: {}", command.getId(), e);
            throw new PromptVersionOperationFailedException("프롬프트 버전 업데이트 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 프롬프트 버전을 삭제합니다.
     *
     * @param id 삭제할 프롬프트 버전 ID
     * @throws PromptVersionOperationFailedException 프롬프트 버전 삭제 중 오류가 발생한 경우
     */
    @Override
    public void deletePromptVersion(Long id) {
        validateDeleteCommand(id);
        log.debug("Starting prompt version deletion. ID: {}", id);

        try {
            deletePromptVersionPort.deletePromptVersion(id);
            log.info("PromptVersion deleted successfully. ID: {}", id);
        } catch (PromptVersionDomainException e) {
            // 도메인 예외를 애플리케이션 예외로 변환
            throw PromptVersionExceptionConverter.convertToApplicationException(e, id);
        } catch (Exception e) {
            log.error("Failed to delete PromptVersion. ID: {}", id, e);
            throw new PromptVersionOperationFailedException("프롬프트 버전 삭제 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private void validateCreateCommand(CreatePromptVersionCommand command) {
        Assert.notNull(command, "Create prompt version command must not be null");
        Assert.notNull(command.getPromptTemplateId(), "Prompt template ID must not be null");
        Assert.notNull(command.getVersionNumber(), "Version number must not be null");
        Assert.hasText(command.getContent(), "Content must not be empty");
        Assert.notNull(command.getCreatedById(), "Created by ID must not be null");
        Assert.notNull(command.getActionType(), "Action type must not be null");
    }

    private void validateUpdateCommand(UpdatePromptVersionCommand command) {
        Assert.notNull(command, "Update prompt version command must not be null");
        Assert.notNull(command.getId(), "Prompt version ID must not be null");
        Assert.notNull(command.getPromptTemplateId(), "Prompt template ID must not be null");
        Assert.notNull(command.getVersionNumber(), "Version number must not be null");
        Assert.hasText(command.getContent(), "Content must not be empty");
        Assert.notNull(command.getCreatedById(), "Created by ID must not be null");
        Assert.notNull(command.getActionType(), "Action type must not be null");
    }

    private void validateDeleteCommand(Long id) {
        Assert.notNull(id, "Prompt version ID must not be null");
    }
}
