package com.gongdel.promptserver.application.port.out.like.query;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

/**
 * 프롬프트 좋아요 상태 조회 요청 값
 */
@Getter
public class LoadPromptLikeStatus {
    private final Long userId;
    private final Long promptTemplateId;

    /**
     * 프롬프트 좋아요 상태 조회 요청 값 생성자
     *
     * @param userId           사용자 ID
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @throws IllegalArgumentException 파라미터가 null인 경우
     */
    @Builder
    public LoadPromptLikeStatus(Long userId, Long promptTemplateId) {
        Assert.notNull(userId, "userId must not be null");
        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");
        this.userId = userId;
        this.promptTemplateId = promptTemplateId;
    }
}
