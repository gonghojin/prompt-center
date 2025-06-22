package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.statistics.ComparisonPeriod;

/**
 * 즐겨찾기 개수 조회 포트
 */
public interface LoadFavoritesPort {
    /**
     * 특정 사용자의 즐겨찾기 개수를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 즐겨찾기 개수
     */
    long countByUser(Long userId);

    /**
     * 전체 즐겨찾기 개수를 조회합니다.
     *
     * @return 전체 즐겨찾기 개수
     */
    long countTotal();

    /**
     * 특정 기간의 즐겨찾기 개수를 조회합니다.
     *
     * @param period 조회할 기간 (null 불가)
     * @return 해당 기간의 즐겨찾기 개수 (0 이상)
     * @throws IllegalArgumentException period가 null인 경우
     */
    long loadFavoriteCountByPeriod(ComparisonPeriod period);
}
