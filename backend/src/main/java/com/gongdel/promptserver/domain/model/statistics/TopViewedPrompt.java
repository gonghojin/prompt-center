package com.gongdel.promptserver.domain.model.statistics;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 인기 프롬프트 정보를 담는 DTO 클래스입니다.
 */
@Getter
@ToString
@Builder
public class TopViewedPrompt {
    /**
     * 프롬프트 템플릿 ID
     */
    private final Long promptTemplateId;

    /**
     * 조회수
     */
    private final Long viewCount;

    /**
     * 프롬프트 제목
     */
    private final String title;

    /**
     * 카테고리명
     */
    private final String categoryName;

    /**
     * 작성자명
     */
    private final String authorName;

    /**
     * 마지막 조회 시점
     */
    private final LocalDateTime lastViewedAt;

    // QueryDSL에서 사용할 생성자
    public TopViewedPrompt(Long promptTemplateId, Long viewCount, String title,
                           String categoryName, String authorName, LocalDateTime lastViewedAt) {
        this.promptTemplateId = promptTemplateId;
        this.viewCount = viewCount;
        this.title = title;
        this.categoryName = categoryName;
        this.authorName = authorName;
        this.lastViewedAt = lastViewedAt;
    }

    // 기본 생성자 (프로젝션용)
    public TopViewedPrompt(Long promptTemplateId, Long viewCount, String title, String categoryName) {
        this(promptTemplateId, viewCount, title, categoryName, null, null);
    }
}
