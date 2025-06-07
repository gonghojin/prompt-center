package com.gongdel.promptserver.domain.model.statistics;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 내 프롬프트 통계 결과를 담는 불변 객체입니다.
 * <p>
 * - 전체, 임시저장(DRAFT), 발행(PUBLISHED), 보관(ARCHIVED) 상태별 개수를 포함합니다.
 */
@Getter
@Builder
@ToString
public class PromptStatisticsResult {
    /**
     * 전체 개수
     */
    private final long totalCount;
    /**
     * 임시저장 개수
     */
    private final long draftCount;
    /**
     * 발행 개수
     */
    private final long publishedCount;
    /**
     * 보관 개수
     */
    private final long archivedCount;
}
