package com.gongdel.promptserver.adapter.in.rest.controller.dashboard;

import com.gongdel.promptserver.adapter.in.rest.response.dashboard.CategoryStatisticsResponse;
import com.gongdel.promptserver.application.port.in.CategoryStatisticsQueryUseCase;
import com.gongdel.promptserver.domain.model.statistics.CategoryPromptCount;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 대시보드에서 카테고리별 프롬프트 통계를 제공하는 컨트롤러입니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard/categories")
@RequiredArgsConstructor
@Tag(name = "대시보드 카테고리 통계", description = "대시보드용 카테고리별 통계 API")
public class CategoryStatisticsController {

    private final CategoryStatisticsQueryUseCase categoryStatisticsQueryUseCase;

    /**
     * 루트 카테고리별 프롬프트 현황을 조회합니다.
     *
     * @return 루트 카테고리별 프롬프트 통계 응답
     */
    @GetMapping("/root/statistics")
    @Operation(summary = "루트 카테고리별 프롬프트 현황", description = "루트 카테고리별 프롬프트 개수를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "루트 카테고리별 프롬프트 통계 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<CategoryStatisticsResponse> getRootCategoryStatistics() {
        log.info("Request root category statistics for dashboard");
        List<CategoryPromptCount> counts = categoryStatisticsQueryUseCase.getRootCategoryPromptCounts();
        log.debug("Fetched {} root category statistics", counts.size());
        return ResponseEntity.ok(mapToResponse(counts));
    }

    /**
     * 특정 루트의 하위 카테고리별 프롬프트 현황을 조회합니다.
     *
     * @param rootId 루트 카테고리 ID (필수)
     * @return 하위 카테고리별 프롬프트 통계 응답
     * @throws IllegalArgumentException rootId가 null인 경우 발생
     */
    @GetMapping("/{rootId}/children/statistics")
    @Operation(summary = "하위 카테고리별 프롬프트 현황", description = "특정 루트의 하위 카테고리별 프롬프트 개수를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "하위 카테고리별 프롬프트 통계 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 파라미터(rootId null 등)"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<CategoryStatisticsResponse> getChildCategoryStatistics(@PathVariable Long rootId) {
        Assert.notNull(rootId, "rootId must not be null");
        log.info("Request child category statistics for rootId: {}", rootId);
        List<CategoryPromptCount> counts = categoryStatisticsQueryUseCase.getChildCategoryPromptCounts(rootId);
        log.debug("Fetched {} child category statistics for rootId: {}", counts.size(), rootId);
        return ResponseEntity.ok(mapToResponse(counts));
    }

    /**
     * CategoryPromptCount 리스트를 CategoryStatisticsResponse로 변환합니다.
     *
     * @param counts 카테고리별 프롬프트 개수 목록
     * @return 카테고리 통계 응답 객체
     */
    private CategoryStatisticsResponse mapToResponse(List<CategoryPromptCount> counts) {
        return CategoryStatisticsResponse.builder()
            .categories(
                counts.stream()
                    .map(c -> CategoryStatisticsResponse.CategoryStat.builder()
                        .categoryId(c.getCategoryId())
                        .categoryName(c.getCategoryName())
                        .promptCount(c.getPromptCount())
                        .build())
                    .toList())
            .build();
    }
}
