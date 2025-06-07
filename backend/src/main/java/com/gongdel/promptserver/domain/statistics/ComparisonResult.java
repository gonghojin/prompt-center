package com.gongdel.promptserver.domain.statistics;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * 통계 비교 결과를 나타내는 값 객체입니다.
 * <p>
 * 현재 기간, 이전 기간의 프롬프트 수와 변동률을 포함합니다.
 */
@Getter
@Builder
@Slf4j
public class ComparisonResult {
    private final long currentCount;
    private final long previousCount;
    private final double percentageChange;

    /**
     * ComparisonResult 객체를 생성합니다.
     *
     * @param currentCount     현재 기간 프롬프트 수 (0 이상)
     * @param previousCount    이전 기간 프롬프트 수 (0 이상)
     * @param percentageChange 변동률 (null 불가)
     * @throws IllegalArgumentException 유효하지 않은 값이 전달된 경우
     */
    public ComparisonResult(long currentCount, long previousCount, double percentageChange) {
        Assert.isTrue(currentCount >= 0, "currentCount must be non-negative");
        Assert.isTrue(previousCount >= 0, "previousCount must be non-negative");
        // double은 null 체크 불필요, NaN 방지
        Assert.isTrue(!Double.isNaN(percentageChange), "percentageChange must be a valid number");
        this.currentCount = currentCount;
        this.previousCount = previousCount;
        this.percentageChange = percentageChange;
        log.debug("ComparisonResult created: currentCount={}, previousCount={}, percentageChange={}", currentCount,
            previousCount, percentageChange);
    }

    /**
     * 변동률을 계산하는 정적 팩토리 메서드
     */
    public static ComparisonResult of(long currentCount, long previousCount) {
        double percentageChange = 0.0;
        if (previousCount == 0) {
            percentageChange = currentCount > 0 ? 100.0 : 0.0;
        } else {
            percentageChange = ((double) (currentCount - previousCount) / previousCount) * 100.0;
        }
        return new ComparisonResult(currentCount, previousCount, percentageChange);
    }
}
