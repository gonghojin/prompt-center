package com.gongdel.promptserver.adapter.out.persistence.repository;

import com.gongdel.promptserver.adapter.out.persistence.entity.QCategoryEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.QPromptTemplateEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.QUserEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.view.QPromptViewCountEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.view.QPromptViewLogEntity;
import com.gongdel.promptserver.domain.model.statistics.DailyViewStatistics;
import com.gongdel.promptserver.domain.model.statistics.TopViewedPrompt;
import com.gongdel.promptserver.domain.model.statistics.ViewCountDistribution;
import com.gongdel.promptserver.domain.model.statistics.ViewCountRange;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * QueryDSL 기반 조회수 통계/집계 Repository 구현체입니다.
 * <p>
 * 기존 JPA Repository의 복잡한 @Query 메서드들을 QueryDSL로 개선하여
 * 타입 안전성, 동적 쿼리 지원, 복잡한 조건 조합을 제공합니다.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ViewStatisticsQueryRepositoryImpl implements ViewStatisticsQueryRepository {

    // QueryDSL Q클래스들
    private static final QPromptViewLogEntity viewLog = QPromptViewLogEntity.promptViewLogEntity;
    private static final QPromptViewCountEntity viewCount = QPromptViewCountEntity.promptViewCountEntity;
    private static final QPromptTemplateEntity promptTemplate = QPromptTemplateEntity.promptTemplateEntity;
    private static final QCategoryEntity category = QCategoryEntity.categoryEntity;
    private static final QUserEntity user = QUserEntity.userEntity;
    private final JPAQueryFactory queryFactory;

    /**
     * 1. 인기 프롬프트 목록을 조회합니다. (특정 기간 내, 동적 필터링 지원)
     * <p>
     * 기존 PromptViewLogJpaRepository.findTopViewedPrompts() 메서드를 개선
     * - 동적 카테고리 필터링 추가
     * - 작성자 정보 포함
     * - 타입 안전성 보장
     */
    @Override
    public List<TopViewedPrompt> findTopViewedPromptsWithFilters(
        LocalDateTime startDate,
        LocalDateTime endDate,
        List<Long> categoryIds,
        int limit) {

        Assert.notNull(startDate, "startDate must not be null");
        Assert.notNull(endDate, "endDate must not be null");
        Assert.notNull(categoryIds, "categoryIds must not be null");
        Assert.isTrue(limit > 0, "limit must be positive");

        log.debug("Finding top viewed prompts - period: {} to {}, categories: {}, limit: {}",
            startDate, endDate, categoryIds, limit);

        BooleanBuilder whereCondition = new BooleanBuilder()
            .and(viewLog.viewedAt.between(startDate, endDate));

        // 카테고리 필터링 (빈 리스트가 아닌 경우에만)
        if (!categoryIds.isEmpty()) {
            whereCondition.and(promptTemplate.category.id.in(categoryIds));
        }

        List<TopViewedPrompt> result = queryFactory
            .select(Projections.constructor(TopViewedPrompt.class,
                viewLog.promptTemplateId,
                viewLog.count(),
                promptTemplate.title,
                category.name,
                user.name,
                viewLog.viewedAt.max()))
            .from(viewLog)
            .join(promptTemplate).on(viewLog.promptTemplateId.eq(promptTemplate.id))
            .join(category).on(promptTemplate.category.id.eq(category.id))
            .join(user).on(promptTemplate.createdBy.id.eq(user.id))
            .where(whereCondition)
            .groupBy(viewLog.promptTemplateId, promptTemplate.title, category.name, user.name)
            .orderBy(viewLog.count().desc(), viewLog.viewedAt.max().desc())
            .limit(limit)
            .fetch();

        log.debug("Found {} top viewed prompts", result.size());
        return result;
    }

    /**
     * 2. 일별 조회수 통계를 조회합니다. (동적 시간 단위 지원)
     * <p>
     * 기존 PromptViewLogJpaRepository.findDailyViewStatistics() 메서드를 개선
     * - QueryDSL의 DATE 함수 사용으로 타입 안전성 보장
     * - 향후 시간별, 주별 확장 가능
     */
    @Override
    public List<DailyViewStatistics> findDailyViewStatistics(
        Long promptTemplateId,
        LocalDateTime startDate,
        LocalDateTime endDate) {

        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");
        Assert.notNull(startDate, "startDate must not be null");
        Assert.notNull(endDate, "endDate must not be null");

        log.debug("Finding daily view statistics - promptId: {}, period: {} to {}",
            promptTemplateId, startDate, endDate);

        // DATE 함수를 사용한 날짜 그룹화
        DateTemplate<LocalDate> dateExpression = Expressions.dateTemplate(
            LocalDate.class, "DATE({0})", viewLog.viewedAt);

        List<DailyViewStatistics> result = queryFactory
            .select(Projections.constructor(DailyViewStatistics.class,
                dateExpression,
                viewLog.count()))
            .from(viewLog)
            .where(
                viewLog.promptTemplateId.eq(promptTemplateId)
                    .and(viewLog.viewedAt.between(startDate, endDate)))
            .groupBy(dateExpression)
            .orderBy(dateExpression.asc())
            .fetch();

        log.debug("Found {} daily statistics records", result.size());
        return result;
    }

    /**
     * 3. 조회수 분포 통계를 조회합니다. (동적 구간 설정 지원)
     * <p>
     * 기존 PromptViewCountJpaRepository.getViewCountDistribution() 메서드를 개선
     * - 동적 구간 설정 지원
     * - 카테고리 필터링 추가
     * - 복잡한 CASE 문을 메서드 체이닝으로 표현
     */
    @Override
    public List<ViewCountDistribution> getViewCountDistributionWithFilters(
        ViewCountRange[] ranges,
        List<Long> categoryIds) {

        Assert.notNull(categoryIds, "categoryIds must not be null");

        // 기본 구간 사용 (null인 경우)
        ViewCountRange[] actualRanges = ranges != null ? ranges : ViewCountRange.getDefaultRanges();

        log.debug("Getting view count distribution - ranges: {}, categories: {}",
            actualRanges.length, categoryIds);

        BooleanBuilder whereCondition = new BooleanBuilder();

        // 카테고리 필터링 (빈 리스트가 아닌 경우에만)
        if (!categoryIds.isEmpty()) {
            whereCondition.and(promptTemplate.category.id.in(categoryIds));
        }

        // 동적 CASE 문 생성
        StringExpression rangeExpression = createRangeExpression(viewCount.totalViewCount, actualRanges);

        List<ViewCountDistribution> result = queryFactory
            .select(Projections.constructor(ViewCountDistribution.class,
                rangeExpression,
                viewCount.count()))
            .from(viewCount)
            .join(promptTemplate).on(viewCount.promptTemplateId.eq(promptTemplate.id))
            .where(whereCondition)
            .groupBy(rangeExpression)
            .orderBy(rangeExpression.asc())
            .fetch();

        log.debug("Found {} distribution ranges", result.size());
        return result;
    }

    /**
     * 4. 특정 기간 내 조회수 합계를 조회합니다. (동적 필터링 지원)
     * <p>
     * 기존 PromptViewCountJpaRepository.getTotalViewCountByPeriod() 메서드를 개선
     * - 카테고리 필터링 추가
     * - 다양한 조건 조합 지원
     */
    @Override
    public long getTotalViewCountByPeriodWithFilters(
        LocalDateTime startDate,
        LocalDateTime endDate,
        List<Long> categoryIds) {

        Assert.notNull(startDate, "startDate must not be null");
        Assert.notNull(endDate, "endDate must not be null");
        Assert.notNull(categoryIds, "categoryIds must not be null");

        log.debug("Getting total view count by period - period: {} to {}, categories: {}",
            startDate, endDate, categoryIds);

        BooleanBuilder whereCondition = new BooleanBuilder()
            .and(viewCount.updatedAt.between(startDate, endDate));

        // 카테고리 필터링 (빈 리스트가 아닌 경우에만)
        if (!categoryIds.isEmpty()) {
            whereCondition.and(promptTemplate.category.id.in(categoryIds));
        }

        Long result = queryFactory
            .select(viewCount.totalViewCount.sum().coalesce(0L))
            .from(viewCount)
            .join(promptTemplate).on(viewCount.promptTemplateId.eq(promptTemplate.id))
            .where(whereCondition)
            .fetchOne();

        long totalCount = result != null ? result : 0L;
        log.debug("Total view count: {}", totalCount);
        return totalCount;
    }

    /**
     * 5. 프롬프트별 조회수를 집계합니다. (동적 필터링 지원)
     * <p>
     * 기존 PromptViewLogJpaRepository.countByPromptTemplateId() 관련 메서드들을 개선
     * - 여러 프롬프트 ID 일괄 처리
     * - 기간별 필터링 지원
     */
    @Override
    public List<TopViewedPrompt> getViewCountsByPromptIds(
        List<Long> promptTemplateIds,
        LocalDateTime startDate,
        LocalDateTime endDate) {

        Assert.notNull(promptTemplateIds, "promptTemplateIds must not be null");
        Assert.notEmpty(promptTemplateIds, "promptTemplateIds must not be empty");

        log.debug("Getting view counts by prompt IDs - ids: {}, period: {} to {}",
            promptTemplateIds, startDate, endDate);

        BooleanBuilder whereCondition = new BooleanBuilder()
            .and(viewLog.promptTemplateId.in(promptTemplateIds));

        // 기간 필터링 (null이 아닌 경우에만)
        if (startDate != null && endDate != null) {
            whereCondition.and(viewLog.viewedAt.between(startDate, endDate));
        }

        List<TopViewedPrompt> result = queryFactory
            .select(Projections.constructor(TopViewedPrompt.class,
                viewLog.promptTemplateId,
                viewLog.count(),
                promptTemplate.title,
                category.name))
            .from(viewLog)
            .join(promptTemplate).on(viewLog.promptTemplateId.eq(promptTemplate.id))
            .join(category).on(promptTemplate.category.id.eq(category.id))
            .where(whereCondition)
            .groupBy(viewLog.promptTemplateId, promptTemplate.title, category.name)
            .orderBy(viewLog.count().desc())
            .fetch();

        log.debug("Found view counts for {} prompts", result.size());
        return result;
    }

    /**
     * 동적 조회수 구간 CASE 문을 생성합니다.
     *
     * @param countExpression 조회수 필드 표현식
     * @param ranges          구간 설정 배열
     * @return CASE 문 표현식
     */
    private StringExpression createRangeExpression(
        com.querydsl.core.types.dsl.NumberPath<Long> countExpression,
        ViewCountRange[] ranges) {

        // 간단한 CASE 문 구성 - 첫 번째 조건부터 시작
        Expression<String> result = Expressions.constant("Unknown");

        // 역순으로 처리하여 체이닝
        for (int i = ranges.length - 1; i >= 0; i--) {
            ViewCountRange range = ranges[i];
            BooleanBuilder rangeCondition = new BooleanBuilder();

            // 최솟값 조건
            rangeCondition.and(countExpression.goe(range.getMinCount()));

            // 최댓값 조건 (무제한이 아닌 경우)
            if (!range.isUnbounded()) {
                rangeCondition.and(countExpression.loe(range.getMaxCount()));
            }

            result = Expressions.cases()
                .when(rangeCondition)
                .then(range.getLabel())
                .otherwise(result);
        }

        // StringExpression으로 캐스팅
        return (StringExpression) result;
    }
}
