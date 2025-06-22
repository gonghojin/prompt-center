package com.gongdel.promptserver.adapter.in.rest.controller.view;

import com.gongdel.promptserver.adapter.in.rest.response.view.ViewCountResponse;
import com.gongdel.promptserver.application.port.in.ViewQueryUseCase;
import com.gongdel.promptserver.application.port.in.query.view.GetViewCountQuery;
import com.gongdel.promptserver.domain.view.ViewCount;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.util.Optional;
import java.util.UUID;

/**
 * 프롬프트 조회수 조회 관련 REST 컨트롤러입니다.
 */
@Slf4j
@Tag(name = "프롬프트 조회수 - Query", description = "프롬프트 조회수 조회 API")
@RestController
@RequestMapping("/api/v1/prompts")
@RequiredArgsConstructor
public class ViewQueryController {

    private final ViewQueryUseCase viewQueryUseCase;

    /**
     * 프롬프트의 조회수 정보를 조회합니다.
     *
     * @param promptId 프롬프트 UUID
     * @return 조회수 정보
     */
    @Operation(summary = "프롬프트 조회수 조회", description = "프롬프트의 상세 조회수 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회수 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "프롬프트를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{promptId}/view-count")
    public ResponseEntity<ViewCountResponse> getViewCount(
        @Parameter(description = "프롬프트 UUID", example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable UUID promptId) {

        Assert.notNull(promptId, "Prompt ID cannot be null");

        log.debug("Getting view count for prompt: {}", promptId);

        // 쿼리 생성 및 실행
        GetViewCountQuery query = GetViewCountQuery.byUuid(promptId);
        Optional<ViewCount> viewCount = viewQueryUseCase.getViewCount(query);

        // 응답 생성
        ViewCountResponse response = viewCount
            .map(vc -> ViewCountResponse.from(vc, promptId))
            .orElse(ViewCountResponse.empty(promptId));

        log.debug("View count retrieved: promptId={}, count={}",
            promptId, response.getTotalViewCount());

        return ResponseEntity.ok(response);
    }

    /**
     * 프롬프트의 총 조회수만 간단히 조회합니다.
     * 클라이언트에서 빠른 응답이 필요할 때 사용합니다.
     *
     * @param promptId 프롬프트 UUID
     * @return 총 조회수
     */
    @Operation(summary = "프롬프트 총 조회수 조회", description = "프롬프트의 총 조회수만 간단히 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "총 조회수 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "프롬프트를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{promptId}/view-count/total")
    public ResponseEntity<Long> getTotalViewCount(
        @Parameter(description = "프롬프트 UUID", example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable UUID promptId) {

        Assert.notNull(promptId, "Prompt ID cannot be null");

        log.debug("Getting total view count for prompt: {}", promptId);

        // 쿼리 생성 및 실행
        GetViewCountQuery query = GetViewCountQuery.byUuid(promptId);
        long totalViewCount = viewQueryUseCase.getTotalViewCount(query);

        log.debug("Total view count retrieved: promptId={}, count={}", promptId, totalViewCount);

        return ResponseEntity.ok(totalViewCount);
    }
}
