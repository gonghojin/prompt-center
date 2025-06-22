package com.gongdel.promptserver.adapter.out.persistence;

import com.gongdel.promptserver.adapter.out.persistence.entity.PromptTemplateEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.PromptTemplateTagEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.TagEntity;
import com.gongdel.promptserver.adapter.out.persistence.mapper.PromptTemplateMapper;
import com.gongdel.promptserver.adapter.out.persistence.mapper.TagMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.PromptTemplateJpaRepository;
import com.gongdel.promptserver.adapter.out.persistence.repository.PromptTemplateTagRepository;
import com.gongdel.promptserver.adapter.out.persistence.repository.TagRepository;
import com.gongdel.promptserver.application.port.out.PromptTemplateTagRelationPort;
import com.gongdel.promptserver.domain.exception.PromptErrorType;
import com.gongdel.promptserver.domain.exception.PromptOperationException;
import com.gongdel.promptserver.domain.exception.PromptValidationException;
import com.gongdel.promptserver.domain.model.PromptTemplate;
import com.gongdel.promptserver.domain.model.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 프롬프트 템플릿과 태그 간의 연결을 관리하는 어댑터 구현체
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class PromptTemplateTagPersistenceAdapter implements PromptTemplateTagRelationPort {

    private final PromptTemplateJpaRepository promptTemplateRepository;
    private final TagRepository tagRepository;
    private final PromptTemplateTagRepository promptTagRepository;
    private final TagMapper tagMapper;
    private final PromptTemplateMapper promptTemplateMapper;
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 프롬프트 템플릿에 태그를 연결합니다. 기존 태그와 신규 태그 모두 처리합니다.
     *
     * @param promptTemplate 태그를 연결할 프롬프트 템플릿
     * @param tags           연결할 태그 목록
     * @return 태그가 연결된 프롬프트 템플릿
     * @throws PromptValidationException 입력값이 유효하지 않은 경우
     * @throws PromptOperationException  영속성 오류 또는 알 수 없는 오류가 발생한 경우
     */
    @Override
    public PromptTemplate connectTagsToPrompt(PromptTemplate promptTemplate, Set<Tag> tags) {
        try {
            Assert.notNull(promptTemplate, "PromptTemplate must not be null");
            Assert.notNull(tags, "Tags must not be null");

            log.debug("Start connecting tags. Prompt ID: {}, Tag count: {}", promptTemplate.getId(), tags.size());

            if (tags.isEmpty()) {
                log.debug("No tags to connect. Returning original prompt template. ID: {}", promptTemplate.getId());
                return promptTemplate;
            }

            PromptTemplateEntity entity = getPromptTemplateEntity(promptTemplate.getId());

            // 한 번의 순회로 ID/이름 분리
            Set<Long> tagIds = new HashSet<>();
            Set<String> tagNames = new HashSet<>();
            for (Tag tag : tags) {
                if (tag.getId() != null)
                    tagIds.add(tag.getId());
                else if (tag.getName() != null && !tag.getName().isBlank())
                    tagNames.add(tag.getName());
            }

            connectExistingTags(entity, tagIds);
            createAndConnectNewTags(entity, tagNames);

            log.info("Completed connecting tags to prompt. Prompt ID: {}", promptTemplate.getId());
            return promptTemplateMapper.toDomain(entity);
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument: {}", e.getMessage(), e);
            throw new PromptValidationException("Invalid argument: " + e.getMessage(), e);
        } catch (DataAccessException e) {
            log.error("Database error while connecting tags: {}",
                promptTemplate != null ? promptTemplate.getId() : null, e);
            throw new PromptOperationException(PromptErrorType.PERSISTENCE_ERROR,
                "Database error occurred while connecting tags", e);
        } catch (Exception e) {
            log.error("Unexpected error while connecting tags: {}",
                promptTemplate != null ? promptTemplate.getId() : null, e);
            throw new PromptOperationException(PromptErrorType.UNKNOWN_ERROR,
                "Unexpected error occurred while connecting tags", e);
        }
    }

    private PromptTemplateEntity getPromptTemplateEntity(Long promptTemplateId) {
        try {
            Assert.notNull(promptTemplateId, "PromptTemplate ID must not be null");
            return promptTemplateRepository.findById(promptTemplateId)
                .orElseThrow(() -> new PromptValidationException("Prompt template not found: " + promptTemplateId));
        } catch (IllegalArgumentException e) {
            log.error("Invalid prompt template ID: {}", promptTemplateId, e);
            throw new PromptValidationException("Invalid prompt template ID: " + promptTemplateId, e);
        } catch (DataAccessException e) {
            log.error("Database error while retrieving prompt template: {}", promptTemplateId, e);
            throw new PromptOperationException(PromptErrorType.PERSISTENCE_ERROR,
                "Database error occurred while retrieving prompt template", e);
        }
    }

    private void connectExistingTags(PromptTemplateEntity entity, Set<Long> tagIds) {
        if (tagIds.isEmpty())
            return;
        try {
            Set<TagEntity> tagEntities = new HashSet<>(tagRepository.findAllById(tagIds));
            for (TagEntity tagEntity : tagEntities) {
                associateTagWithPrompt(entity, tagEntity);
            }
        } catch (DataAccessException e) {
            log.error("Database error while connecting existing tags. Prompt ID: {}", entity.getId(), e);
            throw new PromptOperationException(PromptErrorType.PERSISTENCE_ERROR,
                "Database error occurred while connecting existing tags", e);
        }
    }

    private void createAndConnectNewTags(PromptTemplateEntity entity, Set<String> tagNames) {
        if (tagNames.isEmpty())
            return;
        for (String tagName : tagNames) {
            try {
                TagEntity tagEntity = tagRepository.findByName(tagName)
                    .orElseGet(
                        () -> tagRepository.save(
                            tagMapper.toEntity(Tag.create(tagName))));
                associateTagWithPrompt(entity, tagEntity);
            } catch (DataAccessException e) {
                log.error("Database error while creating or connecting new tag: {}. Prompt ID: {}", tagName,
                    entity.getId(), e);
                throw new PromptOperationException(PromptErrorType.PERSISTENCE_ERROR,
                    "Database error occurred while creating or connecting new tag: " + tagName, e);
            }
        }
    }

    private void associateTagWithPrompt(PromptTemplateEntity entity, TagEntity tagEntity) {
        try {
            if (!promptTagRepository.existsByPromptTemplateAndTag(entity, tagEntity)) {
                PromptTemplateTagEntity promptTagEntity = new PromptTemplateTagEntity(entity, tagEntity);
                promptTagRepository.save(promptTagEntity);
                log.info("Tag associated: Prompt={}, Tag={}", entity.getId(), tagEntity.getName());
            } else {
                log.debug("Tag already associated: Prompt={}, Tag={}", entity.getId(), tagEntity.getName());
            }
        } catch (DataAccessException e) {
            log.error("Database error while associating tag. Prompt ID: {}, Tag: {}", entity.getId(),
                tagEntity.getName(), e);
            throw new PromptOperationException(PromptErrorType.PERSISTENCE_ERROR,
                "Database error occurred while associating tag: " + tagEntity.getName(), e);
        }
    }

    @Override
    public PromptTemplate updateTagsOfPrompt(PromptTemplate promptTemplate, Set<Tag> newTags) {
        Assert.notNull(promptTemplate, "PromptTemplate must not be null");
        Assert.notNull(newTags, "Tags must not be null");
        log.info("Start updating tags for prompt. Prompt ID: {}, New tag count: {}", promptTemplate.getId(),
            newTags.size());

        // 1. 프롬프트 엔티티 조회 (영속성 컨텍스트 보장)
        PromptTemplateEntity entity = promptTemplateRepository.findById(promptTemplate.getId())
            .orElseThrow(
                () -> new PromptValidationException("Prompt template not found: " + promptTemplate.getId()));

        // 2. 기존 태그 관계 모두 컬렉션에서 제거 (orphanRemoval=true)
        entity.getTagRelations().clear();
        log.debug("Cleared all existing tag relations for prompt. Prompt ID: {}", promptTemplate.getId());

        // 3. 새 태그 연결 (중복 방지)
        for (Tag tag : newTags) {
            TagEntity tagEntity = (tag.getId() != null)
                ? tagRepository.findById(tag.getId()).orElse(null)
                : tagRepository.findByName(tag.getName()).orElse(null);
            if (tagEntity == null) {
                tagEntity = tagRepository.save(tagMapper.toEntity(tag));
            }
            entity.addTag(tagEntity);
        }
        log.info("Completed updating tags for prompt. Prompt ID: {}, Final tag count: {}", promptTemplate.getId(),
            newTags.size());
        return promptTemplateMapper.toDomain(entity);
    }

    @Override
    public Set<Tag> findTagsByPromptTemplateId(Long promptTemplateId) {
        Assert.notNull(promptTemplateId, "PromptTemplateId must not be null");
        log.debug("Retrieving tags for prompt template ID: {}", promptTemplateId);
        Set<Tag> tags = new HashSet<>();
        // PromptTemplateTagEntity에서 promptTemplateId로 조회
        List<PromptTemplateTagEntity> promptTagEntities = promptTagRepository
            .findAllByPromptTemplateId(promptTemplateId);
        for (PromptTemplateTagEntity promptTagEntity : promptTagEntities) {
            TagEntity tagEntity = promptTagEntity.getTag();
            tags.add(tagMapper.toDomain(tagEntity));
        }
        log.debug("Retrieved {} tags for prompt template ID: {}", tags.size(), promptTemplateId);
        return tags;
    }
}
