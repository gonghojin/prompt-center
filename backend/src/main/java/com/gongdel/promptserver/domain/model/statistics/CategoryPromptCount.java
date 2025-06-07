package com.gongdel.promptserver.domain.model.statistics;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 카테고리별 프롬프트 개수 정보를 담는 도메인 클래스입니다.
 */
@Getter
@ToString
@Builder
public class CategoryPromptCount {
    /**
     * 카테고리 ID
     */
    private final Long categoryId;

    /**
     * 카테고리명
     */
    private final String categoryName;

    /**
     * 프롬프트 개수
     */
    private final long promptCount;

    // QueryDSL에서 사용할 생성자
    public CategoryPromptCount(Long categoryId, String categoryName, Long promptCount) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.promptCount = promptCount;
    }
}
