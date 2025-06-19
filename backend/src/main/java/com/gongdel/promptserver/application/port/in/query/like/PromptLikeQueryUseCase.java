package com.gongdel.promptserver.application.port.in.query.like;

import com.gongdel.promptserver.application.port.out.like.query.FindLikedPrompts;
import com.gongdel.promptserver.application.port.out.like.query.LoadPromptLikeStatus;
import com.gongdel.promptserver.domain.like.LikeStatus;
import com.gongdel.promptserver.domain.like.LikedPromptResult;
import org.springframework.data.domain.Page;

/**
 * 프롬프트 좋아요 조회 유즈케이스 인터페이스입니다.
 */
public interface PromptLikeQueryUseCase {
    /**
     * 프롬프트 좋아요 상태 및 카운트를 조회합니다.
     *
     * @param loadPromptLikeStatus 좋아요 상태 조회 요청 값
     * @return 좋아요 상태 및 카운트
     */
    LikeStatus getLikeStatus(LoadPromptLikeStatus loadPromptLikeStatus);

    /**
     * 프롬프트별 좋아요 수를 조회합니다.
     *
     * @param promptTemplateId 프롬프트 ID
     * @return 좋아요 수
     */
    long getLikeCount(Long promptTemplateId);

    /**
     * 내가 좋아요한 프롬프트 목록을 조회합니다.
     *
     * @param findLikedPrompts 좋아요한 프롬프트 목록 조회 요청 값
     * @return 좋아요한 프롬프트 목록(페이징)
     */
    Page<LikedPromptResult> getLikedPrompts(FindLikedPrompts findLikedPrompts);
}
