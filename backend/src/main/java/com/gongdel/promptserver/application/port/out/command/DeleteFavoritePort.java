package com.gongdel.promptserver.application.port.out.command;

/**
 * 즐겨찾기(Favorite) 삭제를 위한 아웃바운드 포트입니다.
 */
public interface DeleteFavoritePort {
    /**
     * 사용자와 프롬프트 ID로 즐겨찾기를 삭제합니다.
     *
     * @param userId           사용자 ID
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 삭제된 개수
     */
    long deleteByUserIdAndPromptTemplateId(Long userId, Long promptTemplateId);
}
