package com.gongdel.promptserver.application.port.out.like.query;

import com.gongdel.promptserver.domain.like.LikeStatus;

/**
 * 프롬프트 좋아요 상태를 조회하는 포트입니다.
 */
public interface LoadPromptLikeStatusPort {
    /**
     * 프롬프트 좋아요 상태를 조회합니다.
     *
     * @param loadPromptLikeStatus 좋아요 상태 조회 요청 값
     * @return 좋아요 상태 및 카운트
     */
    LikeStatus loadStatus(LoadPromptLikeStatus loadPromptLikeStatus);
}
