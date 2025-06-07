package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.port.in.CategoryStatisticsQueryUseCase;
import com.gongdel.promptserver.application.port.out.CategoryStatisticsQueryPort;
import com.gongdel.promptserver.domain.exception.PromptStatisticsErrorType;
import com.gongdel.promptserver.domain.exception.PromptStatisticsException;
import com.gongdel.promptserver.domain.model.statistics.CategoryPromptCount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 카테고리별 프롬프트 통계 조회 서비스 구현체입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryStatisticsQueryService implements CategoryStatisticsQueryUseCase {

    private final CategoryStatisticsQueryPort categoryStatisticsQueryPort;

    /**
     * 루트 카테고리별 프롬프트 개수 목록을 반환합니다.
     *
     * @return 루트 카테고리별 프롬프트 개수 도메인 모델 목록
     * @throws PromptStatisticsException 집계 중 예외 발생 시
     */
    @Override
    public List<CategoryPromptCount> getRootCategoryPromptCounts() {
        try {
            log.debug("Getting root category prompt counts");
            return categoryStatisticsQueryPort.getRootCategoryPromptCounts();
        } catch (Exception e) {
            log.error("Failed to get root category prompt counts: {}", e.getMessage(), e);
            throw new PromptStatisticsException(
                PromptStatisticsErrorType.GENERAL,
                "Failed to get root category prompt counts",
                e);
        }
    }

    /**
     * 특정 루트의 하위 카테고리별 프롬프트 개수 목록을 반환합니다.
     *
     * @param rootId 루트 카테고리 ID
     * @return 하위 카테고리별 프롬프트 개수 도메인 모델 목록
     * @throws PromptStatisticsException 집계 중 예외 발생 시
     */
    @Override
    public List<CategoryPromptCount> getChildCategoryPromptCounts(Long rootId) {
        try {
            Assert.notNull(rootId, "rootId must not be null");
            log.debug("Getting child category prompt counts for rootId: {}", rootId);
            return categoryStatisticsQueryPort.getChildCategoryPromptCounts(rootId);
        } catch (Exception e) {
            log.error("Failed to get child category prompt counts for rootId: {}, error={}", rootId, e.getMessage(), e);
            throw new PromptStatisticsException(
                PromptStatisticsErrorType.GENERAL,
                "Failed to get child category prompt counts",
                e);
        }
    }
}
