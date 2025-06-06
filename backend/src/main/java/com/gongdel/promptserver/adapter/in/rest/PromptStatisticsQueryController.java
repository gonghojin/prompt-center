package com.gongdel.promptserver.adapter.in.rest;

import com.gongdel.promptserver.adapter.in.rest.response.PromptStatisticsResponse;
import com.gongdel.promptserver.application.port.in.PromptStatisticsQueryUseCase;
import com.gongdel.promptserver.domain.statistics.ComparisonPeriod;
import com.gongdel.promptserver.domain.statistics.PromptStatistics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * 프롬프트 통계 대시보드용 API 컨트롤러입니다.
 */
@Tag(name = "프롬프트 통계", description = "프롬프트 통계 대시보드 API")
@RestController
@RequestMapping("/api/v1/dashboard/prompt-statistics")
@RequiredArgsConstructor
@Slf4j
public class PromptStatisticsQueryController {
    private final PromptStatisticsQueryUseCase promptStatisticsQueryUseCase;

    /**
     * 대시보드용 프롬프트 통계 정보를 조회합니다.
     *
     * @param startDate 시작일 (yyyy-MM-dd'T'HH:mm:ss)
     * @param endDate   종료일 (yyyy-MM-dd'T'HH:mm:ss)
     * @return 프롬프트 통계 응답
     * @throws IllegalArgumentException 잘못된 파라미터 입력 시 발생
     */
    @Operation(summary = "프롬프트 통계 조회", description = "시작일, 종료일을 기준으로 프롬프트 통계 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "프롬프트 통계 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<PromptStatisticsResponse> getPromptStatistics(
        @Parameter(description = "시작일 (yyyy-MM-dd'T'HH:mm:ss)", example = "2024-06-01T00:00:00") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @Parameter(description = "종료일 (yyyy-MM-dd'T'HH:mm:ss)", example = "2024-06-02T00:00:00") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Request prompt statistics: startDate={}, endDate={}", startDate, endDate);

        validateParameters(startDate, endDate);

        ComparisonPeriod period = new ComparisonPeriod(startDate, endDate);
        PromptStatistics stats = promptStatisticsQueryUseCase.getPromptStatistics(period);
        log.info("Prompt statistics successfully retrieved for startDate={}, endDate={}", startDate, endDate);
        PromptStatisticsResponse response = PromptStatisticsResponse.from(stats);
        return ResponseEntity.ok(response);
    }

    /**
     * 파라미터 유효성 검증 메서드
     *
     * @param startDate 시작일
     * @param endDate   종료일
     * @throws IllegalArgumentException 잘못된 파라미터 입력 시 발생
     */
    private void validateParameters(LocalDateTime startDate, LocalDateTime endDate) {
        Assert.notNull(startDate, "StartDate must not be null");
        Assert.notNull(endDate, "EndDate must not be null");
        if (endDate.isBefore(startDate)) {
            log.error("End date {} is before start date {}", endDate, startDate);
            throw new IllegalArgumentException("End date must not be before start date");
        }
    }
}
