package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.exception.StatisticsOperationFailedException;
import com.gongdel.promptserver.application.port.in.MyPromptsQueryUseCase;
import com.gongdel.promptserver.application.port.out.query.LoadPromptStatisticsPort;
import com.gongdel.promptserver.application.port.out.query.SearchPromptsPort;
import com.gongdel.promptserver.domain.exception.PromptErrorType;
import com.gongdel.promptserver.domain.exception.PromptOperationException;
import com.gongdel.promptserver.domain.model.PromptSearchCondition;
import com.gongdel.promptserver.domain.model.PromptSearchResult;
import com.gongdel.promptserver.domain.model.my.MyPromptSearchCondition;
import com.gongdel.promptserver.domain.model.statistics.PromptStatisticsResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 내 프롬프트 관리(목록, 통계 등) 유스케이스 서비스 구현체입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPromptsQueryService implements MyPromptsQueryUseCase {
    private final SearchPromptsPort searchPromptsPort;
    private final LoadPromptStatisticsPort loadPromptStatisticsPort;

    /**
     * 내 프롬프트 목록을 조회합니다.
     *
     * @param condition 내 프롬프트 검색 조건
     * @return 프롬프트 검색 결과 페이지
     * @throws IllegalArgumentException 검색 조건 또는 사용자 ID가 null인 경우
     * @throws PromptOperationException 프롬프트 목록 조회 중 오류가 발생한 경우
     */
    @Override
    public Page<PromptSearchResult> findMyPrompts(MyPromptSearchCondition condition) {
        Assert.notNull(condition, "MyPromptSearchCondition must not be null");
        Assert.notNull(condition.getUserId(), "userId must not be null");
        try {
            log.info("Searching my prompts. userId={}, condition={}", condition.getUserId(), condition);
            PromptSearchCondition searchCondition = convertToPromptSearchCondition(condition);
            Page<PromptSearchResult> result = searchPromptsPort.searchPrompts(searchCondition);
            log.info("My prompts search successful. userId={}, resultCount={}", condition.getUserId(),
                result.getTotalElements());
            return result;
        } catch (Exception e) {
            log.error("Failed to search my prompts. userId={}, error={}", condition.getUserId(), e.getMessage(), e);
            throw new PromptOperationException(PromptErrorType.OPERATION_FAILED, "Failed to search my prompts", e);
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
}
