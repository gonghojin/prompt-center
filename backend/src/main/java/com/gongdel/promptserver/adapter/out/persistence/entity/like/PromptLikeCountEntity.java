package com.gongdel.promptserver.adapter.out.persistence.entity.like;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 프롬프트별 좋아요 수 집계 Projection 엔티티입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "prompt_like_count")
public class PromptLikeCountEntity {
    @Id
    @Column(name = "prompt_template_id")
    private Long promptTemplateId;

    @Column(nullable = false)
    private Long likeCount = 0L;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * PromptLikeCountEntity 생성자
     *
     * @param promptTemplateId 프롬프트 ID
     * @param likeCount        좋아요 수
     * @param updatedAt        마지막 업데이트 일시
     */
    @Builder
    public PromptLikeCountEntity(Long promptTemplateId, Long likeCount, LocalDateTime updatedAt) {
        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");
        Assert.notNull(likeCount, "likeCount must not be null");
        Assert.isTrue(likeCount >= 0, "likeCount must be zero or positive");
        Assert.notNull(updatedAt, "updatedAt must not be null");
        this.promptTemplateId = promptTemplateId;
        this.likeCount = likeCount;
        this.updatedAt = updatedAt;
    }

    /**
     * 좋아요 수를 delta만큼 증감시킵니다. (최소 0)
     *
     * @param delta 증감값 (양수: 증가, 음수: 감소)
     */
    public void applyLikeCountChange(long delta) {
        long newCount = this.likeCount + delta;
        this.likeCount = Math.max(0, newCount);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 좋아요 수를 1 증가시킵니다.
     */
    public void increaseLikeCount() {
        applyLikeCountChange(1);
    }

    /**
     * 좋아요 수를 1 감소시킵니다. (최소 0)
     */
    public void decreaseLikeCount() {
        applyLikeCountChange(-1);
    }
}
