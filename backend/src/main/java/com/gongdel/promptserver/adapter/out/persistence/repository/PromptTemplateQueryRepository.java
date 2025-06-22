package com.gongdel.promptserver.adapter.out.persistence.repository;

import com.gongdel.promptserver.adapter.out.persistence.entity.PromptTemplateEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.QPromptTemplateEntity;
import com.gongdel.promptserver.domain.model.PromptSearchCondition;
import com.gongdel.promptserver.domain.model.PromptSortType;
import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.model.statistics.PromptStatisticsResult;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.gongdel.promptserver.adapter.out.persistence.entity.QPromptTemplateEntity.promptTemplateEntity;
import static com.gongdel.promptserver.adapter.out.persistence.entity.QPromptTemplateTagEntity.promptTemplateTagEntity;
import static com.gongdel.promptserver.adapter.out.persistence.entity.QTagEntity.tagEntity;

/**
 * 프롬프트 템플릿 검색 쿼리 리포지토리
 * <p>
 * 다양한 조건으로 프롬프트 템플릿을 검색합니다.
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class PromptTemplateQueryRepository {
    private final JPAQueryFactory queryFactory;

    /**
     * 프롬프트 템플릿을 검색합니다.
     *
     * @param condition 검색 조건 객체
     * @return 검색된 프롬프트 템플릿 페이지
     */
    public Page<PromptTemplateEntity> searchPrompts(PromptSearchCondition condition) {
        Assert.notNull(condition, "Search condition must not be null");
        Assert.notNull(condition.getPageable(), "Pageable must not be null");
        Assert.isTrue(condition.getPageable().getOffset() >= 0, "Offset must be non-negative");
        Assert.isTrue(condition.getPageable().getPageSize() > 0, "Page size must be positive");

        log.debug(
            "Searching prompts with condition: title={}, description={}, tag={}, categoryId={}, status={}, sortType={}, pageable={}",
            condition.getTitle(), condition.getDescription(), condition.getTag(),
            condition.getCategoryId(), condition.getStatus(), condition.getSortType(), condition.getPageable());

        BooleanExpression[] predicates = buildPredicates(condition);
        OrderSpecifier<?>[] orderSpecifiers = createOrderSpecifiers(condition.getSortType());

        List<PromptTemplateEntity> content = queryFactory
            .selectDistinct(promptTemplateEntity)
            .from(promptTemplateEntity)
            .leftJoin(promptTemplateEntity.createdBy).fetchJoin()
            .leftJoin(promptTemplateEntity.category).fetchJoin()
            .leftJoin(promptTemplateEntity.tagRelations, promptTemplateTagEntity).fetchJoin()
            .leftJoin(promptTemplateTagEntity.tag, tagEntity).fetchJoin()
            .where(predicates)
            .orderBy(orderSpecifiers)
            .offset(condition.getPageable().getOffset())
            .limit(condition.getPageable().getPageSize())
            .fetch();

        Long total = queryFactory
            .select(promptTemplateEntity.countDistinct())
            .from(promptTemplateEntity)
            .leftJoin(promptTemplateEntity.createdBy)
            .leftJoin(promptTemplateEntity.category)
            .leftJoin(promptTemplateEntity.tagRelations, promptTemplateTagEntity)
            .leftJoin(promptTemplateTagEntity.tag, tagEntity)
            .where(predicates)
            .fetchOne();

        long totalCount = Optional.ofNullable(total).orElse(0L);
        log.info("Prompt search result: {} entities found", totalCount);

        return new PageImpl<>(content, condition.getPageable(), totalCount);
    }

    /**
     * 사용자별 프롬프트 상태별 통계를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 상태별 프롬프트 통계 결과
     */
    public PromptStatisticsResult loadPromptStatisticsByUserId(Long userId) {
        Assert.notNull(userId, "userId must not be null");
        QPromptTemplateEntity prompt = QPromptTemplateEntity.promptTemplateEntity;
        List<Tuple> counts = queryFactory
            .select(prompt.status, prompt.count())
            .from(prompt)
            .where(prompt.createdBy.id.eq(userId))
            .groupBy(prompt.status)
            .fetch();

        Map<PromptStatus, Long> statusCounts = counts.stream()
            .collect(Collectors.toMap(
                tuple -> tuple.get(prompt.status),
                tuple -> tuple.get(prompt.count())));

        return PromptStatisticsResult.builder()
            .totalCount(statusCounts.values().stream().mapToLong(Long::longValue).sum())
            .draftCount(statusCounts.getOrDefault(PromptStatus.DRAFT, 0L))
            .publishedCount(statusCounts.getOrDefault(PromptStatus.PUBLISHED, 0L))
            .archivedCount(statusCounts.getOrDefault(PromptStatus.ARCHIVED, 0L))
            .build();
    }

    private BooleanExpression[] buildPredicates(PromptSearchCondition condition) {
        List<BooleanExpression> predicates = new ArrayList<>();

        // 삭제(논리 삭제) 제외 조건 (관리자/특수 조회가 아니면 항상 적용)
        if (!condition.isIncludeDeleted()) {
            predicates.add(notDeleted());
        }

        // 기존 단일 조건
        BooleanExpression status = statusEq(condition.getStatus());
        if (status != null)
            predicates.add(status);
        BooleanExpression title = titleContains(condition.getTitle());
        if (title != null)
            predicates.add(title);
        BooleanExpression description = descriptionContains(condition.getDescription());
        if (description != null)
            predicates.add(description);
        BooleanExpression tag = tagContains(condition.getTag());
        if (tag != null)
            predicates.add(tag);
        BooleanExpression category = categoryIdEq(condition.getCategoryId());
        if (category != null)
            predicates.add(category);

        // --- 확장 조건 추가 ---
        // 다중 상태 필터
        BooleanExpression statusFilters = statusIn(condition.getStatusFilters());
        if (statusFilters != null)
            predicates.add(statusFilters);
        // 다중 공개 범위 필터
        BooleanExpression visibilityFilters = visibilityIn(condition.getVisibilityFilters());
        if (visibilityFilters != null)
            predicates.add(visibilityFilters);
        // 내 프롬프트 조회
        if (condition.isMyPrompts() && condition.getUserId() != null) {
            BooleanExpression userId = userIdEq(condition.getUserId());
            if (userId != null)
                predicates.add(userId);
        }
        // 통합 검색어(제목, 설명, 태그)
        BooleanExpression searchKeyword = searchKeywordContains(condition.getSearchKeyword());
        if (searchKeyword != null)
            predicates.add(searchKeyword);
        // --- 확장 조건 끝 ---

        return predicates.toArray(new BooleanExpression[0]);
    }

    /**
     * status != DELETED 조건 반환
     */
    private BooleanExpression notDeleted() {
        return promptTemplateEntity.status.ne(PromptStatus.DELETED);
    }

    private OrderSpecifier<?>[] createOrderSpecifiers(PromptSortType sortType) {
        if (sortType == null) {
            return new OrderSpecifier[]{promptTemplateEntity.updatedAt.desc()};
        }
        return switch (sortType) {
            case LATEST_MODIFIED -> new OrderSpecifier[]{promptTemplateEntity.updatedAt.desc()};
            case TITLE -> new OrderSpecifier[]{promptTemplateEntity.title.asc()};
        };
    }

    private BooleanExpression statusEq(PromptStatus status) {
        return status != null ? promptTemplateEntity.status.eq(status) : null;
    }

    private BooleanExpression titleContains(String title) {
        return StringUtils.hasText(title)
            ? promptTemplateEntity.title.containsIgnoreCase(title)
            : null;
    }

    private BooleanExpression descriptionContains(String description) {
        return StringUtils.hasText(description)
            ? promptTemplateEntity.description.containsIgnoreCase(description)
            : null;
    }

    private BooleanExpression tagContains(String tag) {
        return StringUtils.hasText(tag)
            ? tagEntity.name.containsIgnoreCase(tag)
            : null;
    }

    private BooleanExpression categoryIdEq(Long categoryId) {
        return categoryId != null
            ? promptTemplateEntity.category.id.eq(categoryId)
            : null;
    }

    // 다중 상태 필터
    private BooleanExpression statusIn(java.util.Set<PromptStatus> statuses) {
        return (statuses != null && !statuses.isEmpty()) ? promptTemplateEntity.status.in(statuses) : null;
    }

    // 다중 공개 범위 필터
    private BooleanExpression visibilityIn(
        java.util.Set<com.gongdel.promptserver.domain.model.Visibility> visibilities) {
        return (visibilities != null && !visibilities.isEmpty()) ? promptTemplateEntity.visibility.in(visibilities)
            : null;
    }

    // 사용자 ID 조건
    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? promptTemplateEntity.createdBy.id.eq(userId) : null;
    }

    // 통합 검색어(제목, 설명, 태그)
    private BooleanExpression searchKeywordContains(String keyword) {
        if (!StringUtils.hasText(keyword))
            return null;
        return promptTemplateEntity.title.containsIgnoreCase(keyword)
            .or(promptTemplateEntity.description.containsIgnoreCase(keyword))
            .or(tagEntity.name.containsIgnoreCase(keyword));
    }

}
