package com.gongdel.promptserver.application.usecase.command;

import com.gongdel.promptserver.application.dto.DeletePromptResponse;
import com.gongdel.promptserver.application.dto.RegisterPromptResponse;
import com.gongdel.promptserver.application.exception.PromptErrorType;
import com.gongdel.promptserver.application.exception.PromptRegistrationException;
import com.gongdel.promptserver.application.port.in.PromptCommandUseCase;
import com.gongdel.promptserver.application.port.in.command.DeletePromptCommand;
import com.gongdel.promptserver.application.port.in.command.RegisterPromptCommand;
import com.gongdel.promptserver.application.port.in.command.UpdatePromptCommand;
import com.gongdel.promptserver.application.port.in.result.UpdatePromptResult;
import com.gongdel.promptserver.application.port.out.PromptTemplateTagRelationPort;
import com.gongdel.promptserver.application.port.out.SavePromptPort;
import com.gongdel.promptserver.application.port.out.SaveTagPort;
import com.gongdel.promptserver.application.port.out.command.SavePromptVersionPort;
import com.gongdel.promptserver.application.port.out.query.LoadPromptPort;
import com.gongdel.promptserver.application.port.out.query.LoadPromptVersionPort;
import com.gongdel.promptserver.application.port.out.query.LoadTagPort;
import com.gongdel.promptserver.domain.exception.PromptOperationException;
import com.gongdel.promptserver.domain.exception.PromptValidationException;
import com.gongdel.promptserver.domain.model.*;
import com.gongdel.promptserver.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final LoadPromptVersionPort loadPromptVersionPort;

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
            Set<Tag> tags = extractOrCreateTags(command.getTags());
            PromptTemplate promptTemplate = buildPromptTemplate(command, tags);
            PromptTemplate savedPrompt = savePromptPort.savePrompt(promptTemplate);
            log.info("Prompt template saved. ID: {}", savedPrompt.getId());

            PromptVersion savedVersion = createAndSaveInitialVersion(savedPrompt, command);
            log.info("Initial prompt version saved. ID: {}", savedVersion.getId());

            updatePromptWithCurrentVersion(savedPrompt, savedVersion.getId());

            PromptTemplate foundPrompt = loadPromptByUuidOrThrow(savedPrompt.getUuid());
            return RegisterPromptResponse.from(foundPrompt, tags);
        } catch (PromptValidationException e) {
            log.error("Prompt validation failed: {}", e.getMessage());
            throw new PromptRegistrationException(
                PromptErrorType.VALIDATION_ERROR,
                e.getMessage(), e);
        }
    }

    /**
     * 프롬프트를 논리적으로 삭제합니다.
     *
     * @param command 삭제 요청 정보를 담은 커맨드 객체
     * @return 삭제된 프롬프트 정보 응답 DTO
     * @throws PromptOperationException 삭제 권한이 없거나 프롬프트를 찾을 수 없는 경우
     */
    @Override
    public DeletePromptResponse deletePrompt(DeletePromptCommand command) {
        Assert.notNull(command, "DeletePromptCommand must not be null");
        PromptTemplate prompt = loadPromptByUuidOrThrow(command.getUuid());
        // 권한 체크 (생성자 또는 관리자만 삭제 가능)
        if (!isDeletableUser(prompt, command.getCurrentUser())) {
            log.warn("User {} does not have permission to delete prompt {}",
                command.getCurrentUser() != null ? command.getCurrentUser().getId() : null, prompt.getId());
            throw new PromptRegistrationException(PromptErrorType.INSUFFICIENT_PERMISSION, "삭제 권한이 없습니다.");
        }
        PromptStatus previousStatus = prompt.getStatus();
        prompt.updateStatus(PromptStatus.DELETED);
        savePromptPort.savePrompt(prompt);
        log.info("Prompt {} deleted by user {}", prompt.getId(),
            command.getCurrentUser() != null ? command.getCurrentUser().getId() : null);
        return DeletePromptResponse.of(
            prompt.getUuid(),
            prompt.getTitle(),
            previousStatus,
            prompt.getUpdatedAt(),
            command.getCurrentUser() != null ? command.getCurrentUser().getUuid() : null);

    }

    /**
     * 프롬프트 템플릿을 수정(소프트 업데이트)합니다.
     *
     * @param command 프롬프트 수정 커맨드
     * @return 수정된 프롬프트 결과
     * @throws PromptOperationException 권한/상태/유효성 등 비즈니스 예외
     */
    @Override
    public UpdatePromptResult updatePrompt(UpdatePromptCommand command) {
        log.info("Start prompt update. TemplateId: {}", command.getPromptTemplateId());
        try {
            // 1. 입력 유효성 검사
            Assert.notNull(command, "UpdatePromptCommand must not be null");

            // 2. 프롬프트 조회 및 권한 확인
            PromptTemplate template = loadPromptByUuidOrThrow(command.getPromptTemplateId());
            if (!isEditableUser(template, command.getEditor())) {
                log.warn("User {} has no permission to edit prompt {}", command.getEditor().getId(), template.getId());
                throw new PromptOperationException(
                    com.gongdel.promptserver.domain.exception.PromptErrorType.INSUFFICIENT_PERMISSION,
                    "수정 권한이 없습니다.");
            }

            // 3. 변경 내용 적용 (title, content, description, categoryId, tags
            // visibility, status 등)
            applyPromptUpdate(template, command);

            // 4. 버전 이력 추가 (EDIT)
            PromptVersion newVersion = createAndSaveEditVersion(template, command);

            // 5. 템플릿 엔티티 업데이트 (현재 버전 정보 갱신)
            template.setCurrentVersionId(newVersion.getId());
            savePromptPort.savePrompt(template);

            // 6. 결과 반환
            Set<Tag> tags = promptTemplateTagRelationPort.findTagsByPromptTemplateId(template.getId());
            return UpdatePromptResult.from(template, newVersion, tags);

        } catch (PromptValidationException e) {
            log.error("Prompt validation failed: {}", e.getMessage());
            throw new PromptOperationException(
                com.gongdel.promptserver.domain.exception.PromptErrorType.VALIDATION_ERROR, e.getMessage(), e);
        }
    }

    /**
     * 커맨드 객체로부터 태그를 추출하거나 새로 생성합니다.
     * 중복 및 공백 태그는 미리 제거하여 DB 접근을 최소화합니다.
     *
     * @param tagNames 태그 이름 리스트
     * @return 처리된 태그 세트
     */
    private Set<Tag> extractOrCreateTags(Set<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        if (tagNames == null || tagNames.isEmpty()) {
            return tags;
        }

        // 공백 제거 및 유효 태그만 추림
        Set<String> uniqueTagNames = tagNames.stream()
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());

        // TODO:동시성 보완하기
        for (String tagName : uniqueTagNames) {
            Tag tag = loadTagPort.loadTagByName(tagName)
                .orElseGet(() -> {
                    log.debug("Creating new tag: {}", tagName);
                    return saveTagPort.saveTag(Tag.create(tagName));
                });
            tags.add(tag);
        }
        log.debug("Processed {} tags", tags.size());
        return tags;
    }

    /**
     * 커맨드 객체로부터 프롬프트 템플릿 엔티티를 생성합니다.
     *
     * @param command 프롬프트 등록 커맨드
     * @return 생성된 프롬프트 템플릿 엔티티
     * @throws PromptValidationException 프롬프트 템플릿 유효성 검증에 실패한 경우
     */
    private PromptTemplate buildPromptTemplate(RegisterPromptCommand command, Set<Tag> tags)
        throws PromptValidationException {
        Long createdById = command.getCreatedBy().getId();
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
            command.getStatus(),
            tags);
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

    /**
     * 프롬프트 삭제 권한이 있는지 확인합니다.
     *
     * @param prompt 프롬프트 템플릿
     * @param user   현재 요청 유저
     * @return 삭제 가능 여부
     */
    private boolean isDeletableUser(PromptTemplate prompt, User user) {
        Assert.notNull(prompt, "PromptTemplate must not be null");
        Assert.notNull(user, "User must not be null");
        boolean result = prompt.getCreatedById().equals(user.getId());
        if (!result) {
            log.warn("User {} does not have permission to delete prompt {}", user.getId(), prompt.getId());
        }
        return result;
    }

    /**
     * 프롬프트 수정 권한이 있는지 확인합니다.
     */
    private boolean isEditableUser(PromptTemplate template, User user) {
        boolean result = template.getCreatedById().equals(user.getId());
        if (!result) {
            log.warn("User {} has no permission to edit prompt {}", user.getId(), template.getId());
        }
        return result;
    }

    /**
     * 프롬프트의 주요 필드를 커맨드 값으로 변경합니다.
     */
    private void applyPromptUpdate(PromptTemplate template, UpdatePromptCommand command) {
        Set<Tag> tags = extractOrCreateTags(command.getTags());
        template.update(
            command.getTitle(),
            template.getCurrentVersionId(), // 버전ID는 버전 생성 후 set
            command.getCategoryId(),
            command.getVisibility(),
            command.getStatus(),
            command.getDescription(),
            tags // 태그 덮어쓰기
        );
    }

    /**
     * EDIT 버전 이력을 생성하고 저장합니다.
     */
    private PromptVersion createAndSaveEditVersion(PromptTemplate template, UpdatePromptCommand command) {
        int nextVersionNumber = getNextVersionNumber(template);
        PromptVersion newVersion = PromptVersion.builder()
            .promptTemplateId(template.getId())
            .uuid(UUID.randomUUID())
            .versionNumber(nextVersionNumber)
            .content(command.getContent())
            .changes("프롬프트 내용 수정") // TODO: 변경 diff 기록 가능
            .createdById(command.getEditor().getId())
            .createdAt(LocalDateTime.now())
            .inputVariables(command.getInputVariables())
            .actionType(PromptVersionActionType.EDIT)
            .build();
        return savePromptVersionPort.savePromptVersion(newVersion);
    }

    private int getNextVersionNumber(PromptTemplate template) {
        if (template.getCurrentVersionId() == null) {
            return 1; // 최초 버전
        }
        return loadPromptVersionPort.loadPromptVersionById(template.getCurrentVersionId())
            .map(PromptVersion::getVersionNumber)
            .map(n -> n + 1)
            .orElse(1); // 예외 상황: 버전이 없으면 1
    }

}
