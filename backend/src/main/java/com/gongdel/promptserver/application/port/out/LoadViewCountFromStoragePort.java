package com.gongdel.promptserver.application.port.out;

import com.gongdel.promptserver.domain.view.LoadViewCountQuery;
import com.gongdel.promptserver.domain.view.ViewCount;

import java.util.Optional;

/**
 * 데이터베이스에서 조회수를 조회하는 포트입니다.
 * 영구 저장소에서의 정확한 데이터 조회를 위한 인터페이스입니다.
 */
public interface LoadViewCountFromStoragePort {

    /**
     * 데이터베이스에서 조회수를 조회합니다.
     *
     * @param query 조회수 조회 쿼리
     * @return 저장된 조회수 정보 (없으면 Optional.empty())
     */
    Optional<ViewCount> loadViewCountFromStorage(LoadViewCountQuery query);

    /**
     * 프롬프트 템플릿 ID로 데이터베이스에서 조회수를 조회합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 저장된 조회수 정보 (없으면 Optional.empty())
     */
    Optional<ViewCount> loadViewCountFromStorage(Long promptTemplateId);
}
