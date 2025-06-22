package com.gongdel.promptserver.application.port.out.command;

import com.gongdel.promptserver.domain.model.favorite.Favorite;

/**
 * 즐겨찾기(Favorite) 저장을 위한 아웃바운드 포트입니다.
 */
public interface SaveFavoritePort {
    /**
     * 즐겨찾기 도메인 객체를 저장합니다.
     *
     * @param favorite 즐겨찾기 도메인 객체
     * @return 저장된 즐겨찾기 도메인 객체
     */
    Favorite save(Favorite favorite);
}
