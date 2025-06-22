package com.gongdel.promptserver.application.port.out.like.command;

import lombok.Getter;
import org.springframework.util.Assert;

/**
 * 프롬프트 좋아요 추가 요청 값
 */
@Getter
public class AddPromptLike {
    private final Long userId;
    private final Long promptTemplateId;

    /**
     * 프롬프트 좋아요 추가 요청 값 생성자
     *
     * @param userId           사용자 ID
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @throws IllegalArgumentException 파라미터가 null인 경우
     */
    private AddPromptLike(Long userId, Long promptTemplateId) {
        Assert.notNull(userId, "userId must not be null");
        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");
        this.userId = userId;
        this.promptTemplateId = promptTemplateId;
    }

    /**
     * 프롬프트 좋아요 추가 요청 값 생성 팩토리 메서드
     *
     * @param userId           사용자 ID
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return AddPromptLike 인스턴스
     */
    public static AddPromptLike of(Long userId, Long promptTemplateId) {
        return new AddPromptLike(userId, promptTemplateId);
    }
}
