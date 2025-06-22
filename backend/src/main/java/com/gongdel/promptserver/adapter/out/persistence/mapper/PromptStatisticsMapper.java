package com.gongdel.promptserver.adapter.out.persistence.mapper;

import com.gongdel.promptserver.domain.statistics.ComparisonPeriod;
import com.gongdel.promptserver.domain.statistics.ComparisonResult;
import com.gongdel.promptserver.domain.statistics.PromptStatistics;
import org.springframework.stereotype.Component;

/**
 * 프롬프트 통계 도메인 객체로 변환하는 매퍼입니다.
 */
@Component
public class PromptStatisticsMapper {
    /**
     * 통계 정보로 도메인 객체를 생성합니다.
     *
     * @param totalCount    전체 프롬프트 개수
     * @param currentCount  현재 기간 프롬프트 개수
     * @param previousCount 이전 기간 프롬프트 개수
     * @param period        비교 기간
     * @return PromptStatistics 도메인 객체
     */
    public PromptStatistics toDomain(long totalCount, long currentCount, long previousCount, ComparisonPeriod period) {
        return new PromptStatistics(
            totalCount,
            period,
            ComparisonResult.of(currentCount, previousCount));
    }
}
