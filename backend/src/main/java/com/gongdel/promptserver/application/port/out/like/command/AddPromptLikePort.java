package com.gongdel.promptserver.application.port.out.like.command;

/**
 * 프롬프트 좋아요 추가 포트입니다.
 */
public interface AddPromptLikePort {
    /**
     * 프롬프트 좋아요를 추가합니다.
     *
     * @param addPromptLike 좋아요 추가 요청 값
     */
    void addLike(AddPromptLike addPromptLike);
}
