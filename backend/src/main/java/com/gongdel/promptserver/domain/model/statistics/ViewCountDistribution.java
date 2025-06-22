package com.gongdel.promptserver.domain.model.statistics;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 조회수 분포 통계 정보를 담는 클래스입니다.
 */
@Getter
@ToString
@Builder
public class ViewCountDistribution {
    /**
     * 조회수 구간 (예: "0-10", "11-50")
     */
    private final String viewRange;

    /**
     * 해당 구간의 프롬프트 개수
     */
    private final Long count;

    // QueryDSL에서 사용할 생성자
    public ViewCountDistribution(String viewRange, Long count) {
        this.viewRange = viewRange;
        this.count = count;
    }
}
