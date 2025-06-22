package com.gongdel.promptserver.application.port.out.like.command;

/**
 * 프롬프트 좋아요 취소 포트입니다.
 */
public interface RemovePromptLikePort {
    /**
     * 프롬프트 좋아요를 취소합니다.
     *
     * @param removePromptLike 좋아요 취소 요청 값
     */
    void removeLike(RemovePromptLike removePromptLike);
}
