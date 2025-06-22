package com.gongdel.promptserver.domain.model.statistics;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * 일별 조회수 통계 정보를 담는 클래스입니다.
 */
@Getter
@ToString
@Builder
public class DailyViewStatistics {
    /**
     * 조회 날짜
     */
    private final LocalDate viewDate;

    /**
     * 해당 날짜의 조회수
     */
    private final Long viewCount;

    // QueryDSL에서 사용할 생성자
    public DailyViewStatistics(LocalDate viewDate, Long viewCount) {
        this.viewDate = viewDate;
        this.viewCount = viewCount;
    }
}
