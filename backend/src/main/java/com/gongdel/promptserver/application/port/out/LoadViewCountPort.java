package com.gongdel.promptserver.application.port.out;

import com.gongdel.promptserver.domain.view.LoadViewCountQuery;
import com.gongdel.promptserver.domain.view.ViewCount;

import java.util.Optional;

/**
 * 조회수 조회를 위한 포트 인터페이스
 */
public interface LoadViewCountPort {

    /**
     * 프롬프트 템플릿의 조회수를 조회합니다.
     *
     * @param query 조회수 조회 쿼리
     * @return 조회수 정보 (존재하지 않는 경우 Optional.empty())
     */
    Optional<ViewCount> loadViewCount(LoadViewCountQuery query);
}
