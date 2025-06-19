package com.gongdel.promptserver.domain.like;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 프롬프트 좋아요 상태 및 카운트 도메인 객체입니다.
 */
@Getter
@ToString
@EqualsAndHashCode
public class LikeStatus {
    /**
     * 좋아요 여부
     */
    private final boolean liked;
    /**
     * 좋아요 수
     */
    private final long likeCount;

    /**
     * LikeStatus 생성자
     *
     * @param liked     좋아요 여부
     * @param likeCount 좋아요 수
     */
    @Builder
    public LikeStatus(boolean liked, long likeCount) {
        this.liked = liked;
        this.likeCount = likeCount;
    }
}
