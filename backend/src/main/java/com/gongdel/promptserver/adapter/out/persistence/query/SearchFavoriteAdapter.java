package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.entity.CategoryEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.PromptTemplateEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.UserEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.favorite.FavoriteEntity;
import com.gongdel.promptserver.adapter.out.persistence.repository.FavoriteQueryRepository;
import com.gongdel.promptserver.adapter.out.persistence.repository.PromptLikeCountProjection;
import com.gongdel.promptserver.adapter.out.persistence.repository.PromptLikeCountRepository;
import com.gongdel.promptserver.adapter.out.persistence.repository.PromptLikeJpaRepository;
import com.gongdel.promptserver.application.port.out.query.SearchFavoritePort;
import com.gongdel.promptserver.domain.model.PromptStats;
import com.gongdel.promptserver.domain.model.favorite.FavoritePromptResult;
import com.gongdel.promptserver.domain.model.favorite.FavoriteSearchCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 즐겨찾기 검색(Search) 어댑터 구현체입니다.
 * QueryDSL 기반 동적 검색, 페이징, 정렬, 키워드 등 복합 조건을 지원합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchFavoriteAdapter implements SearchFavoritePort {

    private final FavoriteQueryRepository favoriteQueryRepository;
    private final PromptLikeCountRepository promptLikeCountRepository;
    private final PromptLikeJpaRepository promptLikeJpaRepository;

    /**
     * `
     * {@inheritDoc}
     */
    @Override
    public Page<FavoritePromptResult> searchFavorites(FavoriteSearchCondition condition) {
        Assert.notNull(condition, "검색 조건은 null일 수 없습니다.");
        log.debug("Searching favorites with condition: {}", condition);
        Page<FavoriteEntity> entityPage = favoriteQueryRepository.searchFavorites(condition);

        // 프롬프트 ID 목록 추출
        List<Long> promptIds = entityPage.getContent().stream()
            .map(e -> e.getPromptTemplate().getId())
            .toList();

        // 좋아요 카운트 일괄 조회
        Map<Long, Long> likeCountMap = promptLikeCountRepository
            .findLikeCountsByPromptTemplateIds(promptIds)
            .stream()
            .collect(Collectors.toMap(
                PromptLikeCountProjection::getPromptTemplateId,
                PromptLikeCountProjection::getLikeCount));

        // 내가 좋아요한 프롬프트 ID 일괄 조회
        Set<Long> likedPromptIds = (condition.getUserId() != null && !promptIds.isEmpty())
            ? new HashSet<>(
            promptLikeJpaRepository.findPromptTemplateIdsLikedByUser(condition.getUserId(), promptIds))
            : java.util.Collections.emptySet();

        // 결과 매핑 (likeCount, isLiked 반영)
        return entityPage.map(entity -> toFavoritePromptResult(
            entity,
            likeCountMap.getOrDefault(entity.getPromptTemplate().getId(), 0L),
            likedPromptIds.contains(entity.getPromptTemplate().getId())));
    }

    /**
     * FavoriteEntity를 FavoritePromptResult 도메인 모델로 변환합니다.
     *
     * @param entity    즐겨찾기 엔티티
     * @param likeCount 좋아요 수
     * @param isLiked   내가 좋아요한 프롬프트 여부
     * @return 도메인 모델 FavoritePromptResult
     */
    private FavoritePromptResult toFavoritePromptResult(FavoriteEntity entity, long likeCount, boolean isLiked) {
        Assert.notNull(entity, "FavoriteEntity는 null일 수 없습니다.");
        Assert.notNull(entity.getPromptTemplate(), "PromptTemplateEntity는 null일 수 없습니다.");
        PromptTemplateEntity prompt = entity.getPromptTemplate();
        UserEntity createdBy = prompt.getCreatedBy();
        CategoryEntity category = prompt.getCategory();
        // 태그명 리스트 추출
        List<String> tags = prompt.getTags().stream()
            .map(com.gongdel.promptserver.adapter.out.persistence.entity.TagEntity::getName)
            .toList();

        return FavoritePromptResult.builder()
            .favoriteId(entity.getId())
            .promptId(prompt.getId())
            .promptUuid(prompt.getUuid())
            .title(prompt.getTitle())
            .description(prompt.getDescription())
            .tags(tags)
            .createdById(createdBy != null ? createdBy.getId() : null)
            .createdByName(createdBy != null ? createdBy.getName() : null)
            .categoryId(category != null ? category.getId() : null)
            .categoryName(category != null ? category.getName() : null)
            .visibility(prompt.getVisibility())
            .status(prompt.getStatus())
            .promptCreatedAt(prompt.getCreatedAt())
            .promptUpdatedAt(prompt.getUpdatedAt())
            .favoriteCreatedAt(entity.getCreatedAt())
            .stats(new PromptStats(0, (int) likeCount))
            .isLiked(isLiked)
            .build();
    }
}
