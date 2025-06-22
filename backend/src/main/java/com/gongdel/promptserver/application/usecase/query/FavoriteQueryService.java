package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.exception.StatisticsOperationFailedException;
import com.gongdel.promptserver.application.port.in.query.FavoriteQueryUseCase;
import com.gongdel.promptserver.application.port.out.query.LoadFavoritesPort;
import com.gongdel.promptserver.application.port.out.query.SearchFavoritePort;
import com.gongdel.promptserver.domain.exception.FavoriteException;
import com.gongdel.promptserver.domain.model.PromptStats;
import com.gongdel.promptserver.domain.model.favorite.FavoritePromptResult;
import com.gongdel.promptserver.domain.model.favorite.FavoriteSearchCondition;
import com.gongdel.promptserver.domain.statistics.ComparisonPeriod;
import com.gongdel.promptserver.domain.statistics.ComparisonResult;
import com.gongdel.promptserver.domain.statistics.FavoriteStatistics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 즐겨찾기 관련 Query 유스케이스 구현체입니다.
 * 조회수 정보를 통합하여 완전한 즐겨찾기 정보를 제공합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteQueryService implements FavoriteQueryUseCase {

    private final LoadFavoritesPort loadFavoritesPort;
    private final SearchFavoritePort searchFavoritePort;
    private final ViewQueryService viewQueryService;

    /**
     * 특정 사용자의 즐겨찾기 개수를 조회합니다.
     *
     * @param userId 사용자 ID (null 불가)
     * @return 즐겨찾기 개수
     * @throws IllegalArgumentException userId가 null인 경우
     * @throws FavoriteException        내부 오류 발생 시
     */
    @Override
    public long countByUser(Long userId) {
        try {
            validateUserId(userId);
            log.debug("Counting favorites for userId={}", userId);
            long count = loadFavoritesPort.countByUser(userId);
            log.info("Favorite count for userId={}: {}", userId, count);
            return count;
        } catch (IllegalArgumentException e) {
            log.warn("Invalid userId when counting favorites: {}", userId);
            throw e;
        } catch (Exception e) {
            log.error("Failed to count favorites for userId={}: {}", userId, e.getMessage(), e);
            throw FavoriteException.internalError("Failed to count favorites", e);
        }
    }

    /**
     * 전체 즐겨찾기 개수를 조회합니다.
     *
     * @return 전체 즐겨찾기 개수
     * @throws FavoriteException 내부 오류 발생 시
     */
    @Override
    public long getTotalFavoriteCount() {
        try {
            log.debug("Counting total favorites");
            long count = loadFavoritesPort.countTotal();
            log.info("Total favorite count: {}", count);
            return count;
        } catch (Exception e) {
            log.error("Failed to count total favorites: {}", e.getMessage(), e);
            throw FavoriteException.internalError("Failed to count total favorites", e);
        }
    }

    /**
     * 즐겨찾기 목록을 조회합니다.
     * 조회수 정보를 포함한 완전한 즐겨찾기 목록을 반환합니다.
     *
     * @param condition 즐겨찾기 검색 조건 (userId, pageable 모두 null 불가)
     * @return 조회수가 포함된 즐겨찾기 프롬프트 결과 페이지
     * @throws IllegalArgumentException condition, userId, pageable이 null인 경우
     * @throws FavoriteException        내부 오류 발생 시
     */
    @Override
    public Page<FavoritePromptResult> searchFavorites(FavoriteSearchCondition condition) {
        try {
            validateSearchCondition(condition);
            log.debug("Finding favorites with view counts for userId={}, condition={}",
                condition.getUserId(), condition);

            // 1. 기본 즐겨찾기 검색 수행
            Page<FavoritePromptResult> searchResults = searchFavoritePort.searchFavorites(condition);

            if (searchResults.isEmpty()) {
                log.debug("No favorite results found for userId={}", condition.getUserId());
                return searchResults;
            }

            // 2. 프롬프트 ID 목록 추출
            List<Long> promptIds = searchResults.getContent().stream()
                .map(FavoritePromptResult::getPromptId)
                .collect(Collectors.toList());

            // 3. 배치로 조회수 정보 조회
            Map<Long, Long> viewCountMap = viewQueryService.getViewCountsByPromptIds(promptIds);

            // 4. 검색 결과에 조회수 정보 통합
            Page<FavoritePromptResult> enrichedResults = searchResults
                .map(result -> createFavoritePromptResultWithViewCount(result, viewCountMap));

            log.debug("Successfully enriched {} favorite results with view counts for userId={}",
                enrichedResults.getContent().size(), condition.getUserId());
            return enrichedResults;

        } catch (IllegalArgumentException e) {
            log.warn("Invalid FavoriteSearchCondition: {}", condition);
            throw e;
        } catch (Exception e) {
            log.error("Failed to search favorites with view counts for userId={}: {}",
                condition != null ? condition.getUserId() : null, e.getMessage(), e);
            throw FavoriteException.internalError("Failed to search favorites", e);
        }
    }

    /**
     * 사용자 ID의 유효성을 검증합니다.
     *
     * @param userId 사용자 ID (null 불가)
     * @throws IllegalArgumentException userId가 null인 경우
     */
    private void validateUserId(Long userId) {
        Assert.notNull(userId, "userId must not be null");
    }

    /**
     * 즐겨찾기 검색 조건의 유효성을 검증합니다.
     *
     * @param condition 즐겨찾기 검색 조건 (userId, pageable 모두 null 불가)
     * @throws IllegalArgumentException condition, userId, pageable이 null인 경우
     */
    private void validateSearchCondition(FavoriteSearchCondition condition) {
        Assert.notNull(condition, "FavoriteSearchCondition must not be null");
    }

    /**
     * FavoritePromptResult에 조회수를 통합한 새로운 인스턴스를 생성합니다.
     *
     * @param original     원본 FavoritePromptResult
     * @param viewCountMap 조회수 맵
     * @return 조회수가 통합된 새로운 FavoritePromptResult
     */
    private FavoritePromptResult createFavoritePromptResultWithViewCount(
        FavoritePromptResult original, Map<Long, Long> viewCountMap) {

        long viewCount = viewCountMap.getOrDefault(original.getPromptId(), 0L);

        // 기존 PromptStats에 조회수를 업데이트한 새로운 PromptStats 생성
        PromptStats updatedStats = new PromptStats((int) viewCount, original.getStats().getFavoriteCount());

        return FavoritePromptResult.builder()
            .favoriteId(original.getFavoriteId())
            .promptId(original.getPromptId())
            .promptUuid(original.getPromptUuid())
            .title(original.getTitle())
            .description(original.getDescription())
            .tags(original.getTags())
            .createdById(original.getCreatedById())
            .createdByName(original.getCreatedByName())
            .categoryId(original.getCategoryId())
            .categoryName(original.getCategoryName())
            .visibility(original.getVisibility())
            .status(original.getStatus())
            .promptCreatedAt(original.getPromptCreatedAt())
            .promptUpdatedAt(original.getPromptUpdatedAt())
            .favoriteCreatedAt(original.getFavoriteCreatedAt())
            .stats(updatedStats)
            .isLiked(original.isLiked())
            .build();
    }

    /**
     * 대시보드용 즐겨찾기 통계 정보를 조회합니다.
     *
     * @param period 비교 기간 (null 불가)
     * @return 즐겨찾기 통계 도메인 객체
     * @throws IllegalArgumentException           period가 null인 경우
     * @throws StatisticsOperationFailedException 통계 조회 중 오류가 발생한 경우
     */
    @Override
    public FavoriteStatistics getFavoriteStatistics(ComparisonPeriod period) {
        Assert.notNull(period, "period must not be null");
        try {
            log.debug("Start loading favorite statistics for period: {}", period);

            long totalCount = loadFavoritesPort.countTotal();
            long currentCount = loadFavoritesPort.loadFavoriteCountByPeriod(period);
            ComparisonPeriod previousPeriod = calculatePreviousPeriod(period);
            long previousCount = loadFavoritesPort.loadFavoriteCountByPeriod(previousPeriod);

            log.debug("Favorite count summary - total: {}, current period: {}, previous period: {}",
                totalCount, currentCount, previousCount);

            ComparisonResult comparisonResult = ComparisonResult.of(currentCount, previousCount);
            FavoriteStatistics stats = new FavoriteStatistics(totalCount, period, comparisonResult);
            log.debug("Favorite statistics result: {}", stats);
            return stats;
        } catch (Exception e) {
            log.error("Unexpected error during favorite statistics query", e);
            throw new StatisticsOperationFailedException("Failed to query favorite statistics", e);
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
