package com.gongdel.promptserver.application.port.in.query;

import com.gongdel.promptserver.domain.model.favorite.FavoritePromptResult;
import com.gongdel.promptserver.domain.model.favorite.FavoriteSearchCondition;
import com.gongdel.promptserver.domain.statistics.ComparisonPeriod;
import com.gongdel.promptserver.domain.statistics.FavoriteStatistics;
import org.springframework.data.domain.Page;

/**
 * 즐겨찾기 관련 Query 유스케이스 인터페이스입니다.
 */
public interface FavoriteQueryUseCase {

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
    long getTotalFavoriteCount();

    /**
     * 대시보드용 즐겨찾기 통계 정보를 조회합니다.
     *
     * @param period 비교 기간 (null 불가)
     * @return 즐겨찾기 통계 도메인 객체
     */
    FavoriteStatistics getFavoriteStatistics(ComparisonPeriod period);

    /**
     * 특정 사용자의 즐겨찾기 목록을 조회합니다.
     *
     * @param condition 검색 조건
     * @return 즐겨찾기 목록
     */
    Page<FavoritePromptResult> searchFavorites(FavoriteSearchCondition condition);

}
