package com.gongdel.promptserver.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Pageable;

import java.util.Set;

/**
 * 프롬프트 검색 조건을 담는 불변 객체입니다.
 * <p>
 * - 고도화 검색 및 내 프롬프트 조회를 위한 다양한 필터와 검색어, 사용자 ID, 전용 플래그를 포함합니다.
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

    /**
     * 상태 필터 (다중 선택)
     */
    private final Set<PromptStatus> statusFilters;
    /**
     * 공개 범위 필터 (다중 선택)
     */
    private final Set<Visibility> visibilityFilters;
    /**
     * 통합 검색어 (제목, 설명, 태그 등)
     */
    private final String searchKeyword;
    /**
     * 사용자 ID (내 프롬프트 조회 시 사용)
     */
    private final Long userId;
    /**
     * 내 프롬프트 조회 여부
     */
    private final boolean isMyPrompts;

    private final boolean isFavoritePrompts;

    @Builder
    public PromptSearchCondition(
        String title,
        String description,
        String tag,
        Long categoryId,
        PromptStatus status,
        PromptSortType sortType,
        Pageable pageable,
        Set<PromptStatus> statusFilters,
        Set<Visibility> visibilityFilters,
        String searchKeyword,
        Long userId,
        boolean isMyPrompts,
        boolean isFavoritePrompts) {
        this.title = title;
        this.description = description;
        this.tag = tag;
        this.categoryId = categoryId;
        this.status = status;
        this.sortType = sortType;
        this.pageable = pageable;
        this.statusFilters = statusFilters;
        this.visibilityFilters = visibilityFilters;
        this.searchKeyword = searchKeyword;
        this.userId = userId;
        this.isMyPrompts = isMyPrompts;
        this.isFavoritePrompts = isFavoritePrompts;
    }

}
