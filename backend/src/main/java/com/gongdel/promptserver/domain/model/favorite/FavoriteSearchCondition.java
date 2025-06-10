package com.gongdel.promptserver.domain.model.favorite;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;

/**
 * 즐겨찾기 목록 조회용 검색 조건을 담는 불변 객체입니다.
 * <p>
 * - 사용자 ID, 통합 검색어, 정렬, 페이징 정보를 포함합니다.
 * - userId, pageable은 필수값입니다.
 */
@Getter
@ToString
@Builder
public class FavoriteSearchCondition {
    /**
     * 통합 검색어 (프롬프트 제목, 설명, 태그 등)
     */
    private final String searchKeyword;
    /**
     * 정렬 타입 (예: 최신순, 제목순 등)
     */
    private final String sortType;
    /**
     * 페이징 정보 (필수)
     */
    private final Pageable pageable;
    /**
     * 사용자 ID (필수)
     */
    private final Long userId;

    public FavoriteSearchCondition(String searchKeyword, String sortType, Pageable pageable, Long userId) {
        Assert.notNull(userId, "userId must not be null");
        Assert.notNull(pageable, "pageable must not be null");
        this.searchKeyword = searchKeyword;
        this.sortType = sortType;
        this.pageable = pageable;
        this.userId = userId;
    }
}
