package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.repository.PromptLikeCountRepository;
import com.gongdel.promptserver.adapter.out.persistence.repository.PromptTemplateJpaRepository;
import com.gongdel.promptserver.adapter.out.persistence.repository.PromptTemplateQueryRepository;
import com.gongdel.promptserver.adapter.out.persistence.repository.PromptViewCountJpaRepository;
import com.gongdel.promptserver.application.port.out.query.LoadPromptStatisticsPort;
import com.gongdel.promptserver.domain.exception.PromptStatisticsException;
import com.gongdel.promptserver.domain.model.statistics.PromptStatisticsResult;
import com.gongdel.promptserver.domain.statistics.ComparisonPeriod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 프롬프트 통계 조회를 위한 어댑터 구현체입니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PromptStatisticsQueryAdapter implements LoadPromptStatisticsPort {
    private final PromptTemplateJpaRepository promptTemplateJpaRepository;
    private final PromptTemplateQueryRepository promptTemplateQueryRepository;
    private final PromptLikeCountRepository promptLikeCountRepository;
    private final PromptViewCountJpaRepository promptViewCountJpaRepository;

    /**
     * 전체 프롬프트 개수를 조회합니다.
     *
     * @return 전체 프롬프트 개수
     */
    @Override
    public long loadTotalPromptCount() {
        try {
            log.debug("Loading total prompt count");
            long count = promptTemplateJpaRepository.count();
            log.debug("Total prompt count loaded: {}", count);
            return count;
        } catch (Exception e) {
            log.error("Failed to load total prompt count. Error: {}", e.getMessage(), e);
            throw PromptStatisticsException.databaseError(e);
        }
    }

    /**
     * 주어진 기간 내 프롬프트 개수를 조회합니다.
     *
     * @param period 조회할 기간 정보
     * @return 해당 기간 내 프롬프트 개수
     * @throws PromptStatisticsException 유효성 검사 또는 DB 오류 발생 시
     */
    @Override
    public long loadPromptCountByPeriod(ComparisonPeriod period) {
        try {
            Assert.notNull(period, "ComparisonPeriod must not be null");
            log.debug("Loading prompt count for period: {} ~ {}", period.getStartDate(), period.getEndDate());
            long count = promptTemplateJpaRepository.countByCreatedAtBetween(period.getStartDate(),
                period.getEndDate());
            log.debug("Prompt count loaded for period {} ~ {}: {}", period.getStartDate(), period.getEndDate(), count);
            return count;
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument for period: {}", period, e);
            throw PromptStatisticsException.invalidPeriod();
        } catch (Exception e) {
            log.error("Failed to load prompt count by period: {}. Error: {}", period, e.getMessage(), e);
            throw PromptStatisticsException.databaseError(e);
        }
    }

    /**
     * 사용자별 프롬프트 상태별 통계를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 상태별 프롬프트 통계 결과
     * @throws PromptStatisticsException 유효성 검사 또는 DB 오류 발생 시
     */
    @Override
    public PromptStatisticsResult loadPromptStatisticsByUserId(Long userId) {
        try {
            Assert.notNull(userId, "userId must not be null");
            return promptTemplateQueryRepository.loadPromptStatisticsByUserId(userId);
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument for userId: {}", userId, e);
            throw PromptStatisticsException.invalidPeriod();
        } catch (Exception e) {
            log.error("Failed to load prompt statistics for userId {}. Error: {}", userId, e.getMessage(), e);
            throw PromptStatisticsException.databaseError(e);
        }
    }

    @Override
    public long loadTotalLikeCountByUserId(Long userId) {
        try {
            Assert.notNull(userId, "userId must not be null");
            Long sum = promptLikeCountRepository.sumLikeCountByUserId(userId);
            return sum != null ? sum : 0L;
        } catch (Exception e) {
            log.error("Failed to load total like count for userId {}. Error: {}", userId, e.getMessage(), e);
            throw PromptStatisticsException.databaseError(e);
        }
    }

    @Override
    public long loadTotalViewCountByUserId(Long userId) {
        try {
            Assert.notNull(userId, "userId must not be null");
            Long sum = promptViewCountJpaRepository.sumViewCountByUserId(userId);
            return sum != null ? sum : 0L;
        } catch (Exception e) {
            log.error("Failed to load total view count for userId {}. Error: {}", userId, e.getMessage(), e);
            throw PromptStatisticsException.databaseError(e);
        }
    }
}
