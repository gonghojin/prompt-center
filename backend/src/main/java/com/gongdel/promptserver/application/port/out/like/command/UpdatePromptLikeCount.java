package com.gongdel.promptserver.application.port.out.like.command;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

/**
 * 프롬프트별 좋아요 수 Projection 갱신 요청 값
 */
@Getter
@ToString
@EqualsAndHashCode(of = {"promptTemplateId", "likeCount"})
public class UpdatePromptLikeCount {
    private static final long INCREMENT = 1L;
    private static final long DECREMENT = -1L;

    private final Long promptTemplateId;
    private final long likeCount;

    /**
     * Projection 갱신 요청 값 생성자
     *
     * @param promptTemplateId 프롬프트 ID (null 불가)
     * @param likeCount        갱신할 좋아요 수
     * @throws IllegalArgumentException promptTemplateId가 null인 경우
     */
    @Builder
    public UpdatePromptLikeCount(Long promptTemplateId, long likeCount) {
        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");
        this.promptTemplateId = promptTemplateId;
        this.likeCount = likeCount;
    }

    /**
     * 좋아요 1 증가 요청 객체 생성
     *
     * @param promptTemplateId 프롬프트 ID (null 불가)
     * @return 좋아요 1 증가 요청 객체
     */
    public static UpdatePromptLikeCount increment(Long promptTemplateId) {
        return new UpdatePromptLikeCount(promptTemplateId, INCREMENT);
    }

    /**
     * 좋아요 1 감소 요청 객체 생성
     *
     * @param promptTemplateId 프롬프트 ID (null 불가)
     * @return 좋아요 1 감소 요청 객체
     */
    public static UpdatePromptLikeCount decrement(Long promptTemplateId) {
        return new UpdatePromptLikeCount(promptTemplateId, DECREMENT);
    }


}
