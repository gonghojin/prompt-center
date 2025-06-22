package com.gongdel.promptserver.adapter.in.rest.controller.dashboard;

import com.gongdel.promptserver.adapter.in.rest.response.dashboard.WeeklyViewStatisticsResponse;
import com.gongdel.promptserver.application.port.in.ViewStatisticsQueryUseCase;
import com.gongdel.promptserver.domain.statistics.ComparisonPeriod;
import com.gongdel.promptserver.domain.view.ViewStatistics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * 대시보드에서 사용하는 조회수 통계 API 컨트롤러입니다.
 * 주간, 월간 등 다양한 기간의 조회수 비교 통계를 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard/view-statistics")
@RequiredArgsConstructor
@Tag(name = "대시보드 조회수 통계", description = "대시보드 조회수 통계 API")
public class DashboardViewStatisticsController {

    private final ViewStatisticsQueryUseCase viewStatisticsQueryUseCase;

    /**
     * 이번주 vs 지난주 조회수 비교 통계를 조회합니다.
     * 대시보드에서 "3,891 조회수 (+18%)" 형태로 표시할 데이터를 제공합니다.
     *
     * @return 주간 조회수 통계 응답
     */
    @GetMapping("/weekly")
    @Operation(summary = "주간 조회수 통계 조회", description = "이번주와 지난주 조회수를 비교하여 증감률과 함께 반환합니다. 대시보드 통계 카드에서 사용됩니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "주간 조회수 통계 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류 - 통계 데이터 조회 실패")
    })
    public ResponseEntity<WeeklyViewStatisticsResponse> getWeeklyViewStatistics() {
        log.info("Request weekly view statistics for dashboard");

        // 이번주 기간 계산 (월요일~일요일)
        LocalDate today = LocalDate.now();
        LocalDate thisWeekStart = today.with(DayOfWeek.MONDAY);
        LocalDate thisWeekEnd = today.with(DayOfWeek.SUNDAY);

        ComparisonPeriod weeklyPeriod = new ComparisonPeriod(
            thisWeekStart.atStartOfDay(),
            thisWeekEnd.atTime(23, 59, 59));

        // 범용 통계 서비스 사용
        ViewStatistics statistics = viewStatisticsQueryUseCase.getViewStatistics(weeklyPeriod);

        // 주간 응답 DTO로 변환
        WeeklyViewStatisticsResponse response = WeeklyViewStatisticsResponse.builder()
            .thisWeekViewCount(statistics.getCurrentPeriodViewCount())
            .lastWeekViewCount(statistics.getPreviousPeriodViewCount())
            .changeCount(statistics.getChangeCount())
            .changeRate(statistics.getChangeRate())
            .weekStartDate(thisWeekStart)
            .weekEndDate(thisWeekEnd)
            .build();

        log.info("Weekly view statistics loaded: thisWeek={}, changeRate={}%",
            response.getThisWeekViewCount(), response.getChangeRate());

        return ResponseEntity.ok(response);
    }
}
