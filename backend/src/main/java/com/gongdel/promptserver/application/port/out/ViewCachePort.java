package com.gongdel.promptserver.application.port.out;

import com.gongdel.promptserver.domain.view.ViewCount;

import java.util.Optional;

/**
 * 조회수 캐싱을 위한 포트 인터페이스입니다.
 * <p>
 * Redis를 사용한 조회수 캐싱 기능을 정의합니다.
 */
public interface ViewCachePort {

    /**
     * 조회수 정보를 캐시에 저장합니다.
     *
     * @param viewCount 저장할 조회수 정보
     */
    void cacheViewCount(ViewCount viewCount);

    /**
     * 캐시된 조회수 정보를 조회합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 캐시된 조회수 정보 (캐시에 없는 경우 Optional.empty())
     */
    Optional<ViewCount> getCachedViewCount(Long promptTemplateId);

    /**
     * 조회수 캐시를 삭제합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     */
    void evictViewCountCache(Long promptTemplateId);

    /**
     * 조회수를 원자적으로 증가시킵니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 증가된 조회수
     */
    long incrementViewCount(Long promptTemplateId);
}
