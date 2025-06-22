package com.gongdel.promptserver.domain.model.statistics;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

/**
 * 조회수 구간 정보를 담는 Value Object 클래스입니다.
 */
@Getter
@ToString
@Builder
public class ViewCountRange {
    /**
     * 구간 최솟값 (포함)
     */
    private final long minCount;

    /**
     * 구간 최댓값 (포함, null이면 무제한)
     */
    private final Long maxCount;

    /**
     * 구간 레이블
     */
    private final String label;

    public ViewCountRange(long minCount, Long maxCount, String label) {
        Assert.isTrue(minCount >= 0, "minCount must be non-negative");
        Assert.isTrue(maxCount == null || maxCount >= minCount, "maxCount must be greater than or equal to minCount");
        Assert.hasText(label, "label must not be blank");

        this.minCount = minCount;
        this.maxCount = maxCount;
        this.label = label;
    }

    /**
     * 무제한 상한 구간 생성 팩토리 메서드
     */
    public static ViewCountRange of(long minCount, String label) {
        return new ViewCountRange(minCount, null, label);
    }

    /**
     * 제한된 구간 생성 팩토리 메서드
     */
    public static ViewCountRange of(long minCount, long maxCount, String label) {
        return new ViewCountRange(minCount, maxCount, label);
    }

    /**
     * 기본 조회수 구간들을 반환합니다.
     */
    public static ViewCountRange[] getDefaultRanges() {
        return new ViewCountRange[]{
            ViewCountRange.of(0, 10, "0-10"),
            ViewCountRange.of(11, 50, "11-50"),
            ViewCountRange.of(51, 100, "51-100"),
            ViewCountRange.of(101, 500, "101-500"),
            ViewCountRange.of(501, 1000, "501-1000"),
            ViewCountRange.of(1001, "1000+")
        };
    }

    /**
     * 상한이 무제한인지 확인합니다.
     */
    public boolean isUnbounded() {
        return maxCount == null;
    }
}
