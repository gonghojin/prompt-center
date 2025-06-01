package com.gongdel.promptserver.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Pageable;

/**
 * 프롬프트 검색 조건을 담는 불변 객체입니다.
 */
@Getter
@Builder
@ToString
public class PromptSearchCondition {
    /**
     * 프롬프트 제목
     */
    private final String title;
    /**
     * 프롬프트 설명
     */
    private final String description;
    /**
     * 태그
     */
    private final String tag;
    /**
     * 카테고리 ID
     */
    private final Long categoryId;
    /**
     * 프롬프트 상태
     */
    private final PromptStatus status;
    /**
     * 정렬 타입
     */
    private final PromptSortType sortType;
    /**
     * 페이징 정보
     */
    private final Pageable pageable;
}
