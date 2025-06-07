package com.gongdel.promptserver.adapter.in.rest.controller.prompt;

import com.gongdel.promptserver.adapter.in.rest.response.PageResponse;
import com.gongdel.promptserver.adapter.in.rest.response.prompt.PromptDetailResponse;
import com.gongdel.promptserver.adapter.in.rest.response.prompt.PromptListResponse;
import com.gongdel.promptserver.application.port.in.PromptsQueryUseCase;
import com.gongdel.promptserver.domain.model.PromptSearchCondition;
import com.gongdel.promptserver.domain.model.PromptSearchResult;
import com.gongdel.promptserver.domain.model.PromptSortType;
import com.gongdel.promptserver.domain.model.PromptStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 프롬프트 조회 관련 REST API를 제공하는 컨트롤러입니다.
 */
@Slf4j
@Tag(name = "프롬프트 조회", description = "프롬프트 조회 API")
@RestController
@RequestMapping("/api/v1/prompts")
@RequiredArgsConstructor
public class PromptQueryController {

    private final PromptsQueryUseCase getPromptsUseCase;

    /**
     * ID로 프롬프트를 조회합니다.
     *
     * @param id 프롬프트 ID
     * @return 프롬프트 정보 또는 404 응답
     */
    @GetMapping("/{id}")
    @Operation(summary = "프롬프트 상세 조회", description = "UUID로 프롬프트 상세 정보를 조회합니다.")
    public ResponseEntity<PromptDetailResponse> getPrompt(
        @Parameter(description = "프롬프트 ID", example = "1") @PathVariable UUID id) {
        Assert.notNull(id, "프롬프트 ID는 null일 수 없습니다.");
        log.info("Retrieving prompt with id: {}", id);

        return getPromptsUseCase.loadPromptDetailByUuid(id)
            .map(detail -> ResponseEntity.ok(PromptDetailResponse.from(detail)))
            .orElseGet(() -> {
                log.warn("Prompt not found with id: {}", id);
                return ResponseEntity.notFound().build();
            });
    }

    /**
     * 복합 검색 조건(제목, 설명, 태그, 카테고리, 정렬 등)으로 프롬프트를 조회합니다.
     *
     * @param title       프롬프트 제목(옵션)
     * @param description 설명(옵션)
     * @param tag         태그(옵션)
     * @param categoryId  카테고리 ID(옵션)
     * @param status      프롬프트 상태(옵션, 기본값: PUBLISHED)
     * @param sortType    정렬 기준(옵션, 기본값: LATEST_MODIFIED)
     * @param pageable    페이징 정보
     * @return 프롬프트 검색 결과 페이지 (공통 페이징 포맷)
     */
    @Operation(summary = "프롬프트 복합 검색", description = "제목, 설명, 태그, 카테고리, 정렬 등 다양한 조건으로 프롬프트를 페이징 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "프롬프트 검색 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/advanced-search")
    public ResponseEntity<PageResponse<PromptListResponse>> searchPromptsAdvanced(
        @Parameter(description = "프롬프트 제목", example = "AI 추천") @RequestParam(required = false) String title,
        @Parameter(description = "프롬프트 설명", example = "이미지 생성") @RequestParam(required = false) String description,
        @Parameter(description = "태그", example = "stable-diffusion") @RequestParam(required = false) String tag,
        @Parameter(description = "카테고리 ID", example = "1") @RequestParam(required = false) Long categoryId,
        @Parameter(description = "프롬프트 상태", example = "PUBLISHED", required = false) @RequestParam(required = false, defaultValue = "PUBLISHED") String status,
        @Parameter(description = "정렬 기준 (LATEST_MODIFIED: 최근 수정순, TITLE: 프롬프트 이름순)", example = "LATEST_MODIFIED", required = false) @RequestParam(required = false, defaultValue = "LATEST_MODIFIED") PromptSortType sortType,
        Pageable pageable) {
        Assert.notNull(pageable, "Pageable 정보는 null일 수 없습니다.");

        PromptStatus promptStatus = PromptStatus.fromString(status, PromptStatus.PUBLISHED);

        log.info("Advanced search: title={}, description={}, tag={}, categoryId={}, status={}, sortType={}",
            title, description, tag, categoryId, promptStatus, sortType);

        PromptSearchCondition condition = PromptSearchCondition.builder()
            .title(title)
            .description(description)
            .tag(tag)
            .categoryId(categoryId)
            .status(promptStatus)
            .sortType(sortType)
            .pageable(pageable)
            .build();
        Page<PromptSearchResult> resultPage = getPromptsUseCase.searchPrompts(condition);

        Page<PromptListResponse> mappedPage = resultPage.map(PromptListResponse::from);
        return ResponseEntity.ok(PageResponse.from(mappedPage));
    }
}
