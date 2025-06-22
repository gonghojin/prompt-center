package com.gongdel.promptserver.adapter.out.persistence.repository;

import com.gongdel.promptserver.adapter.out.persistence.entity.favorite.FavoriteEntity;
import com.gongdel.promptserver.domain.model.favorite.FavoriteSearchCondition;
import org.springframework.data.domain.Page;

/**
 * QueryDSL 기반 즐겨찾기 검색 전용 레포지토리입니다.
 * 동적 조건, 페이징, 정렬, 키워드 등 복합 검색을 지원합니다.
 */
public interface FavoriteQueryRepository {
    /**
     * 즐겨찾기 검색 조건에 따라 목록을 조회합니다.
     *
     * @param condition 즐겨찾기 검색 조건
     * @return 즐겨찾기 엔티티 페이지
     */
    Page<FavoriteEntity> searchFavorites(FavoriteSearchCondition condition);
}
