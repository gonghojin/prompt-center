package com.gongdel.promptserver.application.port.in;

import com.gongdel.promptserver.application.port.in.query.view.GetViewCountQuery;
import com.gongdel.promptserver.domain.view.ViewCount;

import java.util.Optional;

/**
 * 조회수 조회 관련 쿼리 작업을 처리하는 유스케이스 인터페이스입니다.
 * 조회수 조회 및 검색 기능을 정의합니다.
 */
public interface ViewQueryUseCase {

    /**
     * 프롬프트의 조회수 정보를 조회합니다.
     * Redis 캐시를 우선 확인하고, 없으면 데이터베이스에서 조회합니다.
     *
     * @param query 조회수 조회 쿼리 객체
     * @return 조회수 정보 (없는 경우 빈 Optional 반환)
     */
    Optional<ViewCount> getViewCount(GetViewCountQuery query);

    /**
     * 프롬프트의 총 조회수를 조회합니다.
     * 빠른 응답을 위해 숫자만 반환합니다.
     *
     * @param query 조회수 조회 쿼리 객체
     * @return 총 조회수 (없는 경우 0 반환)
     */
    long getTotalViewCount(GetViewCountQuery query);
}
