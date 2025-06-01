package com.gongdel.promptserver.domain.model;

import com.gongdel.promptserver.domain.exception.PromptValidationException;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * 프롬프트 템플릿 버전 정보를 담는 도메인 모델입니다. 프롬프트 템플릿의 버전별 내용과 메타데이터를 관리합니다.
 */
@Getter
@Slf4j
public class PromptVersion {

    // 상수 정의
    private static final int MIN_CONTENT_LENGTH = 1;

    // 필수 필드
    private Long id;
    private Long promptTemplateId;
    private Integer versionNumber;
    private String content;
    private String changes;
    private Long createdById;
    private LocalDateTime createdAt;
    private List<InputVariable> inputVariables;
    private PromptVersionActionType actionType;
    private UUID uuid;

    /**
     * 프롬프트 버전 생성자
     *
     * @param id               버전 ID
     * @param promptTemplateId 템플릿 ID
     * @param versionNumber    버전 번호
     * @param content          버전 내용
     * @param changes          변경 사항
     * @param createdById      생성자 ID
     * @param inputVariables   변수 정의 (JSON 스키마)
     * @param actionType       작업 유형
     * @param createdAt        생성 시간
     * @param uuid             고유 식별자
     * @throws PromptValidationException 유효성 검증에 실패한 경우
     */
    @Builder
    public PromptVersion(
        Long id,
        Long promptTemplateId,
        Integer versionNumber,
        String content,
        String changes,
        Long createdById,
        List<InputVariable> inputVariables,
        PromptVersionActionType actionType,
        LocalDateTime createdAt,
        UUID uuid) throws PromptValidationException {

        // 필수 필드 유효성 검증
        validatePromptTemplateId(promptTemplateId);
        validateContent(content);
        validateCreatedById(createdById);
        validateVersionNumber(versionNumber);
        validateActionType(actionType);
        validateUuid(uuid);
        validateInputVariables(inputVariables);

        this.id = id;
        this.promptTemplateId = promptTemplateId;
        this.versionNumber = versionNumber;
        this.content = content;
        this.changes = changes;
        this.createdById = createdById;
        this.inputVariables = inputVariables != null ? new java.util.ArrayList<>(inputVariables)
            : new java.util.ArrayList<>();
        this.actionType = actionType;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.uuid = uuid;

        log.debug("Created prompt version: id={}, promptTemplateId={}, versionNumber={}",
            this.id, this.promptTemplateId, this.versionNumber);
    }

    /**
     * 템플릿 ID 유효성 검증
     *
     * @param promptTemplateId 검증할 템플릿 ID
     * @throws PromptValidationException 유효성 검증에 실패한 경우
     */
    private void validatePromptTemplateId(Long promptTemplateId) throws PromptValidationException {
        if (promptTemplateId == null) {
            throw new PromptValidationException("템플릿 ID는 필수입니다");
        }
    }

    /**
     * 버전 내용 유효성 검증
     *
     * @param content 검증할 내용
     * @throws PromptValidationException 유효성 검증에 실패한 경우
     */
    private void validateContent(String content) throws PromptValidationException {
        if (StringUtils.isBlank(content)) {
            throw new PromptValidationException("버전 내용은 필수입니다");
        }

        if (content.length() < MIN_CONTENT_LENGTH) {
            throw new PromptValidationException(
                String.format("버전 내용은 최소 %d자 이상이어야 합니다", MIN_CONTENT_LENGTH));
        }
    }

    /**
     * 생성자 ID 유효성 검증
     *
     * @param createdById 검증할 생성자 ID
     * @throws PromptValidationException 유효성 검증에 실패한 경우
     */
    private void validateCreatedById(Long createdById) throws PromptValidationException {
        if (createdById == null) {
            throw new PromptValidationException("생성자 ID는 필수입니다");
        }
    }

    /**
     * 버전 번호 유효성 검증
     *
     * @param versionNumber 검증할 버전 번호
     * @throws PromptValidationException 유효성 검증에 실패한 경우
     */
    private void validateVersionNumber(Integer versionNumber) throws PromptValidationException {
        if (versionNumber == null || versionNumber < 1) {
            throw new PromptValidationException("버전 번호는 1 이상이어야 합니다");
        }
    }

    /**
     * 작업 유형 유효성 검증
     *
     * @param actionType 검증할 작업 유형
     * @throws PromptValidationException 유효성 검증에 실패한 경우
     */
    private void validateActionType(PromptVersionActionType actionType) throws PromptValidationException {
        if (actionType == null) {
            throw new PromptValidationException("작업 유형은 필수입니다");
        }
    }

    /**
     * uuid 유효성 검증
     *
     * @param uuid 검증할 uuid
     * @throws PromptValidationException 유효성 검증에 실패한 경우
     */
    private void validateUuid(UUID uuid) throws PromptValidationException {
        if (uuid == null) {
            throw new PromptValidationException("uuid는 필수입니다");
        }
    }

    /**
     * 입력 변수 목록의 유효성을 검증합니다.
     *
     * @param inputVariables 입력 변수 목록
     * @throws PromptValidationException 유효성 검증에 실패한 경우
     */
    private void validateInputVariables(List<InputVariable> inputVariables) throws PromptValidationException {
        if (inputVariables != null) {
            java.util.Set<String> names = new HashSet<>();
            for (InputVariable variable : inputVariables) {
                if (!names.add(variable.getName())) {
                    throw new PromptValidationException("중복된 변수명이 있습니다: " + variable.getName());
                }
                if (org.apache.commons.lang3.StringUtils.isBlank(variable.getName())) {
                    throw new PromptValidationException("변수명은 비어 있을 수 없습니다.");
                }
                // 필요하다면 추가 검증(타입, 필수 여부 등)
            }
        }
    }
}
