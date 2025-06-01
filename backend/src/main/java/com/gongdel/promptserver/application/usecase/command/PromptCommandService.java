package com.gongdel.promptserver.application.usecase.command;

import com.gongdel.promptserver.application.constant.DevelopmentConstants;
import com.gongdel.promptserver.application.dto.RegisterPromptResponse;
import com.gongdel.promptserver.application.exception.PromptErrorType;
import com.gongdel.promptserver.application.exception.PromptRegistrationException;
import com.gongdel.promptserver.application.port.in.PromptCommandUseCase;
import com.gongdel.promptserver.application.port.in.command.RegisterPromptCommand;
import com.gongdel.promptserver.application.port.out.PromptTemplateTagRelationPort;
import com.gongdel.promptserver.application.port.out.SavePromptPort;
import com.gongdel.promptserver.application.port.out.SaveTagPort;
import com.gongdel.promptserver.application.port.out.command.SavePromptVersionPort;
import com.gongdel.promptserver.application.port.out.query.LoadPromptPort;
import com.gongdel.promptserver.application.port.out.query.LoadTagPort;
import com.gongdel.promptserver.domain.exception.PromptValidationException;
import com.gongdel.promptserver.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 프롬프트 템플릿 등록을 위한 서비스 구현체입니다.
 * 헥사고널 아키텍처의 유스케이스 구현으로, 프롬프트 등록 비즈니스 로직을 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PromptCommandService implements PromptCommandUseCase {

    private final SavePromptPort savePromptPort;
    private final SavePromptVersionPort savePromptVersionPort;
    private final SaveTagPort saveTagPort;

    private final PromptTemplateTagRelationPort promptTemplateTagRelationPort;
    private final LoadPromptPort loadPromptPort;
    private final LoadTagPort loadTagPort;


    /**
     * 새로운 프롬프트 템플릿을 등록합니다. 입력된 커맨드 객체를 기반으로 프롬프트 템플릿을 생성하고 저장합니다.
     *
     * @param command 프롬프트 등록에 필요한 정보를 담은 커맨드 객체
     * @return 등록된 프롬프트 정보 응답 DTO
     * @throws PromptRegistrationException 프롬프트 등록 과정에서 오류가 발생한 경우
     */
    @Override
    public RegisterPromptResponse registerPrompt(RegisterPromptCommand command) {
        log.info("Start prompt registration. Title: {}", command.getTitle());
        try {
            Set<Tag> tags = extractOrCreateTags(command);
            PromptTemplate promptTemplate = buildPromptTemplate(command);
            PromptTemplate savedPrompt = savePromptPort.savePrompt(promptTemplate);
            log.info("Prompt template saved. ID: {}", savedPrompt.getId());

            PromptVersion savedVersion = createAndSaveInitialVersion(savedPrompt, command);
            log.info("Initial prompt version saved. ID: {}", savedVersion.getId());

            updatePromptWithCurrentVersion(savedPrompt, savedVersion.getId());

            connectTagsIfPresent(savedPrompt, tags);

            PromptTemplate foundPrompt = loadPromptByUuidOrThrow(savedPrompt.getUuid());
            return RegisterPromptResponse.from(foundPrompt, tags);
        } catch (PromptValidationException e) {
            log.error("Prompt validation failed: {}", e.getMessage());
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
     * 커맨드 객체로부터 태그를 추출하거나 새로 생성합니다.
     * 중복 및 공백 태그는 미리 제거하여 DB 접근을 최소화합니다.
     *
     * @param command 프롬프트 등록 커맨드
     * @return 처리된 태그 세트
     */
    private Set<Tag> extractOrCreateTags(RegisterPromptCommand command) {
        Set<Tag> tags = new HashSet<>();
        if (command.getTags() == null || command.getTags().isEmpty()) {
            return tags;
        }
        // 중복 및 공백 태그 미리 제거
        Set<String> uniqueTagNames = new HashSet<>();
        for (String tagName : command.getTags()) {
            String trimmed = tagName == null ? "" : tagName.trim();
            if (!trimmed.isEmpty()) {
                uniqueTagNames.add(trimmed);
            }
        }
        for (String tagName : uniqueTagNames) {
            Tag tag = loadTagPort.loadTagByName(tagName)
                .orElseGet(() -> {
                    log.debug("Creating new tag: {}", tagName);
                    return saveTagPort.saveTag(Tag.create(tagName));
                });
            tags.add(tag);
        }
        log.info("Processed {} tags", tags.size());
        return tags;
    }

    /**
     * 커맨드 객체로부터 프롬프트 템플릿 엔티티를 생성합니다.
     *
     * @param command 프롬프트 등록 커맨드
     * @return 생성된 프롬프트 템플릿 엔티티
     * @throws PromptValidationException 프롬프트 템플릿 유효성 검증에 실패한 경우
     */
    private PromptTemplate buildPromptTemplate(RegisterPromptCommand command) throws PromptValidationException {
        Long createdById = DevelopmentConstants.TEMP_USER_ID;
        Visibility visibility;
        if (command.getVisibility() != null) {
            visibility = command.getVisibility();
        } else if (command.getCreatedBy() != null && command.getCreatedBy().getTeam() != null) {
            visibility = Visibility.TEAM;
        } else {
            visibility = Visibility.PRIVATE;
        }
        return PromptTemplate.newTemplateForInitialRegistration(
            command.getTitle(),
            command.getDescription(),
            createdById,
            visibility,
            command.getCategoryId(),
            command.getStatus());
    }

    /**
     * 첫 번째 프롬프트 버전을 생성하고 저장합니다.
     *
     * @param promptTemplate 저장된 프롬프트 템플릿
     * @param command        프롬프트 등록 커맨드
     * @return 저장된 프롬프트 버전
     * @throws PromptValidationException 프롬프트 버전 유효성 검증에 실패한 경우
     */
    private PromptVersion createAndSaveInitialVersion(PromptTemplate promptTemplate, RegisterPromptCommand command)
        throws PromptValidationException {
        validateVersionCreation(promptTemplate, command);
        return savePromptVersionPort.savePromptVersion(
            PromptVersion.builder()
                .promptTemplateId(promptTemplate.getId())
                .uuid(UUID.randomUUID())
                .versionNumber(1)
                .content(command.getContent())
                .changes("프롬프트 템플릿 최초 생성")
                .createdById(promptTemplate.getCreatedById())
                .createdAt(LocalDateTime.now())
                .inputVariables(command.getInputVariables())
                .actionType(PromptVersionActionType.CREATE)
                .build());
    }

    /**
     * 프롬프트 템플릿에 현재 버전 ID를 설정하고 저장합니다.
     *
     * @param promptTemplate   프롬프트 템플릿
     * @param currentVersionId 현재 버전 ID
     */
    private void updatePromptWithCurrentVersion(PromptTemplate promptTemplate, Long currentVersionId) {
        promptTemplate.setCurrentVersionId(currentVersionId);
        savePromptPort.savePrompt(promptTemplate);
        log.info("Prompt template updated with current version ID: {}", currentVersionId);
    }

    /**
     * 태그가 존재할 경우 프롬프트에 연결합니다.
     *
     * @param promptTemplate 프롬프트 템플릿
     * @param tags           태그 세트
     */
    private void connectTagsIfPresent(PromptTemplate promptTemplate, Set<Tag> tags) {
        if (!tags.isEmpty()) {
            promptTemplateTagRelationPort.connectTagsToPrompt(promptTemplate, tags);
            log.info("Connected {} tags to prompt template {}", tags.size(), promptTemplate.getId());
        }
    }

    /**
     * 프롬프트 UUID로 프롬프트를 조회하고, 없으면 예외를 발생시킵니다.
     *
     * @param uuid 프롬프트 UUID
     * @return 조회된 프롬프트 템플릿
     * @throws PromptRegistrationException 프롬프트를 찾을 수 없는 경우
     */
    private PromptTemplate loadPromptByUuidOrThrow(UUID uuid) {
        return loadPromptPort.loadPromptByUuid(uuid)
            .orElseThrow(() -> new PromptRegistrationException(
                PromptErrorType.NOT_FOUND,
                "프롬프트 템플릿을 찾을 수 없습니다: " + uuid));
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
    }
}
