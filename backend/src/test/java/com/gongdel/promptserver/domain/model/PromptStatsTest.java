package com.gongdel.promptserver.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PromptStats 도메인 모델 테스트
 */
class PromptStatsTest {

    @Test
    @DisplayName("기본 생성자로 PromptStats 객체를 생성하면 조회수와 좋아요 수가 0으로 초기화된다")
    void createPromptStats() {
        // when
        PromptStats stats = new PromptStats();

        // then
        assertThat(stats.getViewCount()).isZero();
        assertThat(stats.getFavoriteCount()).isZero();
    }

    @Test
    @DisplayName("조회수를 증가시킬 수 있다")
    void incrementViewCount() {
        // given
        PromptStats stats = new PromptStats();
        int initialViewCount = stats.getViewCount();

        // when
        stats.incrementViewCount();

        // then
        assertThat(stats.getViewCount()).isEqualTo(initialViewCount + 1);
    }

    @Test
    @DisplayName("좋아요 수를 증가시킬 수 있다")
    void incrementFavoriteCount() {
        // given
        PromptStats stats = new PromptStats();
        int initialFavoriteCount = stats.getFavoriteCount();

        // when
        stats.incrementFavoriteCount();

        // then
        assertThat(stats.getFavoriteCount()).isEqualTo(initialFavoriteCount + 1);
    }

    @Test
    @DisplayName("좋아요 수를 감소시킬 수 있다")
    void decrementFavoriteCount() {
        // given
        PromptStats stats = new PromptStats();
        stats.incrementFavoriteCount();
        stats.incrementFavoriteCount();
        int initialFavoriteCount = stats.getFavoriteCount();

        // when
        stats.decrementFavoriteCount();

        // then
        assertThat(stats.getFavoriteCount()).isEqualTo(initialFavoriteCount - 1);
    }

    @Test
    @DisplayName("좋아요 수가 0일 때 감소시키면 0으로 유지된다")
    void decrementFavoriteCountWhenZero() {
        // given
        PromptStats stats = new PromptStats();
        assertThat(stats.getFavoriteCount()).isZero(); // 초기값이 0인지 확인

        // when
        stats.decrementFavoriteCount();

        // then
        assertThat(stats.getFavoriteCount()).isZero(); // 여전히 0인지 확인
    }

    @Test
    @DisplayName("조회수를 초기화할 수 있다")
    void resetViewCount() {
        // given
        PromptStats stats = new PromptStats();
        stats.incrementViewCount();
        stats.incrementViewCount();
        stats.incrementViewCount();
        assertThat(stats.getViewCount()).isPositive(); // 증가했는지 확인

        // when
        stats.resetViewCount();

        // then
        assertThat(stats.getViewCount()).isZero();
    }

    @Test
    @DisplayName("좋아요 수를 초기화할 수 있다")
    void resetFavoriteCount() {
        // given
        PromptStats stats = new PromptStats();
        stats.incrementFavoriteCount();
        stats.incrementFavoriteCount();
        assertThat(stats.getFavoriteCount()).isPositive(); // 증가했는지 확인

        // when
        stats.resetFavoriteCount();

        // then
        assertThat(stats.getFavoriteCount()).isZero();
    }

    @Test
    @DisplayName("여러 번 조회수를 증가시킬 수 있다")
    void multipleIncrementViewCount() {
        // given
        PromptStats stats = new PromptStats();
        int incrementCount = 5;

        // when
        for (int i = 0; i < incrementCount; i++) {
            stats.incrementViewCount();
        }

        // then
        assertThat(stats.getViewCount()).isEqualTo(incrementCount);
    }

    @Test
    @DisplayName("여러 번 좋아요 수를 증가시키고 감소시킬 수 있다")
    void multipleIncrementAndDecrementFavoriteCount() {
        // given
        PromptStats stats = new PromptStats();

        // when: 3번 증가, 1번 감소
        stats.incrementFavoriteCount();
        stats.incrementFavoriteCount();
        stats.incrementFavoriteCount();
        stats.decrementFavoriteCount();

        // then
        assertThat(stats.getFavoriteCount()).isEqualTo(2);
    }
}
