package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.adapter.out.persistence.repository.PromptViewCountJpaRepository;
import com.gongdel.promptserver.adapter.out.persistence.repository.ViewStatisticsQueryRepository;
import com.gongdel.promptserver.application.port.in.ViewStatisticsQueryUseCase;
import com.gongdel.promptserver.application.port.in.query.view.GetViewStatisticsQuery;
import com.gongdel.promptserver.domain.model.statistics.DailyViewStatistics;
import com.gongdel.promptserver.domain.model.statistics.TopViewedPrompt;
import com.gongdel.promptserver.domain.model.statistics.ViewCountDistribution;
import com.gongdel.promptserver.domain.statistics.ComparisonPeriod;
import com.gongdel.promptserver.domain.statistics.ComparisonResult;
import com.gongdel.promptserver.domain.view.ViewOperationException;
import com.gongdel.promptserver.domain.view.ViewStatistics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 조회수 통계 조회 전용 서비스 구현체입니다.
 * QueryDSL 기반 고급 통계 기능을 제공합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ViewStatisticsQueryService implements ViewStatisticsQueryUseCase {

    private final ViewStatisticsQueryRepository viewStatisticsQueryRepository;
    private final PromptViewCountJpaRepository promptViewCountJpaRepository;

    /**
     * 인기 프롬프트 목록을 조회합니다.
     * 특정 기간 내, 동적 필터링을 지원합니다.
     *
     * @param query 통계 조회 쿼리 객체
     * @return 인기 프롬프트 목록
     */
    @Override
    public List<TopViewedPrompt> getTopViewedPrompts(GetViewStatisticsQuery query) {
        Assert.notNull(query, "GetViewStatisticsQuery must not be null");

        log.debug("Loading top viewed prompts: startDate={}, endDate={}, categoryIds={}, limit={}",
            query.getStartDate(), query.getEndDate(), query.getCategoryIds(), query.getLimit());

        try {
            List<TopViewedPrompt> result = viewStatisticsQueryRepository
                .findTopViewedPromptsWithFilters(
                    query.getStartDate(),
                    query.getEndDate(),
                    query.getCategoryIds(),
                    query.getLimit());

            log.info("Top viewed prompts loaded successfully: count={}", result.size());
            return result;

        } catch (Exception e) {
            log.error("Failed to load top viewed prompts: startDate={}, endDate={}",
                query.getStartDate(), query.getEndDate(), e);
            throw ViewOperationException.topViewedLoadFailed(e);
        }
    }

    /**
     * 일별 조회수 통계를 조회합니다.
     * 특정 프롬프트의 시간별 조회 패턴을 분석할 때 사용합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @param query            통계 조회 쿼리 객체
     * @return 일별 조회수 통계
     */
    @Override
    public List<DailyViewStatistics> getDailyViewStatistics(Long promptTemplateId, GetViewStatisticsQuery query) {
        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");
        Assert.notNull(query, "GetViewStatisticsQuery must not be null");

        log.debug("Loading daily view statistics: promptId={}, startDate={}, endDate={}",
            promptTemplateId, query.getStartDate(), query.getEndDate());

        try {
            List<DailyViewStatistics> result = viewStatisticsQueryRepository
                .findDailyViewStatistics(
                    promptTemplateId,
                    query.getStartDate(),
                    query.getEndDate());

            log.info("Daily view statistics loaded successfully: promptId={}, count={}",
                promptTemplateId, result.size());
            return result;

        } catch (Exception e) {
            log.error("Failed to load daily view statistics: promptId={}, startDate={}, endDate={}",
                promptTemplateId, query.getStartDate(), query.getEndDate(), e);
            throw ViewOperationException.statisticsLoadFailed(e);
        }
    }

    /**
     * 조회수 분포 통계를 조회합니다.
     * 전체 프롬프트들의 조회수 분포를 파악할 때 사용합니다.
     *
     * @param query 통계 조회 쿼리 객체
     * @return 조회수 분포 통계
     */
    @Override
    public List<ViewCountDistribution> getViewCountDistribution(GetViewStatisticsQuery query) {
        Assert.notNull(query, "GetViewStatisticsQuery must not be null");

        log.debug("Loading view count distribution: categoryIds={}, ranges={}",
            query.getCategoryIds(), query.getViewCountRanges());

        try {
            List<ViewCountDistribution> result = viewStatisticsQueryRepository
                .getViewCountDistributionWithFilters(
                    query.getViewCountRanges(),
                    query.getCategoryIds());

            log.info("View count distribution loaded successfully: count={}", result.size());
            return result;

        } catch (Exception e) {
            log.error("Failed to load view count distribution: categoryIds={}",
                query.getCategoryIds(), e);
            throw ViewOperationException.statisticsLoadFailed(e);
        }
    }

    /**
     * 특정 기간 내 총 조회수를 조회합니다.
     * 대시보드나 요약 통계에서 사용합니다.
     *
     * @param query 통계 조회 쿼리 객체
     * @return 기간 내 총 조회수
     */
    @Override
    public long getTotalViewCountByPeriod(GetViewStatisticsQuery query) {
        Assert.notNull(query, "GetViewStatisticsQuery must not be null");

        log.debug("Loading total view count by period: startDate={}, endDate={}, categoryIds={}",
            query.getStartDate(), query.getEndDate(), query.getCategoryIds());

        try {
            long result = viewStatisticsQueryRepository
                .getTotalViewCountByPeriodWithFilters(
                    query.getStartDate(),
                    query.getEndDate(),
                    query.getCategoryIds());

            log.info("Total view count by period loaded successfully: count={}", result);
            return result;

        } catch (Exception e) {
            log.error("Failed to load total view count by period: startDate={}, endDate={}",
                query.getStartDate(), query.getEndDate(), e);
            throw ViewOperationException.statisticsLoadFailed(e);
        }
    }

    /**
     * 특정 프롬프트들의 조회수를 일괄 조회합니다.
     * 여러 프롬프트의 조회수를 한 번에 가져올 때 사용합니다.
     *
     * @param query 통계 조회 쿼리 객체
     * @return 프롬프트별 조회수 목록
     */
    @Override
    public List<TopViewedPrompt> getViewCountsByPromptIds(GetViewStatisticsQuery query) {
        Assert.notNull(query, "GetViewStatisticsQuery must not be null");
        Assert.notEmpty(query.getPromptTemplateIds(), "promptTemplateIds must not be empty");

        log.debug("Loading view counts by prompt IDs: promptIds={}, startDate={}, endDate={}",
            query.getPromptTemplateIds(), query.getStartDate(), query.getEndDate());

        try {
            List<TopViewedPrompt> result = viewStatisticsQueryRepository
                .getViewCountsByPromptIds(
                    query.getPromptTemplateIds(),
                    query.getStartDate(),
                    query.getEndDate());

            log.info("View counts by prompt IDs loaded successfully: count={}", result.size());
            return result;

        } catch (Exception e) {
            log.error("Failed to load view counts by prompt IDs: promptIds={}",
                query.getPromptTemplateIds(), e);
            throw ViewOperationException.statisticsLoadFailed(e);
        }
    }

    /**
     * 지정된 기간의 조회수 통계 정보를 조회합니다.
     * 현재 기간과 이전 기간을 비교하여 증감률을 계산합니다.
     *
     * @param period 비교 기간 (null 불가)
     * @return 조회수 통계 도메인 객체
     * @throws IllegalArgumentException period가 null인 경우
     * @throws ViewOperationException   통계 조회 중 오류가 발생한 경우
     */
    @Override
    public ViewStatistics getViewStatistics(ComparisonPeriod period) {
        Assert.notNull(period, "period must not be null");

        try {
            log.debug("Start loading view statistics for period: {}", period);

            // 전체 누적 조회수 조회
            long totalViewCount = promptViewCountJpaRepository.getTotalViewCount();

            // 현재 기간 조회수 조회
            long currentPeriodViewCount = promptViewCountJpaRepository.getTotalViewCountByPeriod(
                period.getStartDate(), period.getEndDate());

            // 이전 기간 계산 및 조회수 조회
            ComparisonPeriod previousPeriod = calculatePreviousPeriod(period);
            long previousPeriodViewCount = promptViewCountJpaRepository.getTotalViewCountByPeriod(
                previousPeriod.getStartDate(), previousPeriod.getEndDate());

            log.debug("View count summary - total: {}, current period: {}, previous period: {}",
                totalViewCount, currentPeriodViewCount, previousPeriodViewCount);

            // 비교 결과 생성
            ComparisonResult comparisonResult = ComparisonResult.of(currentPeriodViewCount, previousPeriodViewCount);
            ViewStatistics statistics = new ViewStatistics(totalViewCount, period, comparisonResult);

            log.info("View statistics loaded successfully: total={}, current={}, changeRate={}%",
                totalViewCount, currentPeriodViewCount, comparisonResult.getPercentageChange());

            return statistics;

        } catch (Exception e) {
            log.error("Failed to load view statistics for period: {}", period, e);
            throw ViewOperationException.statisticsLoadFailed(e);
        }
    }

    /**
     * 이전 비교 기간을 계산합니다.
     *
     * @param period 기준이 되는 비교 기간
     * @return 이전 비교 기간
     */
    private ComparisonPeriod calculatePreviousPeriod(ComparisonPeriod period) {
        long duration = java.time.Duration.between(period.getStartDate(), period.getEndDate()).getSeconds();
        return new ComparisonPeriod(
            period.getStartDate().minusSeconds(duration),
            period.getStartDate().minusSeconds(1));
    }
}
