package com.gongdel.promptserver.application.port.out;

import com.gongdel.promptserver.domain.view.LoadViewCountQuery;
import com.gongdel.promptserver.domain.view.ViewCount;

import java.util.Optional;

/**
 * Redis 캐시에서 조회수를 조회하는 포트입니다.
 * 고속 조회를 위한 캐시 전용 인터페이스입니다.
 */
public interface LoadViewCountFromCachePort {

    /**
     * 캐시에서 조회수를 조회합니다.
     *
     * @param query 조회수 조회 쿼리
     * @return 캐시된 조회수 정보 (캐시에 없으면 Optional.empty())
     */
    Optional<ViewCount> loadViewCountFromCache(LoadViewCountQuery query);

    /**
     * 프롬프트 템플릿 ID로 캐시에서 조회수를 조회합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 캐시된 조회수 정보 (캐시에 없으면 Optional.empty())
     */
    Optional<ViewCount> loadViewCountFromCache(Long promptTemplateId);
}
