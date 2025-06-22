package com.gongdel.promptserver.application.port.out.like.query;

import com.gongdel.promptserver.domain.like.LikedPromptResult;
import org.springframework.data.domain.Page;

/**
 * 내가 좋아요한 프롬프트 목록을 조회하는 포트입니다.
 */
public interface FindLikedPromptsPort {
    /**
     * 내가 좋아요한 프롬프트 목록을 조회합니다.
     *
     * @param findLikedPrompts 조회 요청 값
     * @return 좋아요한 프롬프트 목록(페이징)
     */
    Page<LikedPromptResult> findLikedPrompts(FindLikedPrompts findLikedPrompts);
}
