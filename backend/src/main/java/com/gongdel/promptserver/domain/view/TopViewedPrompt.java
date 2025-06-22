package com.gongdel.promptserver.domain.view;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 조회수 기준 인기 프롬프트 정보를 나타내는 도메인 객체입니다.
 */
@Getter
@ToString
@Builder
public class TopViewedPrompt {
    /**
     * 순위
     */
    private final int rank;

    /**
     * 프롬프트 UUID
     */
    private final UUID promptTemplateUuid;

    /**
     * 프롬프트 제목
     */
    private final String title;

    /**
     * 카테고리명
     */
    private final String categoryName;

    /**
     * 기간 내 조회수
     */
    private final long totalViews;

    /**
     * 전체 누적 조회수
     */
    private final long allTimeViews;

    /**
     * 일평균 조회수
     */
    private final double averageDailyViews;

    /**
     * 작성자명
     */
    private final String authorName;

    /**
     * 마지막 조회 시점
     */
    private final LocalDateTime lastViewedAt;

    /**
     * TopViewedPrompt 객체를 생성합니다.
     *
     * @param rank               순위 (1 이상)
     * @param promptTemplateUuid 프롬프트 UUID (필수)
     * @param title              프롬프트 제목 (필수)
     * @param categoryName       카테고리명
     * @param totalViews         기간 내 조회수 (0 이상)
     * @param allTimeViews       전체 누적 조회수 (0 이상)
     * @param averageDailyViews  일평균 조회수 (0 이상)
     * @param authorName         작성자명
     * @param lastViewedAt       마지막 조회 시점
     */
    @Builder
    public TopViewedPrompt(int rank, UUID promptTemplateUuid, String title, String categoryName,
                           long totalViews, long allTimeViews, double averageDailyViews,
                           String authorName, LocalDateTime lastViewedAt) {
        Assert.isTrue(rank > 0, "rank must be positive");
        Assert.notNull(promptTemplateUuid, "promptTemplateUuid must not be null");
        Assert.hasText(title, "title must not be blank");
        Assert.isTrue(totalViews >= 0, "totalViews must be non-negative");
        Assert.isTrue(allTimeViews >= 0, "allTimeViews must be non-negative");
        Assert.isTrue(averageDailyViews >= 0, "averageDailyViews must be non-negative");

        this.rank = rank;
        this.promptTemplateUuid = promptTemplateUuid;
        this.title = title;
        this.categoryName = categoryName == null ? "" : categoryName;
        this.totalViews = totalViews;
        this.allTimeViews = allTimeViews;
        this.averageDailyViews = averageDailyViews;
        this.authorName = authorName == null ? "" : authorName;
        this.lastViewedAt = lastViewedAt;
    }

    /**
     * 기간 내 조회수가 전체 조회수보다 많은지 확인합니다.
     * (데이터 일관성 검증용)
     *
     * @return 기간 내 조회수가 전체 조회수보다 많은 경우 true
     */
    public boolean hasInconsistentData() {
        return totalViews > allTimeViews;
    }

    /**
     * 상위 랭킹 프롬프트인지 확인합니다. (TOP 10)
     *
     * @return TOP 10 프롬프트인 경우 true
     */
    public boolean isTopRanked() {
        return rank <= 10;
    }

    /**
     * 일평균 조회수를 반올림하여 반환합니다.
     *
     * @return 반올림된 일평균 조회수
     */
    public long getRoundedAverageDailyViews() {
        return Math.round(averageDailyViews);
    }
}
