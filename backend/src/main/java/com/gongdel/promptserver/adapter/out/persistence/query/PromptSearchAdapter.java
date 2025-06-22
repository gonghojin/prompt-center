package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.entity.PromptTemplateEntity;
import com.gongdel.promptserver.adapter.out.persistence.repository.*;
import com.gongdel.promptserver.application.port.out.query.SearchPromptsPort;
import com.gongdel.promptserver.domain.exception.PromptErrorType;
import com.gongdel.promptserver.domain.exception.PromptOperationException;
import com.gongdel.promptserver.domain.model.PromptSearchCondition;
import com.gongdel.promptserver.domain.model.PromptSearchResult;
import com.gongdel.promptserver.domain.model.PromptStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 프롬프트 템플릿 검색(Query) 어댑터 구현체입니다. SearchPromptsPort를 구현하며, fetch join 최적화된 JPA
 * 메서드를 사용합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromptSearchAdapter implements SearchPromptsPort {

    private final PromptTemplateQueryRepository promptTemplateQueryRepository;
    private final FavoriteRepository favoriteRepository;
    private final PromptLikeCountRepository promptLikeCountRepository;
    private final PromptLikeJpaRepository promptLikeJpaRepository;

    /**
     * 프롬프트 검색 조건에 따라 프롬프트 목록을 조회합니다.
     * 각 프롬프트의 즐겨찾기 여부(isFavorite)도 함께 반환합니다.
     *
     * @param condition 프롬프트 검색 조건
     * @return 프롬프트 검색 결과 페이지
     * @throws PromptOperationException 프롬프트 검색 실패 시 발생
     */
    @Override
    public Page<PromptSearchResult> searchPrompts(PromptSearchCondition condition) {
        Assert.notNull(condition, "Search condition must not be null");
        log.debug("Searching prompts with condition: {}", condition);
        try {
            // 1. 프롬프트 목록 조회
            Page<PromptTemplateEntity> entityPage = promptTemplateQueryRepository.searchPrompts(condition);

            // 2. 프롬프트 ID 목록 추출
            List<Long> promptTemplateIds = entityPage.getContent().stream()
                .map(PromptTemplateEntity::getId)
                .toList();

            // 3. 즐겨찾기 정보 조회
            Set<Long> favoritePromptIds = getFavoritePromptIds(condition.getUserId(), promptTemplateIds);

            // 4. 좋아요 수 일괄 조회
            Map<Long, Long> likeCountMap = promptLikeCountRepository
                .findLikeCountsByPromptTemplateIds(promptTemplateIds)
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                    PromptLikeCountProjection::getPromptTemplateId,
                    PromptLikeCountProjection::getLikeCount));

            // 5. 내가 좋아요 했는지 일괄 조회
            Set<Long> likedPromptIds = (condition.getUserId() != null && !promptTemplateIds.isEmpty())
                ? new java.util.HashSet<>(promptLikeJpaRepository
                .findPromptTemplateIdsLikedByUser(condition.getUserId(), promptTemplateIds))
                : java.util.Collections.emptySet();

            // 6. 결과 매핑
            return entityPage.map(entity -> toSearchResult(
                entity,
                favoritePromptIds.contains(entity.getId()),
                likeCountMap.getOrDefault(entity.getId(), 0L),
                likedPromptIds.contains(entity.getId())));
        } catch (Exception e) {
            log.error("Failed to search prompts. Error: {}", e.getMessage(), e);
            throw new PromptOperationException(PromptErrorType.OPERATION_FAILED, "프롬프트 검색 실패", e);
        }
    }

    private Set<Long> getFavoritePromptIds(Long userId, List<Long> promptTemplateIds) {
        if (userId == null || promptTemplateIds.isEmpty()) {
            return Collections.emptySet();
        }
        List<Long> ids = favoriteRepository.findPromptTemplateIdsByUserIdAndPromptTemplateIdsIn(userId,
            promptTemplateIds);
        if (ids == null || ids.isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<>(ids);
    }

    private PromptSearchResult toSearchResult(
        PromptTemplateEntity entity,
        boolean isFavorite,
        long likeCount,
        boolean isLiked) {
        validatePromptTemplateEntity(entity);
        PromptStats stats = new PromptStats(0, (int) likeCount); // viewCount=0(미구현), favoriteCount=likeCount, isLiked는
        // PromptStats에 포함X
        return PromptSearchResult.builder()
            .id(entity.getId())
            .uuid(entity.getUuid())
            .title(entity.getTitle())
            .description(Objects.requireNonNullElse(entity.getDescription(), ""))
            .currentVersionId(Objects.requireNonNullElse(entity.getCurrentVersionId(), 0L))
            .categoryId(entity.getCategory().getId())
            .categoryName(entity.getCategory().getName())
            .createdById(entity.getCreatedBy().getId())
            .createdByName(entity.getCreatedBy().getName())
            .tags(extractTagNames(entity))
            .visibility(entity.getVisibility())
            .status(entity.getStatus())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .stats(stats)
            .isFavorite(isFavorite)
            .isLiked(isLiked)
            .build();
    }

    private void validatePromptTemplateEntity(PromptTemplateEntity entity) {
        Objects.requireNonNull(entity.getId(), "PromptTemplate.id must not be null");
        Objects.requireNonNull(entity.getUuid(), "PromptTemplate.uuid must not be null");
        Objects.requireNonNull(entity.getTitle(), "PromptTemplate.title must not be null");
        Objects.requireNonNull(entity.getCategory(), "PromptTemplate.category must not be null");
        Objects.requireNonNull(entity.getCategory().getId(), "PromptTemplate.category.id must not be null");
        Objects.requireNonNull(entity.getCategory().getName(), "PromptTemplate.category.name must not be null");
        Objects.requireNonNull(entity.getCreatedBy(), "PromptTemplate.createdBy must not be null");
        Objects.requireNonNull(entity.getCreatedBy().getId(), "PromptTemplate.createdBy.id must not be null");
        Objects.requireNonNull(entity.getCreatedBy().getName(), "PromptTemplate.createdBy.name must not be null");
        Objects.requireNonNull(entity.getCreatedAt(), "PromptTemplate.createdAt must not be null");
        Objects.requireNonNull(entity.getUpdatedAt(), "PromptTemplate.updatedAt must not be null");
        Objects.requireNonNull(entity.getVisibility(), "PromptTemplate.visibility must not be null");
        Objects.requireNonNull(entity.getStatus(), "PromptTemplate.status must not be null");
    }

    private List<String> extractTagNames(PromptTemplateEntity entity) {
        if (entity.getTagRelations() == null) {
            return Collections.emptyList();
        }
        return entity.getTagRelations().stream()
            .map(rel -> rel.getTag())
            .filter(Objects::nonNull)
            .map(tag -> tag.getName())
            .filter(Objects::nonNull)
            .toList();
    }
}
