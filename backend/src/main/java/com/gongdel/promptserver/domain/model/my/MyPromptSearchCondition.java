package com.gongdel.promptserver.domain.model.my;

import com.gongdel.promptserver.domain.model.PromptSortType;
import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.model.Visibility;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;

import java.util.Set;

/**
 * 내 프롬프트 전용 검색 조건을 담는 불변 객체입니다.
 * <p>
 * - 상태/공개범위 다중 필터, 통합 검색어, 정렬, 카테고리, 페이징, 사용자 ID를 포함합니다.
 * - userId, pageable은 필수값입니다.
 */
@Getter
@ToString
@Builder
public class MyPromptSearchCondition {
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
     * 정렬 타입
     */
    private final PromptSortType sortType;
    /**
     * 페이징 정보 (필수)
     */
    private final Pageable pageable;
    /**
     * 사용자 ID (필수)
     */
    private final Long userId;

    public MyPromptSearchCondition(Set<PromptStatus> statusFilters,
                                   Set<Visibility> visibilityFilters,
                                   String searchKeyword,
                                   PromptSortType sortType,
                                   Pageable pageable,
                                   Long userId) {
        Assert.notNull(userId, "userId must not be null");
        Assert.notNull(pageable, "pageable must not be null");
        this.statusFilters = statusFilters;
        this.visibilityFilters = visibilityFilters;
        this.searchKeyword = searchKeyword;
        this.sortType = sortType;
        this.pageable = pageable;
        this.userId = userId;
    }
}
