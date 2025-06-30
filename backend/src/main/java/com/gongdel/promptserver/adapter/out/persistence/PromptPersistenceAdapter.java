package com.gongdel.promptserver.adapter.out.persistence;

import com.gongdel.promptserver.adapter.out.persistence.entity.PromptTemplateEntity;
import com.gongdel.promptserver.adapter.out.persistence.repository.PromptTemplateJpaRepository;
import com.gongdel.promptserver.application.port.out.SavePromptPort;
import com.gongdel.promptserver.domain.exception.PromptErrorType;
import com.gongdel.promptserver.domain.exception.PromptOperationException;
import com.gongdel.promptserver.domain.exception.PromptValidationException;
import com.gongdel.promptserver.domain.model.PromptTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * SavePromptPort 인터페이스를 구현하는 영속성 어댑터 클래스입니다. 프롬프트 템플릿을 저장하는 책임을 담당합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class PromptPersistenceAdapter implements SavePromptPort {

    private final PromptTemplateJpaRepository promptTemplateRepository;
    private final com.gongdel.promptserver.adapter.out.persistence.mapper.PromptTemplateMapper promptTemplateMapper;

    /**
     * 프롬프트 템플릿을 저장합니다.
     *
     * @param promptTemplate 저장할 프롬프트 템플릿
     * @return 저장된 프롬프트 템플릿
     * @throws PromptValidationException 템플릿 데이터가 유효하지 않은 경우
     * @throws PromptOperationException  템플릿 저장 중 오류가 발생한 경우
     */
    @Override
    public PromptTemplate savePrompt(PromptTemplate promptTemplate) {
        try {
            // 입력값 유효성 검증
            Assert.notNull(promptTemplate, "Prompt template must not be null");
            Assert.hasText(promptTemplate.getTitle(), "Prompt title must not be empty");

            log.debug("Saving prompt template with title: {}", promptTemplate.getTitle());

            PromptTemplateEntity entity = promptTemplateMapper.toEntity(promptTemplate);

            // 기존 프롬프트라면 기존 태그 관계를 먼저 삭제(관계 테이블 중복 방지)
            if (entity.getId() != null) {
                PromptTemplateEntity managedEntity = promptTemplateRepository.findByIdWithRelations(entity.getId())
                    .orElseThrow(() -> new PromptOperationException(
                        PromptErrorType.NOT_FOUND, "Prompt template not found: " + entity.getUuid()));
                managedEntity.clearTags(); // 기존 태그 관계 제거
                promptTemplateRepository.saveAndFlush(managedEntity); // 관계 테이블에서 기존 row 삭제
            }

            // 새 태그로 저장
            PromptTemplateEntity savedEntity = promptTemplateRepository.saveAndFlush(entity);

            log.info("Prompt template saved successfully. ID: {}", savedEntity.getId());
            return promptTemplateMapper.toDomainWithTags(savedEntity);
        } catch (DataIntegrityViolationException e) {
            log.error("Database constraint violation while saving prompt template: {}", promptTemplate.getTitle(), e);
            throw new PromptOperationException(
                PromptErrorType.DUPLICATE_TITLE,
                "Prompt template with the same title already exists: " + promptTemplate.getTitle(),
                e);
        } catch (DataAccessException e) {
            log.error("Database access error while saving prompt template: {}", promptTemplate.getTitle(), e);
            throw new PromptOperationException(
                PromptErrorType.PERSISTENCE_ERROR,
                "Database error occurred while saving prompt template",
                e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid prompt template data: {}", e.getMessage(), e);
            throw new PromptValidationException(
                "Invalid prompt template data: " + e.getMessage(),
                e);
        } catch (Exception e) {
            log.error("Unexpected error while saving prompt template: {}", promptTemplate.getTitle(), e);
            throw new PromptOperationException(
                PromptErrorType.UNKNOWN_ERROR,
                "Unexpected error occurred while saving prompt template",
                e);
        }
    }
}
