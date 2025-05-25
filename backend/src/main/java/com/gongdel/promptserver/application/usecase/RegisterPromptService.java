package com.gongdel.promptserver.application.usecase;

import com.gongdel.promptserver.application.constant.DevelopmentConstants;
import com.gongdel.promptserver.application.exception.PromptErrorType;
import com.gongdel.promptserver.application.exception.PromptRegistrationException;
import com.gongdel.promptserver.application.port.in.RegisterPromptUseCase;
import com.gongdel.promptserver.application.port.in.command.RegisterPromptCommand;
import com.gongdel.promptserver.application.port.out.PromptTemplateTagRelationPort;
import com.gongdel.promptserver.application.port.out.SavePromptPort;
import com.gongdel.promptserver.application.port.out.SaveTagPort;
import com.gongdel.promptserver.application.port.out.TagPort;
import com.gongdel.promptserver.application.port.out.command.SavePromptVersionPort;
import com.gongdel.promptserver.application.port.out.query.LoadPromptPort;
import com.gongdel.promptserver.domain.exception.PromptValidationException;
import com.gongdel.promptserver.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 프롬프트 템플릿 등록을 위한 서비스 구현체입니다. 헥사고널 아키텍처의 유스케이스 구현으로, 프롬프트 등록 비즈니스 로직을 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = {PromptRegistrationException.class, Exception.class})
public class RegisterPromptService implements RegisterPromptUseCase {

    private final SavePromptPort savePromptPort;
    private final SavePromptVersionPort savePromptVersionPort;
    private final TagPort tagPort;
    private final SaveTagPort saveTagPort;
    private final PromptTemplateTagRelationPort promptTemplateTagRelationPort;
    private final LoadPromptPort loadPromptPort;

    /**
     * 새로운 프롬프트 템플릿을 등록합니다. 입력된 커맨드 객체를 기반으로 프롬프트 템플릿을 생성하고 저장합니다.
     *
     * @param command 프롬프트 등록에 필요한 정보를 담은 커맨드 객체
     * @return 등록된 프롬프트 템플릿
     * @throws PromptRegistrationException 프롬프트 등록 과정에서 오류가 발생한 경우
     */
    @SuppressWarnings("checkstyle:Indentation")
    @Override
    public PromptTemplate registerPrompt(RegisterPromptCommand command) {
        log.debug("Registering new prompt with title: {}", command.getTitle());

        try {
            // 1. 태그 준비 (태그 생성/조회는 트랜잭션 상 먼저 처리)
            Set<Tag> tags = processTagsFromCommand(command);

            // 2. 프롬프트 템플릿 생성 (currentVersionId는 null 상태)
            PromptTemplate promptTemplate = createPromptTemplateFromCommand(command);

            // 3. 프롬프트 템플릿 저장
            PromptTemplate savedPrompt = savePromptPort.savePrompt(promptTemplate);
            log.info("Prompt template saved with ID: {}", savedPrompt.getId());

            // 4. 프롬프트 버전 생성 및 저장 (PromptTemplate ID 설정)
            PromptVersion promptVersion = createInitialVersion(savedPrompt, command);
            PromptVersion savedVersion = savePromptVersionPort.savePromptVersion(promptVersion);
            log.info("Initial prompt version saved with ID: {}", savedVersion.getId());

            // 5. 프롬프트 템플릿에 현재 버전 ID 설정 후 업데이트
            savedPrompt.setCurrentVersionId(savedVersion.getId());
            // TODO:리턴값 프록시 도메인 -> 리팩토링 필요
            savedPrompt = savePromptPort.savePrompt(savedPrompt);
            log.info("Updated prompt template with current version ID: {}", savedVersion.getId());

            // 6. 저장된 프롬프트에 태그 연결
            if (!tags.isEmpty()) {
                promptTemplateTagRelationPort.connectTagsToPrompt(savedPrompt, tags);
                log.info("Connected {} tags to prompt template {}", tags.size(), savedPrompt.getId());
            }

            final UUID uuid = savedPrompt.getUuid();
            PromptTemplate finedPrompt = loadPromptPort.loadPromptByUuid(uuid)
                .orElseThrow(() -> new PromptRegistrationException(
                    PromptErrorType.NOT_FOUND,
                    "프롬프트 템플릿을 찾을 수 없습니다: " + uuid));

            return PromptTemplate.builder()
                .id(finedPrompt.getId())
                .uuid(finedPrompt.getUuid())
                .title(finedPrompt.getTitle())
                .currentVersionId(finedPrompt.getCurrentVersionId())
                .categoryId(finedPrompt.getCategoryId())
                .createdById(finedPrompt.getCreatedById())
                .visibility(finedPrompt.getVisibility())
                .status(finedPrompt.getStatus())
                .description(finedPrompt.getDescription())
                .inputVariables(finedPrompt.getInputVariables())
                .createdAt(finedPrompt.getCreatedAt())
                .updatedAt(finedPrompt.getUpdatedAt())
                .createdBy(finedPrompt.getCreatedBy())
                .category(finedPrompt.getCategory())
                .tags(new ArrayList<>(tags))
                .version(finedPrompt.getVersion())
                .status(finedPrompt.getStatus())
                .build();
        } catch (PromptValidationException e) {
            log.error("Failed to create prompt template: {}", e.getMessage());
            throw new PromptRegistrationException(
                PromptErrorType.VALIDATION_ERROR,
                e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during prompt registration: {}", e.getMessage());
            throw new PromptRegistrationException(
                PromptErrorType.UNKNOWN_ERROR,
                e.getMessage(), e);
        }
    }

    /**
     * 첫 번째 프롬프트 버전을 생성합니다.
     *
     * @param promptTemplate 저장된 프롬프트 템플릿
     * @param command        프롬프트 등록 커맨드
     * @return 생성된 프롬프트 버전
     * @throws PromptValidationException 프롬프트 버전 유효성 검증에 실패한 경우
     */
    private PromptVersion createInitialVersion(PromptTemplate promptTemplate, RegisterPromptCommand command)
        throws PromptValidationException {
        String initialChangesMessage = "프롬프트 템플릿 최초 생성";

        // 버전 생성 전 유효성 검증
        validateVersionCreation(promptTemplate, command);

        PromptVersion promptVersion = PromptVersion.builder()
            .promptTemplateId(promptTemplate.getId())
            .uuid(UUID.randomUUID())
            .versionNumber(1)
            .content(command.getContent())
            .changes(initialChangesMessage)
            .createdById(promptTemplate.getCreatedById())
            .createdAt(LocalDateTime.now())
            .variables(command.getVariablesSchema())
            .actionType(PromptVersionActionType.CREATE)
            .build();

        return promptVersion;
    }

    /**
     * 버전 생성 전 유효성 검증을 수행합니다.
     *
     * @param promptTemplate 프롬프트 템플릿
     * @param command        프롬프트 등록 커맨드
     * @throws PromptValidationException 유효성 검증에 실패한 경우
     */
    private void validateVersionCreation(PromptTemplate promptTemplate, RegisterPromptCommand command)
        throws PromptValidationException {
        if (command.getContent() == null || command.getContent().trim().isEmpty()) {
            throw new PromptValidationException("프롬프트 내용은 필수입니다.");
        }
        if (promptTemplate.getId() == null) {
            throw new PromptValidationException("프롬프트 템플릿 ID가 없습니다.");
        }
        if (promptTemplate.getCreatedById() == null) {
            throw new PromptValidationException("생성자 ID가 없습니다.");
        }
        if (command.getVariablesSchema() != null) {
            validateVariablesSchema(command.getVariablesSchema());
        }
    }

    /**
     * 변수 스키마의 유효성을 검증합니다.
     *
     * @param variablesSchema 검증할 변수 스키마
     * @throws PromptValidationException 유효성 검증에 실패한 경우
     */
    private void validateVariablesSchema(java.util.Map<String, Object> variablesSchema)
        throws PromptValidationException {
        if (variablesSchema.isEmpty()) {
            return;
        }
        for (java.util.Map.Entry<String, Object> entry : variablesSchema.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key == null || key.trim().isEmpty()) {
                throw new PromptValidationException("변수 이름은 비어있을 수 없습니다.");
            }
            if (value == null) {
                throw new PromptValidationException("변수 '" + key + "'의 값은 null일 수 없습니다.");
            }
        }
    }

    /**
     * 커맨드 객체로부터 프롬프트 템플릿 엔티티를 생성합니다.
     *
     * @param command 프롬프트 등록 커맨드
     * @return 생성된 프롬프트 템플릿 엔티티
     * @throws PromptValidationException 프롬프트 템플릿 유효성 검증에 실패한 경우
     */
    private PromptTemplate createPromptTemplateFromCommand(RegisterPromptCommand command)
        throws PromptValidationException {
        // TODO : 임시 사용자 ID 사용 (추후 실제 사용자 관리 시스템으로 교체 필요)
        Long createdById = DevelopmentConstants.TEMP_USER_ID;

        return PromptTemplate.builder()
            .title(command.getTitle())
            .description(command.getDescription())
            .createdById(createdById)
            .visibility(command.getVisibility() != null ? command.getVisibility()
                : (command.getCreatedBy().getTeam() != null ? Visibility.TEAM : Visibility.PRIVATE))
            .categoryId(command.getCategoryId())
            .inputVariables(command.getInputVariables())
            .status(command.getStatus())
            .build();
    }

    /**
     * 커맨드 객체로부터 태그를 처리합니다. 존재하는 태그는 재사용하고, 새로운 태그는 생성합니다.
     *
     * @param command 프롬프트 등록 커맨드
     * @return 처리된 태그 세트
     */
    private Set<Tag> processTagsFromCommand(RegisterPromptCommand command) {
        Set<Tag> tags = new HashSet<>();

        if (command.getTags() == null || command.getTags().isEmpty()) {
            return tags;
        }

        for (String tagName : command.getTags()) {
            // 태그 이름 정리 (앞뒤 공백 제거)
            String trimmedTagName = tagName.trim();
            if (trimmedTagName.isEmpty()) {
                continue;
            }

            // TODO: 로직 보완 필요
            // 기존 태그가 있는지 확인하고 없으면 새로 생성
            Tag tag = tagPort.findByName(trimmedTagName)
                .orElseGet(() -> {
                    log.debug("Creating new tag: {}", trimmedTagName);
                    Tag newTag = Tag.create(trimmedTagName);
                    return saveTagPort.saveTag(newTag);
                });

            tags.add(tag);
        }

        log.debug("Processed {} tags", tags.size());
        return tags;
    }
}
