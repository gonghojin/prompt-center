package com.gongdel.promptserver.application.port.in;

import com.gongdel.promptserver.domain.statistics.ComparisonPeriod;
import com.gongdel.promptserver.domain.statistics.UserStatistics;

/**
 * 유저 통계 조회 유스케이스 인터페이스입니다.
 */
public interface UserStatisticsQueryUseCase {
    /**
     * 대시보드용 유저 통계 정보를 조회합니다.
     *
     * @param period 비교 기간 (null 불가)
     * @return 유저 통계 도메인 객체
     */
    UserStatistics getUserStatistics(ComparisonPeriod period);
}
