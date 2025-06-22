package com.gongdel.promptserver.application.port.out.like.command;

/**
 * 프롬프트별 좋아요 수 Projection을 갱신하는 포트입니다.
 */
public interface UpdatePromptLikeCountPort {
    /**
     * 좋아요 수를 갱신합니다.
     *
     * @param update Projection 갱신 요청 값
     * @return 갱신된 좋아요 수
     */
    long updateLikeCount(UpdatePromptLikeCount update);
}
