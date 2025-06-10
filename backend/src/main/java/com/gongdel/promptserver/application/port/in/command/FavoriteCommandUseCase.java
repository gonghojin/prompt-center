package com.gongdel.promptserver.application.port.in.command;

import com.gongdel.promptserver.domain.model.favorite.Favorite;

import java.util.UUID;

/**
 * 즐겨찾기 관련 Command 유스케이스 인터페이스입니다.
 */
public interface FavoriteCommandUseCase {
    /**
     * 프롬프트를 즐겨찾기에 추가합니다.
     *
     * @param userId           사용자 ID
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 즐겨찾기 추가 결과
     */
    Favorite addFavorite(Long userId, UUID promptTemplateId);

    /**
     * 프롬프트 즐겨찾기를 삭제합니다.
     *
     * @param userId           사용자 ID
     * @param promptTemplateId 프롬프트 템플릿 ID
     */
    void removeFavorite(Long userId, UUID promptTemplateId);
}
