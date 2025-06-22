package com.gongdel.promptserver.adapter.in.rest.controller.dashboard;

import com.gongdel.promptserver.adapter.in.rest.response.dashboard.FavoriteStatisticsResponse;
import com.gongdel.promptserver.application.port.in.query.FavoriteQueryUseCase;
import com.gongdel.promptserver.domain.statistics.ComparisonPeriod;
import com.gongdel.promptserver.domain.statistics.FavoriteStatistics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * 대시보드 즐겨찾기 통계 조회 컨트롤러입니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard/favorite-statistics")
@RequiredArgsConstructor
@Tag(name = "대시보드 즐겨찾기 통계", description = "대시보드용 즐겨찾기 통계 API")
public class FavoriteStatisticsController {

    private final FavoriteQueryUseCase favoriteQueryUseCase;

    /**
     * 즐겨찾기 통계 정보를 조회합니다.
     *
     * @param startDate 시작 날짜 (기본값: 7일 전)
     * @param endDate   종료 날짜 (기본값: 현재)
     * @return 즐겨찾기 통계 응답
     */
    @GetMapping
    @Operation(summary = "즐겨찾기 통계 조회", description = "대시보드용 즐겨찾기 통계를 조회합니다. 총 즐겨찾기 수, 기간별 비교, 변동률을 포함합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "즐겨찾기 통계 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 - 날짜 형식 오류"),
        @ApiResponse(responseCode = "500", description = "서버 오류 - 통계 데이터 조회 실패")
    })
    public ResponseEntity<FavoriteStatisticsResponse> getFavoriteStatistics(
        @Parameter(description = "통계 시작 날짜 (YYYY-MM-DD'T'HH:mm:ss)", example = "2024-01-01T00:00:00") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

        @Parameter(description = "통계 종료 날짜 (YYYY-MM-DD'T'HH:mm:ss)", example = "2024-01-07T23:59:59") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        log.info("Request favorite statistics: startDate={}, endDate={}", startDate, endDate);

        // 기본값 설정 (7일 전부터 현재까지)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime defaultStartDate = startDate != null ? startDate : now.minusDays(7);
        LocalDateTime defaultEndDate = endDate != null ? endDate : now;

        ComparisonPeriod period = new ComparisonPeriod(defaultStartDate, defaultEndDate);
        FavoriteStatistics statistics = favoriteQueryUseCase.getFavoriteStatistics(period);
        FavoriteStatisticsResponse response = FavoriteStatisticsResponse.from(statistics);

        log.info("Favorite statistics loaded: totalCount={}, currentCount={}, previousCount={}, percentageChange={}",
            response.getTotalCount(), response.getCurrentCount(),
            response.getPreviousCount(), response.getPercentageChange());

        return ResponseEntity.ok(response);
    }
}
