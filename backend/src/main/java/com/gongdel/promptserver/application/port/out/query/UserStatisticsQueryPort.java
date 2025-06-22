package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.statistics.ComparisonPeriod;

/**
 * 유저 통계 조회를 위한 포트 인터페이스입니다.
 */
public interface UserStatisticsQueryPort {
    /**
     * 전체 유저 개수를 조회합니다.
     *
     * @return 전체 유저 개수 (0 이상)
     */
    long loadTotalUserCount();

    /**
     * 특정 기간의 유저 개수를 조회합니다.
     *
     * @param period 조회할 기간 (null 불가)
     * @return 해당 기간의 유저 개수 (0 이상)
     * @throws IllegalArgumentException period가 null인 경우
     */
    long loadUserCountByPeriod(ComparisonPeriod period);
}
