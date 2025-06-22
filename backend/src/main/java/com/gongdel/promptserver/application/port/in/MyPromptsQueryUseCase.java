package com.gongdel.promptserver.application.port.in;

import com.gongdel.promptserver.domain.model.PromptSearchResult;
import com.gongdel.promptserver.domain.model.my.MyPromptSearchCondition;
import com.gongdel.promptserver.domain.model.statistics.PromptStatisticsResult;
import org.springframework.data.domain.Page;

/**
 * 내 프롬프트 관리(목록, 통계 등) 전용 유스케이스 포트입니다.
 */
public interface MyPromptsQueryUseCase {
    /**
     * 내 프롬프트 목록을 조회합니다.
     *
     * @param condition 내 프롬프트 검색 조건
     * @return 프롬프트 검색 결과 페이지
     */
    Page<PromptSearchResult> findMyPrompts(MyPromptSearchCondition condition);

    /**
     * 내 프롬프트 통계를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 프롬프트 통계 결과
     */
    PromptStatisticsResult getMyPromptStatistics(Long userId);

    /**
     * 내가 생성한 프롬프트의 총 좋아요 수를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 총 좋아요 수
     */
    long getMyTotalLikeCount(Long userId);

    /**
     * 내가 생성한 프롬프트의 총 조회수를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 총 조회수
     */
    long getMyTotalViewCount(Long userId);
}
