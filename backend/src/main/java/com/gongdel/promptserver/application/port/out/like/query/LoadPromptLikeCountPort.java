package com.gongdel.promptserver.application.port.out.like.query;

/**
 * 프롬프트별 좋아요 수 Projection을 조회하는 포트입니다.
 */
public interface LoadPromptLikeCountPort {
    /**
     * 좋아요 수를 조회합니다.
     *
     * @param promptTemplateId 프롬프트 ID
     * @return 좋아요 수
     */
    long loadLikeCount(Long promptTemplateId);
}
