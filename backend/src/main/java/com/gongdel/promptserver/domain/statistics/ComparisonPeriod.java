package com.gongdel.promptserver.domain.statistics;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 통계 비교 기간을 나타내는 값 객체입니다.
 * <p>
 * 시작일, 종료일을 포함합니다.
 */
@Getter
@Builder
@Slf4j
public class ComparisonPeriod {
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    /**
     * 통계 비교 기간을 나타내는 값 객체입니다.
     * <p>
     * 시작일, 종료일을 포함합니다.
     */
    public ComparisonPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        Assert.notNull(startDate, "startDate must not be null");
        Assert.notNull(endDate, "endDate must not be null");
        this.startDate = startDate;
        this.endDate = endDate;
        log.debug("ComparisonPeriod created: startDate={}, endDate={}", startDate, endDate);
    }
}
