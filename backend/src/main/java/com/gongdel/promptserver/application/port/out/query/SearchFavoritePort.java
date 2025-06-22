package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.model.favorite.FavoritePromptResult;
import com.gongdel.promptserver.domain.model.favorite.FavoriteSearchCondition;
import org.springframework.data.domain.Page;

/**
 * 즐겨찾기 검색(Search) 포트입니다.
 * 동적 조건, 페이징, 정렬, 키워드 등 복합 검색을 지원합니다.
 */
public interface SearchFavoritePort {
    /**
     * 즐겨찾기 검색 조건에 따라 목록을 조회합니다.
     *
     * @param condition 즐겨찾기 검색 조건
     * @return 즐겨찾기 프롬프트 결과 페이지
     */
    Page<FavoritePromptResult> searchFavorites(FavoriteSearchCondition condition);
}
