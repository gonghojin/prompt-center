package com.gongdel.promptserver.application.port.out.like.query;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;

/**
 * 내가 좋아요한 프롬프트 목록 조회 요청 값
 */
@Getter
public class FindLikedPrompts {
    private final Long userId;
    private final Pageable pageable;

    /**
     * 내가 좋아요한 프롬프트 목록 조회 요청 값 생성자
     *
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @throws IllegalArgumentException 파라미터가 null인 경우
     */
    @Builder
    public FindLikedPrompts(Long userId, Pageable pageable) {
        Assert.notNull(userId, "userId must not be null");
        Assert.notNull(pageable, "pageable must not be null");
        this.userId = userId;
        this.pageable = pageable;
    }
}
