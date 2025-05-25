package com.gongdel.promptserver.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프롬프트 템플릿의 통계 정보를 관리하는 클래스
 * 조회수와 좋아요 수를 추적합니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PromptStats {
    private static final int MIN_COUNT = 0;

    private int viewCount;

    private int favoriteCount;

    /**
     * 조회수를 1 증가시킵니다.
     */
    public void incrementViewCount() {
        this.viewCount++;
    }

    /**
     * 좋아요 수를 1 증가시킵니다.
     */
    public void incrementFavoriteCount() {
        this.favoriteCount++;
    }

    /**
     * 좋아요 수를 1 감소시킵니다.
     * 최소값은 0입니다.
     */
    public void decrementFavoriteCount() {
        if (this.favoriteCount > MIN_COUNT) {
            this.favoriteCount--;
        }
    }

    /**
     * 조회수를 초기화합니다.
     */
    public void resetViewCount() {
        this.viewCount = MIN_COUNT;
    }

    /**
     * 좋아요 수를 초기화합니다.
     */
    public void resetFavoriteCount() {
        this.favoriteCount = MIN_COUNT;
    }

    public int getViewCount() {
        return viewCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }
}
