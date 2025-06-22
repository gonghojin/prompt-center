package com.gongdel.promptserver.adapter.in.rest.controller.view;

import com.gongdel.promptserver.adapter.in.rest.response.view.TopViewedPromptResponse;
import com.gongdel.promptserver.adapter.in.rest.response.view.ViewStatisticsResponse;
import com.gongdel.promptserver.application.port.in.ViewStatisticsQueryUseCase;
import com.gongdel.promptserver.application.port.in.query.view.GetViewStatisticsQuery;
import com.gongdel.promptserver.domain.model.statistics.DailyViewStatistics;
import com.gongdel.promptserver.domain.model.statistics.TopViewedPrompt;
import com.gongdel.promptserver.domain.model.statistics.ViewCountDistribution;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 프롬프트 조회수 통계 관련 REST 컨트롤러입니다.
 */
@Slf4j
@Tag(name = "프롬프트 조회수 통계", description = "프롬프트 조회수 통계 조회 API")
@RestController
@RequestMapping("/api/v1/view-statistics")
@RequiredArgsConstructor
public class ViewStatisticsController {

    private final ViewStatisticsQueryUseCase viewStatisticsQueryUseCase;

    /**
     * 인기 프롬프트 목록을 조회합니다.
     *
     * @param startDate   시작일 (선택사항)
     * @param endDate     종료일 (선택사항)
     * @param categoryIds 카테고리 ID 목록 (선택사항)
     * @param limit       조회 개수 (기본값: 10)
     * @return 인기 프롬프트 목록
     */
    @Operation(summary = "인기 프롬프트 조회", description = "조회수 기준 인기 프롬프트 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "인기 프롬프트 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/top-prompts")
    public ResponseEntity<List<TopViewedPromptResponse>> getTopViewedPrompts(
        @Parameter(description = "시작일 (yyyy-MM-dd'T'HH:mm:ss)", example = "2024-01-01T00:00:00") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @Parameter(description = "종료일 (yyyy-MM-dd'T'HH:mm:ss)", example = "2024-01-31T23:59:59") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
        @Parameter(description = "카테고리 ID 목록 (쉼표 구분)", example = "1,2,3") @RequestParam(required = false) List<Long> categoryIds,
        @Parameter(description = "조회 개수", example = "10") @RequestParam(defaultValue = "10") int limit) {

        log.debug("Getting top viewed prompts: startDate={}, endDate={}, categoryIds={}, limit={}",
            startDate, endDate, categoryIds, limit);

        // 쿼리 생성 및 실행
        GetViewStatisticsQuery query = GetViewStatisticsQuery.forTopPrompts(startDate, endDate, categoryIds,
            limit);
        List<TopViewedPrompt> topPrompts = viewStatisticsQueryUseCase.getTopViewedPrompts(query);

        // 응답 변환
        List<TopViewedPromptResponse> response = TopViewedPromptResponse.fromList(topPrompts);

        log.info("Top viewed prompts retrieved: count={}", response.size());
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 프롬프트의 일별 조회수 통계를 조회합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @param startDate        시작일 (선택사항)
     * @param endDate          종료일 (선택사항)
     * @return 일별 조회수 통계
     */
    @Operation(summary = "일별 조회수 통계 조회", description = "특정 프롬프트의 일별 조회수 통계를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "일별 통계 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/daily/{promptTemplateId}")
    public ResponseEntity<List<ViewStatisticsResponse.DailyViewStatistics>> getDailyViewStatistics(
        @Parameter(description = "프롬프트 템플릿 ID", example = "123") @PathVariable Long promptTemplateId,
        @Parameter(description = "시작일 (yyyy-MM-dd'T'HH:mm:ss)", example = "2024-01-01T00:00:00") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @Parameter(description = "종료일 (yyyy-MM-dd'T'HH:mm:ss)", example = "2024-01-31T23:59:59") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        log.debug("Getting daily view statistics: promptId={}, startDate={}, endDate={}",
            promptTemplateId, startDate, endDate);

        // 쿼리 생성 및 실행
        GetViewStatisticsQuery query = GetViewStatisticsQuery.forPeriodTotal(
            startDate != null ? startDate : LocalDateTime.now().minusDays(30),
            endDate != null ? endDate : LocalDateTime.now(),
            List.of());
        List<DailyViewStatistics> dailyStats = viewStatisticsQueryUseCase
            .getDailyViewStatistics(promptTemplateId, query);

        // 응답 변환
        List<ViewStatisticsResponse.DailyViewStatistics> response = ViewStatisticsResponse.DailyViewStatistics
            .fromList(dailyStats);

        log.info("Daily view statistics retrieved: promptId={}, count={}", promptTemplateId, response.size());
        return ResponseEntity.ok(response);
    }

    /**
     * 조회수 분포 통계를 조회합니다.
     *
     * @param categoryIds 카테고리 ID 목록 (선택사항)
     * @return 조회수 분포 통계
     */
    @Operation(summary = "조회수 분포 통계 조회", description = "전체 프롬프트들의 조회수 분포 통계를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "분포 통계 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/distribution")
    public ResponseEntity<List<ViewStatisticsResponse.ViewCountDistribution>> getViewCountDistribution(
        @Parameter(description = "카테고리 ID 목록 (쉼표 구분)", example = "1,2,3") @RequestParam(required = false) List<Long> categoryIds) {

        log.debug("Getting view count distribution: categoryIds={}", categoryIds);

        // 쿼리 생성 및 실행 (기본 구간 사용)
        GetViewStatisticsQuery query = GetViewStatisticsQuery.forDistribution(null, categoryIds);
        List<ViewCountDistribution> distribution = viewStatisticsQueryUseCase
            .getViewCountDistribution(query);

        // 응답 변환
        List<ViewStatisticsResponse.ViewCountDistribution> response = ViewStatisticsResponse.ViewCountDistribution
            .fromList(distribution);

        log.info("View count distribution retrieved: count={}", response.size());
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 기간 내 총 조회수를 조회합니다.
     *
     * @param startDate   시작일
     * @param endDate     종료일
     * @param categoryIds 카테고리 ID 목록 (선택사항)
     * @return 기간 내 총 조회수
     */
    @Operation(summary = "기간별 총 조회수 조회", description = "특정 기간 내 총 조회수를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "총 조회수 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/total")
    public ResponseEntity<Long> getTotalViewCountByPeriod(
        @Parameter(description = "시작일 (yyyy-MM-dd'T'HH:mm:ss)", example = "2024-01-01T00:00:00", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @Parameter(description = "종료일 (yyyy-MM-dd'T'HH:mm:ss)", example = "2024-01-31T23:59:59", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
        @Parameter(description = "카테고리 ID 목록 (쉼표 구분)", example = "1,2,3") @RequestParam(required = false) List<Long> categoryIds) {

        log.debug("Getting total view count by period: startDate={}, endDate={}, categoryIds={}",
            startDate, endDate, categoryIds);

        // 쿼리 생성 및 실행
        GetViewStatisticsQuery query = GetViewStatisticsQuery.forPeriodTotal(startDate, endDate, categoryIds);
        long totalViewCount = viewStatisticsQueryUseCase.getTotalViewCountByPeriod(query);

        log.info("Total view count by period retrieved: startDate={}, endDate={}, count={}",
            startDate, endDate, totalViewCount);
        return ResponseEntity.ok(totalViewCount);
    }

    /**
     * 특정 프롬프트들의 조회수를 일괄 조회합니다.
     *
     * @param promptTemplateIds 프롬프트 템플릿 ID 목록
     * @param startDate         시작일 (선택사항)
     * @param endDate           종료일 (선택사항)
     * @return 프롬프트별 조회수 목록
     */
    @Operation(summary = "특정 프롬프트들 조회수 일괄 조회", description = "여러 프롬프트의 조회수를 한 번에 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "일괄 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/batch")
    public ResponseEntity<List<TopViewedPromptResponse>> getViewCountsByPromptIds(
        @Parameter(description = "프롬프트 템플릿 ID 목록 (쉼표 구분)", example = "1,2,3", required = true) @RequestParam List<Long> promptTemplateIds,
        @Parameter(description = "시작일 (yyyy-MM-dd'T'HH:mm:ss)", example = "2024-01-01T00:00:00") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @Parameter(description = "종료일 (yyyy-MM-dd'T'HH:mm:ss)", example = "2024-01-31T23:59:59") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        log.debug("Getting view counts by prompt IDs: promptIds={}, startDate={}, endDate={}",
            promptTemplateIds, startDate, endDate);

        // 쿼리 생성 및 실행
        GetViewStatisticsQuery query = GetViewStatisticsQuery.forSpecificPrompts(promptTemplateIds, startDate,
            endDate);
        List<TopViewedPrompt> viewCounts = viewStatisticsQueryUseCase.getViewCountsByPromptIds(query);

        // 응답 변환
        List<TopViewedPromptResponse> response = TopViewedPromptResponse.fromList(viewCounts);

        log.info("View counts by prompt IDs retrieved: count={}", response.size());
        return ResponseEntity.ok(response);
    }
}
