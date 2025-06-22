package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.model.favorite.FavoritePromptResult;
import com.gongdel.promptserver.domain.model.favorite.FavoriteSearchCondition;
import org.springframework.data.domain.Page;

/**
 * 즐겨찾기 목록 조회 포트
 */
public interface FindFavoritesPort {
    /**
     * 즐겨찾기 목록을 조회합니다.
     *
     * @param condition 즐겨찾기 검색 조건
     * @return 즐겨찾기 프롬프트 결과 페이지
     */
    Page<FavoritePromptResult> findFavorites(FavoriteSearchCondition condition);
}
