package com.gongdel.promptserver.application.port.in;

import com.gongdel.promptserver.application.port.in.query.view.GetViewStatisticsQuery;
import com.gongdel.promptserver.domain.model.statistics.DailyViewStatistics;
import com.gongdel.promptserver.domain.model.statistics.TopViewedPrompt;
import com.gongdel.promptserver.domain.model.statistics.ViewCountDistribution;
import com.gongdel.promptserver.domain.statistics.ComparisonPeriod;
import com.gongdel.promptserver.domain.view.ViewStatistics;

import java.util.List;

/**
 * 조회수 통계 조회 유스케이스 인터페이스입니다.
 * 다양한 기간(일간, 주간, 월간 등)에 대한 조회수 통계를 제공합니다.
 */
public interface ViewStatisticsQueryUseCase {

    /**
     * 인기 프롬프트 목록을 조회합니다.
     * 특정 기간 내, 동적 필터링을 지원합니다.
     *
     * @param query 통계 조회 쿼리 객체
     * @return 인기 프롬프트 목록
     */
    List<TopViewedPrompt> getTopViewedPrompts(GetViewStatisticsQuery query);

    /**
     * 일별 조회수 통계를 조회합니다.
     * 특정 프롬프트의 시간별 조회 패턴을 분석할 때 사용합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @param query            통계 조회 쿼리 객체
     * @return 일별 조회수 통계
     */
    List<DailyViewStatistics> getDailyViewStatistics(Long promptTemplateId, GetViewStatisticsQuery query);

    /**
     * 조회수 분포 통계를 조회합니다.
     * 전체 프롬프트들의 조회수 분포를 파악할 때 사용합니다.
     *
     * @param query 통계 조회 쿼리 객체
     * @return 조회수 분포 통계
     */
    List<ViewCountDistribution> getViewCountDistribution(GetViewStatisticsQuery query);

    /**
     * 특정 기간 내 총 조회수를 조회합니다.
     * 대시보드나 요약 통계에서 사용합니다.
     *
     * @param query 통계 조회 쿼리 객체
     * @return 기간 내 총 조회수
     */
    long getTotalViewCountByPeriod(GetViewStatisticsQuery query);

    /**
     * 특정 프롬프트들의 조회수를 일괄 조회합니다.
     * 여러 프롬프트의 조회수를 한 번에 가져올 때 사용합니다.
     *
     * @param query 통계 조회 쿼리 객체
     * @return 프롬프트별 조회수 목록
     */
    List<TopViewedPrompt> getViewCountsByPromptIds(GetViewStatisticsQuery query);

    /**
     * 지정된 기간의 조회수 통계 정보를 조회합니다.
     * 현재 기간과 이전 기간을 비교하여 증감률을 계산합니다.
     *
     * @param period 비교 기간 (null 불가)
     * @return 조회수 통계 도메인 객체
     */
    ViewStatistics getViewStatistics(ComparisonPeriod period);
}
