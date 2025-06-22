package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.entity.UserEntity;
import com.gongdel.promptserver.adapter.out.persistence.repository.UserJpaRepository;
import com.gongdel.promptserver.application.port.out.query.UserStatisticsQueryPort;
import com.gongdel.promptserver.domain.statistics.ComparisonPeriod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 유저 통계 조회 어댑터입니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserStatisticsQueryAdapter implements UserStatisticsQueryPort {
    private final UserJpaRepository userJpaRepository;

    /**
     * 전체 활성 유저 개수를 조회합니다.
     *
     * @return 전체 활성 유저 개수 (0 이상)
     */
    @Override
    public long loadTotalUserCount() {
        log.debug("Loading total active user count");
        long count = userJpaRepository.countByStatus(UserEntity.UserStatus.ACTIVE);
        log.debug("Total active user count: {}", count);
        return count;
    }

    /**
     * 특정 기간의 활성 유저 개수를 조회합니다.
     *
     * @param period 조회할 기간 (null 불가)
     * @return 해당 기간의 활성 유저 개수 (0 이상)
     * @throws IllegalArgumentException period가 null인 경우
     */
    @Override
    public long loadUserCountByPeriod(ComparisonPeriod period) {
        Assert.notNull(period, "period must not be null");
        log.debug("Loading user count for period: {} to {}", period.getStartDate(), period.getEndDate());

        long count = userJpaRepository.countByStatusAndCreatedAtBetween(
            UserEntity.UserStatus.ACTIVE,
            period.getStartDate(),
            period.getEndDate());

        log.debug("User count for period: {}", count);
        return count;
    }
}
