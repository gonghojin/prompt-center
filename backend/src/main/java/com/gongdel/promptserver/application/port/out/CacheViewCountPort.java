package com.gongdel.promptserver.application.port.out;

import com.gongdel.promptserver.domain.view.ViewCount;

/**
 * 조회수 캐싱 관련 작업을 담당하는 포트입니다.
 * DB에서 조회한 데이터를 캐시에 저장하는 역할을 합니다.
 */
public interface CacheViewCountPort {

    /**
     * 조회수 정보를 캐시에 저장합니다.
     *
     * @param viewCount 캐시할 조회수 정보
     */
    void cacheViewCount(ViewCount viewCount);

    /**
     * 특정 프롬프트의 조회수 캐시를 삭제합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     */
    void evictViewCountCache(Long promptTemplateId);

    /**
     * 조회수를 캐시에서 원자적으로 증가시킵니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 증가된 조회수
     */
    long incrementViewCountInCache(Long promptTemplateId);
}
