package com.gongdel.promptserver.adapter.in.rest;

import com.gongdel.promptserver.adapter.in.rest.response.PromptResponse;
import com.gongdel.promptserver.application.port.in.CategoryQueryUseCase;
import com.gongdel.promptserver.application.port.in.PromptsQueryUseCase;
import com.gongdel.promptserver.domain.model.Category;
import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.model.PromptTemplate;
import com.gongdel.promptserver.domain.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    private final CategoryQueryUseCase categoryQueryUseCase;

    /**
     * ID로 프롬프트를 조회합니다.
     *
     * @param id 프롬프트 ID
     * @return 프롬프트 정보 또는 404 응답
     */
    @Operation(summary = "프롬프트 단건 조회", description = "ID로 프롬프트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프롬프트 조회 성공"),
            @ApiResponse(responseCode = "404", description = "프롬프트 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PromptResponse> getPrompt(
            @Parameter(description = "프롬프트 ID", example = "1") @PathVariable UUID id) {
        log.info("Retrieving prompt with id: {}", id);
        Optional<PromptTemplate> prompt = getPromptsUseCase.loadPromptByUuid(id);
        return prompt
                .map(p -> ResponseEntity.ok(PromptResponse.from(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 모든 프롬프트 목록을 페이지네이션하여 조회합니다.
     *
     * @param pageable 페이지네이션 정보
     * @return 프롬프트 목록 페이지
     */
    @Operation(summary = "프롬프트 전체 목록 조회", description = "모든 프롬프트 목록을 페이지네이션하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프롬프트 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<PromptResponse>> getAllPrompts(Pageable pageable) {
        log.info("Retrieving all prompts (paged)");
        Page<PromptTemplate> promptsPage = getPromptsUseCase.findAllPrompts(pageable);
        List<PromptResponse> response = promptsPage.getContent().stream()
                .map(PromptResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * 공개된 프롬프트 목록을 페이지네이션하여 조회합니다.
     *
     * @param pageable 페이지네이션 정보
     * @return 공개 프롬프트 목록 페이지
     */
    @Operation(summary = "공개 프롬프트 목록 조회", description = "공개된 프롬프트 목록을 페이지네이션하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공개 프롬프트 목록 조회 성공")
    })
    @GetMapping("/public")
    public ResponseEntity<List<PromptResponse>> getPublicPrompts(Pageable pageable) {
        log.info("Retrieving public prompts (paged)");
        Page<PromptTemplate> promptsPage = getPromptsUseCase
                .findPromptsByVisibilityAndStatus(
                        com.gongdel.promptserver.domain.model.Visibility.PUBLIC,
                        PromptStatus.PUBLISHED,
                        pageable);
        List<PromptResponse> response = promptsPage.getContent().stream()
                .map(PromptResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 작성자의 프롬프트 목록을 페이지네이션하여 조회합니다.
     *
     * @param authorId 작성자 ID
     * @param pageable 페이지네이션 정보
     * @return 작성자의 프롬프트 목록 페이지
     */
    @Operation(summary = "작성자별 프롬프트 목록 조회", description = "특정 작성자의 프롬프트 목록을 페이지네이션하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "작성자별 프롬프트 목록 조회 성공")
    })
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<PromptResponse>> getPromptsByAuthor(
            @Parameter(description = "작성자 ID", example = "1") @PathVariable UUID authorId,
            Pageable pageable) {
        log.info("Retrieving prompts by author: {} (paged)", authorId);
        User user = User.builder().id(authorId).build();
        Page<PromptTemplate> promptsPage = getPromptsUseCase
                .findPromptsByCreatedByAndStatus(
                        user,
                        PromptStatus.PUBLISHED,
                        pageable);
        List<PromptResponse> response = promptsPage.getContent().stream()
                .map(PromptResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * 키워드로 프롬프트를 검색합니다.
     *
     * @param keyword  검색 키워드
     * @param pageable 페이지네이션 정보
     * @param status   프롬프트 상태 (기본값: PUBLISHED)
     * @return 검색 결과 프롬프트 목록
     */
    @Operation(summary = "프롬프트 키워드 검색", description = "키워드로 프롬프트를 검색합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프롬프트 검색 성공")
    })
    @GetMapping("/search")
    public ResponseEntity<List<PromptResponse>> searchPrompts(
            @Parameter(description = "검색 키워드", example = "AI") @RequestParam String keyword,
            Pageable pageable,
            @RequestParam(name = "status", required = false, defaultValue = "PUBLISHED") String status) {
        log.info("Searching prompts with keyword: {}", keyword);
        PromptStatus promptStatus = PromptStatus
                .valueOf(status);
        Page<PromptTemplate> promptsPage = getPromptsUseCase
                .searchPromptsByKeyword(keyword, promptStatus, pageable);
        List<PromptResponse> response = promptsPage.getContent().stream()
                .map(PromptResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * 카테고리별 프롬프트 목록을 페이지네이션하여 조회합니다.
     *
     * @param categoryId 카테고리 ID
     * @param pageable   페이지네이션 정보
     * @param status     프롬프트 상태 (기본값: PUBLISHED)
     * @return 카테고리별 프롬프트 목록 페이지
     */
    @Operation(summary = "카테고리별 프롬프트 목록 조회", description = "특정 카테고리의 프롬프트 목록을 페이지네이션하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리별 프롬프트 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리 없음")
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<PromptResponse>> getPromptsByCategory(
            @Parameter(description = "카테고리 ID", example = "1") @PathVariable Long categoryId,
            Pageable pageable,
            @RequestParam(name = "status", required = false, defaultValue = "PUBLISHED") String status) {
        log.info("Retrieving prompts by category: {} (paged)", categoryId);
        Optional<Category> categoryOpt = categoryQueryUseCase.getCategoryById(categoryId);
        if (categoryOpt.isEmpty()) {
            log.warn("Category not found with id: {}", categoryId);
            return ResponseEntity.notFound().build();
        }
        Category category = categoryOpt.get();
        PromptStatus promptStatus;
        try {
            promptStatus = PromptStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid prompt status: {}. Defaulting to PUBLISHED", status);
            promptStatus = PromptStatus.PUBLISHED;
        }
        Page<PromptTemplate> promptsPage = getPromptsUseCase
                .findPromptsByCategoryAndStatus(category, promptStatus, pageable);
        List<PromptResponse> response = promptsPage.getContent().stream()
                .map(PromptResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
