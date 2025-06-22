package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.exception.StatisticsOperationFailedException;
import com.gongdel.promptserver.application.port.in.MyPromptsQueryUseCase;
import com.gongdel.promptserver.application.port.out.query.LoadPromptStatisticsPort;
import com.gongdel.promptserver.application.port.out.query.SearchPromptsPort;
import com.gongdel.promptserver.domain.exception.PromptErrorType;
import com.gongdel.promptserver.domain.exception.PromptOperationException;
import com.gongdel.promptserver.domain.model.PromptSearchCondition;
import com.gongdel.promptserver.domain.model.PromptSearchResult;
import com.gongdel.promptserver.domain.model.PromptStats;
import com.gongdel.promptserver.domain.model.my.MyPromptSearchCondition;
import com.gongdel.promptserver.domain.model.statistics.PromptStatisticsResult;
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
 * 내 프롬프트 관리(목록, 통계 등) 유스케이스 서비스 구현체입니다.
 * 조회수 정보를 통합하여 완전한 프롬프트 정보를 제공합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPromptsQueryService implements MyPromptsQueryUseCase {
    private final SearchPromptsPort searchPromptsPort;
    private final LoadPromptStatisticsPort loadPromptStatisticsPort;
    private final ViewQueryService viewQueryService;

    /**
     * 내 프롬프트 목록을 조회합니다.
     * 조회수 정보를 포함한 완전한 프롬프트 목록을 반환합니다.
     *
     * @param condition 내 프롬프트 검색 조건
     * @return 조회수가 포함된 프롬프트 검색 결과 페이지
     * @throws IllegalArgumentException 검색 조건 또는 사용자 ID가 null인 경우
     * @throws PromptOperationException 프롬프트 목록 조회 중 오류가 발생한 경우
     */
    @Override
    public Page<PromptSearchResult> findMyPrompts(MyPromptSearchCondition condition) {
        Assert.notNull(condition, "MyPromptSearchCondition must not be null");
        Assert.notNull(condition.getUserId(), "userId must not be null");

        try {
            log.info("Searching my prompts with view counts. userId={}, condition={}",
                condition.getUserId(), condition);

            // 1. 기본 내 프롬프트 검색 수행
            PromptSearchCondition searchCondition = convertToPromptSearchCondition(condition);
            Page<PromptSearchResult> searchResults = searchPromptsPort.searchPrompts(searchCondition);

            if (searchResults.isEmpty()) {
                log.info("No my prompts found. userId={}", condition.getUserId());
                return searchResults;
            }

            // 2. 프롬프트 ID 목록 추출
            List<Long> promptIds = searchResults.getContent().stream()
                .map(PromptSearchResult::getId)
                .collect(Collectors.toList());

            // 3. 배치로 조회수 정보 조회
            Map<Long, Long> viewCountMap = viewQueryService.getViewCountsByPromptIds(promptIds);

            // 4. 검색 결과에 조회수 정보 통합
            Page<PromptSearchResult> enrichedResults = searchResults
                .map(result -> createPromptSearchResultWithViewCount(result, viewCountMap));

            log.info("My prompts search with view counts successful. userId={}, resultCount={}",
                condition.getUserId(), enrichedResults.getTotalElements());
            return enrichedResults;

        } catch (Exception e) {
            log.error("Failed to search my prompts with view counts. userId={}, error={}",
                condition.getUserId(), e.getMessage(), e);
            throw new PromptOperationException(PromptErrorType.OPERATION_FAILED,
                "Failed to search my prompts", e);
        }
    }

    /**
     * 내 프롬프트 통계를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 상태별 프롬프트 통계 결과
     * @throws IllegalArgumentException 사용자 ID가 null인 경우
     * @throws PromptOperationException 프롬프트 통계 조회 중 오류가 발생한 경우
     */
    @Override
    public PromptStatisticsResult getMyPromptStatistics(Long userId) {
        Assert.notNull(userId, "userId must not be null");
        try {
            log.info("Loading my prompt statistics. userId={}", userId);
            PromptStatisticsResult result = loadPromptStatisticsPort.loadPromptStatisticsByUserId(userId);
            log.info("Loaded my prompt statistics. userId={}, result={}", userId, result);
            return result;
        } catch (Exception e) {
            log.error("Failed to load my prompt statistics. userId={}, error={}", userId, e.getMessage(), e);
            throw new StatisticsOperationFailedException("Failed to load my prompt statistics", e);
        }
    }

    @Override
    public long getMyTotalLikeCount(Long userId) {
        Assert.notNull(userId, "userId must not be null");
        try {
            log.info("Loading my total prompt like count. userId={}", userId);
            long count = loadPromptStatisticsPort.loadTotalLikeCountByUserId(userId);
            log.info("Loaded my total prompt like count. userId={}, count={}", userId, count);
            return count;
        } catch (Exception e) {
            log.error("Failed to load my total prompt like count. userId={}, error={}", userId, e.getMessage(), e);
            throw new PromptOperationException(PromptErrorType.OPERATION_FAILED,
                "Failed to load my total prompt like count", e);
        }
    }

    @Override
    public long getMyTotalViewCount(Long userId) {
        Assert.notNull(userId, "userId must not be null");
        try {
            log.info("Loading my total prompt view count. userId={}", userId);
            long count = loadPromptStatisticsPort.loadTotalViewCountByUserId(userId);
            log.info("Loaded my total prompt view count. userId={}, count={}", userId, count);
            return count;
        } catch (Exception e) {
            log.error("Failed to load my total prompt view count. userId={}, error={}", userId, e.getMessage(), e);
            throw new PromptOperationException(PromptErrorType.OPERATION_FAILED,
                "Failed to load my total prompt view count", e);
        }
    }

    /**
     * MyPromptSearchCondition을 PromptSearchCondition으로 변환합니다.
     *
     * @param condition 내 프롬프트 검색 조건
     * @return 공통 프롬프트 검색 조건
     */
    private PromptSearchCondition convertToPromptSearchCondition(MyPromptSearchCondition condition) {
        return PromptSearchCondition.builder()
            .statusFilters(condition.getStatusFilters())
            .visibilityFilters(condition.getVisibilityFilters())
            .searchKeyword(condition.getSearchKeyword())
            .sortType(condition.getSortType())
            .pageable(condition.getPageable())
            .userId(condition.getUserId())
            .isMyPrompts(true)
            .build();
    }

    /**
     * PromptSearchResult에 조회수를 통합한 새로운 인스턴스를 생성합니다.
     *
     * @param original     원본 PromptSearchResult
     * @param viewCountMap 조회수 맵
     * @return 조회수가 통합된 새로운 PromptSearchResult
     */
    private PromptSearchResult createPromptSearchResultWithViewCount(
        PromptSearchResult original, Map<Long, Long> viewCountMap) {

        long viewCount = viewCountMap.getOrDefault(original.getId(), 0L);

        // 기존 PromptStats에 조회수를 업데이트한 새로운 PromptStats 생성
        PromptStats updatedStats = new PromptStats((int) viewCount, original.getStats().getFavoriteCount());

        return PromptSearchResult.builder()
            .id(original.getId())
            .uuid(original.getUuid())
            .title(original.getTitle())
            .description(original.getDescription())
            .currentVersionId(original.getCurrentVersionId())
            .categoryId(original.getCategoryId())
            .categoryName(original.getCategoryName())
            .createdById(original.getCreatedById())
            .createdByName(original.getCreatedByName())
            .tags(original.getTags())
            .visibility(original.getVisibility())
            .status(original.getStatus())
            .createdAt(original.getCreatedAt())
            .updatedAt(original.getUpdatedAt())
            .stats(updatedStats)
            .isFavorite(original.isFavorite())
            .isLiked(original.isLiked())
            .build();
    }
}
