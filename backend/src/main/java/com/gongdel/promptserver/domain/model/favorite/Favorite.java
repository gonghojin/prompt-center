package com.gongdel.promptserver.domain.model.favorite;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 즐겨찾기 도메인 모델입니다.
 * <p>
 * userId, promptTemplateId, createdAt은 null이 될 수 없습니다.
 */
@Getter
@ToString
@Builder
public class Favorite {
    /**
     * 즐겨찾기 고유 식별자
     */
    private final Long id;
    /**
     * 사용자 ID (필수)
     */
    private final Long userId;
    /**
     * 프롬프트 템플릿 ID (필수)
     */
    private final Long promptTemplateId;
    /**
     * 즐겨찾기 생성일시 (필수)
     */
    private final LocalDateTime createdAt;

    public Favorite(Long id, Long userId, Long promptTemplateId, LocalDateTime createdAt) {
        Assert.notNull(userId, "userId must not be null");
        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");
        Assert.notNull(createdAt, "createdAt must not be null");
        this.id = id;
        this.userId = userId;
        this.promptTemplateId = promptTemplateId;
        this.createdAt = createdAt;
    }
}
