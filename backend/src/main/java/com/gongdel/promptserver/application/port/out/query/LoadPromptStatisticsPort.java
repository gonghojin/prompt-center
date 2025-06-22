package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.model.statistics.PromptStatisticsResult;
import com.gongdel.promptserver.domain.statistics.ComparisonPeriod;

/**
 * 프롬프트 통계 조회를 위한 포트 인터페이스입니다.
 */
public interface LoadPromptStatisticsPort {
    /**
     * 전체 프롬프트 개수를 조회합니다.
     *
     * @return 전체 프롬프트 개수 (0 이상)
     */
    long loadTotalPromptCount();

    /**
     * 특정 기간의 프롬프트 개수를 조회합니다.
     *
     * @param period 조회할 기간 (null 불가)
     * @return 해당 기간의 프롬프트 개수 (0 이상)
     * @throws IllegalArgumentException period가 null인 경우
     */
    long loadPromptCountByPeriod(ComparisonPeriod period);

    /**
     * 사용자별 프롬프트 상태별 통계를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 상태별 프롬프트 통계 결과
     */
    PromptStatisticsResult loadPromptStatisticsByUserId(Long userId);

    /**
     * 내가 생성한 프롬프트의 총 좋아요 수를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 총 좋아요 수
     */
    long loadTotalLikeCountByUserId(Long userId);

    /**
     * 내가 생성한 프롬프트의 총 조회수를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 총 조회수
     */
    long loadTotalViewCountByUserId(Long userId);
}
