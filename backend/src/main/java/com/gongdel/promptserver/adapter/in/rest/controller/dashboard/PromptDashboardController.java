package com.gongdel.promptserver.adapter.in.rest.controller.dashboard;

import com.gongdel.promptserver.adapter.in.rest.response.prompt.PromptListResponse;
import com.gongdel.promptserver.application.port.in.PromptDashboardQueryUseCase;
import com.gongdel.promptserver.domain.model.PromptSearchResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 대시보드에서 최근 프롬프트 목록을 제공하는 컨트롤러입니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard/prompts")
@RequiredArgsConstructor
@Tag(name = "대시보드 프롬프트", description = "대시보드 최근 프롬프트 API")
public class PromptDashboardController {

    private final PromptDashboardQueryUseCase promptDashboardQueryUseCase;

    /**
     * 최근 프롬프트를 조회합니다.
     *
     * @param pageSize 조회할 프롬프트 개수 (기본값: 4)
     * @return 최근 프롬프트 목록
     */
    @GetMapping("/recent")
    @Operation(summary = "최근 프롬프트 조회", description = "대시보드에서 최근 N개의 프롬프트를 최신순으로 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "최근 프롬프트 목록 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 파라미터(pageSize <= 0)"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<List<PromptListResponse>> getRecentPrompts(
        @Parameter(description = "조회할 프롬프트 개수", required = false, example = "4") @RequestParam(name = "pageSize", required = false, defaultValue = "4") Integer pageSize) {
        if (pageSize <= 0) {
            log.warn("Invalid pageSize: {}. Set to default value 4.", pageSize);
            pageSize = 4;
        }
        log.info("Request recent prompts for dashboard, pageSize={}", pageSize);
        List<PromptSearchResult> results = promptDashboardQueryUseCase.getRecentPrompts(pageSize);
        List<PromptListResponse> response = results.stream()
            .map(PromptListResponse::from)
            .toList();
        log.debug("Fetched {} recent prompts for dashboard", response.size());
        return ResponseEntity.ok(response);
    }
}
