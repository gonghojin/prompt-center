package com.gongdel.promptserver.application.port.in.command.like;

import java.util.UUID;

/**
 * 프롬프트 좋아요 명령 유즈케이스 인터페이스입니다.
 */
public interface PromptLikeCommandUseCase {
    /**
     * 프롬프트에 좋아요를 추가합니다.
     *
     * @param userId           사용자 ID
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 갱신된 좋아요 수
     */
    long addLike(Long userId, UUID promptTemplateId);

    /**
     * 프롬프트 좋아요를 취소합니다.
     *
     * @param userId           사용자 ID
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 갱신된 좋아요 수
     */
    long removeLike(Long userId, UUID promptTemplateId);
}
