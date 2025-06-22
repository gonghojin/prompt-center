package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.entity.CategoryEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.PromptTemplateEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.UserEntity;
import com.gongdel.promptserver.adapter.out.persistence.mapper.UserMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.PromptTemplateJpaRepository;
import com.gongdel.promptserver.application.port.out.query.FindPromptsPort;
import com.gongdel.promptserver.domain.exception.PromptErrorType;
import com.gongdel.promptserver.domain.exception.PromptOperationException;
import com.gongdel.promptserver.domain.model.Category;
import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.model.PromptTemplate;
import com.gongdel.promptserver.domain.model.Visibility;
import com.gongdel.promptserver.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

// Todo : PromptTemplate 클래스 단순화로 인해 불필요한 필드 맵핑 수정 필요

/**
 * 프롬프트 템플릿 목록 조회(Query) 어댑터 구현체입니다. FindPromptsPort를 구현하며, fetch join 최적화된 JPA
 * 메서드를 사용합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromptQueryAdapter implements FindPromptsPort {

    private final PromptTemplateJpaRepository promptTemplateJpaRepository;
    private final UserMapper userMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<PromptTemplate> findPromptsByCreatedByAndStatus(User user, PromptStatus status, Pageable pageable) {
        Objects.requireNonNull(user, "user must not be null");
        Objects.requireNonNull(status, "status must not be null");
        try {
            log.debug("Finding prompts by createdBy: {}, status: {}", user.getId(), status);
            UserEntity userEntity = userMapper.toEntity(user);
            return promptTemplateJpaRepository.findByCreatedByAndStatusWithRelations(userEntity, status, pageable)
                .map(this::toDomainWithRelations);
        } catch (Exception e) {
            log.error("Failed to find prompts by createdBy: {}, status: {}. Error: {}", user.getId(), status,
                e.getMessage(), e);
            throw new PromptOperationException(PromptErrorType.OPERATION_FAILED, "프롬프트 작성자/상태 목록 조회 실패", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<PromptTemplate> findPromptsByVisibilityAndStatus(Visibility visibility, PromptStatus status,
                                                                 Pageable pageable) {
        Objects.requireNonNull(visibility, "visibility must not be null");
        Objects.requireNonNull(status, "status must not be null");
        try {
            log.debug("Finding prompts by visibility: {}, status: {}", visibility, status);
            return promptTemplateJpaRepository.findByVisibilityAndStatusWithRelations(visibility, status, pageable)
                .map(this::toDomainWithRelations);
        } catch (Exception e) {
            log.error("Failed to find prompts by visibility: {}, status: {}. Error: {}", visibility, status,
                e.getMessage(), e);
            throw new PromptOperationException(PromptErrorType.OPERATION_FAILED, "프롬프트 가시성/상태 목록 조회 실패", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<PromptTemplate> findPromptsByCategoryAndStatus(Category category, PromptStatus status,
                                                               Pageable pageable) {
        Objects.requireNonNull(category, "category must not be null");
        Objects.requireNonNull(status, "status must not be null");
        try {
            log.debug("Finding prompts by category: {}, status: {}", category.getId(), status);
            CategoryEntity categoryEntity = CategoryEntity.fromDomain(category);
            return promptTemplateJpaRepository.findByCategoryAndStatusWithRelations(categoryEntity, status, pageable)
                .map(this::toDomainWithRelations);
        } catch (Exception e) {
            log.error("Failed to find prompts by category: {}, status: {}. Error: {}", category.getId(), status,
                e.getMessage(), e);
            throw e;
        }
    }

    private PromptTemplate toDomainWithRelations(PromptTemplateEntity entity) {
        // PromptTemplate 도메인 모델 생성
        return PromptTemplate.builder()
            .id(entity.getId())
            .uuid(entity.getUuid())
            .title(entity.getTitle())
            .description(entity.getDescription())
            .currentVersionId(entity.getCurrentVersionId())
            .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
            .createdById(entity.getCreatedBy() != null ? entity.getCreatedBy().getId() : null)
            .visibility(entity.getVisibility())
            .status(entity.getStatus())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}
