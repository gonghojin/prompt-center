package com.gongdel.promptserver.adapter.out.persistence;

import com.gongdel.promptserver.adapter.out.persistence.repository.CategoryStatisticsQueryRepository;
import com.gongdel.promptserver.application.port.out.CategoryStatisticsQueryPort;
import com.gongdel.promptserver.domain.exception.PromptStatisticsErrorType;
import com.gongdel.promptserver.domain.exception.PromptStatisticsException;
import com.gongdel.promptserver.domain.model.statistics.CategoryPromptCount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 카테고리별 프롬프트 개수 집계용 JPA 어댑터 구현체입니다.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CategoryStatisticsQueryPersistenceAdapter implements CategoryStatisticsQueryPort {

    private final CategoryStatisticsQueryRepository categoryStatisticsQueryRepository;

    /**
     * 루트 카테고리별 프롬프트 개수 목록을 조회합니다.
     *
     * @return 루트 카테고리별 프롬프트 개수 목록
     * @throws PromptStatisticsException 집계 중 예외 발생 시
     */
    @Override
    public List<CategoryPromptCount> getRootCategoryPromptCounts() {
        try {
            log.info("Fetching root category prompt counts");
            return categoryStatisticsQueryRepository.findRootCategoryPromptCounts();
        } catch (DataAccessException e) {
            log.error("Database access error while fetching root category prompt counts", e);
            throw new PromptStatisticsException(
                PromptStatisticsErrorType.DATABASE_ERROR,
                "Database error occurred while fetching root category prompt counts",
                e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument for root category prompt counts: {}", e.getMessage(), e);
            throw new PromptStatisticsException(
                PromptStatisticsErrorType.GENERAL,
                "Invalid argument for root category prompt counts: " + e.getMessage(),
                e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching root category prompt counts", e);
            throw new PromptStatisticsException(
                PromptStatisticsErrorType.GENERAL,
                "Unexpected error occurred while fetching root category prompt counts",
                e);
        }
    }

    /**
     * 특정 루트의 하위 카테고리별 프롬프트 개수 목록을 조회합니다.
     *
     * @param rootId 루트 카테고리 ID (null 불가)
     * @return 하위 카테고리별 프롬프트 개수 목록
     * @throws PromptStatisticsException 집계 중 예외 발생 시
     */
    @Override
    public List<CategoryPromptCount> getChildCategoryPromptCounts(Long rootId) {
        try {
            Assert.notNull(rootId, "rootId must not be null");
            log.info("Fetching child category prompt counts for rootId: {}", rootId);
            return categoryStatisticsQueryRepository.findChildCategoryPromptCounts(rootId);
        } catch (DataAccessException e) {
            log.error("Database access error while fetching child category prompt counts for rootId: {}", rootId, e);
            throw new PromptStatisticsException(
                PromptStatisticsErrorType.DATABASE_ERROR,
                "Database error occurred while fetching child category prompt counts",
                e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument for child category prompt counts: {}", e.getMessage(), e);
            throw new PromptStatisticsException(
                PromptStatisticsErrorType.GENERAL,
                "Invalid argument for child category prompt counts: " + e.getMessage(),
                e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching child category prompt counts for rootId: {}", rootId, e);
            throw new PromptStatisticsException(
                PromptStatisticsErrorType.GENERAL,
                "Unexpected error occurred while fetching child category prompt counts",
                e);
        }
    }
}
