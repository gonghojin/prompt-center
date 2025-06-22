package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.exception.StatisticsOperationFailedException;
import com.gongdel.promptserver.application.port.in.UserStatisticsQueryUseCase;
import com.gongdel.promptserver.application.port.out.query.UserStatisticsQueryPort;
import com.gongdel.promptserver.domain.statistics.ComparisonPeriod;
import com.gongdel.promptserver.domain.statistics.ComparisonResult;
import com.gongdel.promptserver.domain.statistics.UserStatistics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 유저 통계 조회 서비스입니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserStatisticsQueryService implements UserStatisticsQueryUseCase {
    private final UserStatisticsQueryPort userStatisticsQueryPort;

    /**
     * 대시보드용 유저 통계 정보를 조회합니다.
     *
     * @param period 비교 기간 (null 불가)
     * @return 유저 통계 도메인 객체
     * @throws IllegalArgumentException           period가 null인 경우
     * @throws StatisticsOperationFailedException 통계 조회 중 오류가 발생한 경우
     */
    @Override
    public UserStatistics getUserStatistics(ComparisonPeriod period) {
        Assert.notNull(period, "period must not be null");
        try {
            log.debug("Start loading user statistics for period: {}", period);

            long totalCount = userStatisticsQueryPort.loadTotalUserCount();
            long currentCount = userStatisticsQueryPort.loadUserCountByPeriod(period);
            ComparisonPeriod previousPeriod = calculatePreviousPeriod(period);
            long previousCount = userStatisticsQueryPort.loadUserCountByPeriod(previousPeriod);

            log.debug("User count summary - total: {}, current period: {}, previous period: {}",
                totalCount, currentCount, previousCount);

            ComparisonResult comparisonResult = ComparisonResult.of(currentCount, previousCount);
            UserStatistics stats = new UserStatistics(totalCount, period, comparisonResult);
            log.debug("User statistics result: {}", stats);
            return stats;
        } catch (Exception e) {
            log.error("Unexpected error during user statistics query", e);
            throw new StatisticsOperationFailedException("Failed to query user statistics", e);
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
