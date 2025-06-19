package com.gongdel.promptserver.adapter.in.rest.controller.prompt;

import com.gongdel.promptserver.adapter.in.rest.response.PageResponse;
import com.gongdel.promptserver.adapter.in.rest.response.favorite.FavoriteCountResponse;
import com.gongdel.promptserver.adapter.in.rest.response.favorite.FavoritePromptResponse;
import com.gongdel.promptserver.adapter.in.rest.response.prompt.MyPromptLikeStatisticsResponse;
import com.gongdel.promptserver.adapter.in.rest.response.prompt.MyPromptListResponse;
import com.gongdel.promptserver.adapter.in.rest.response.prompt.MyPromptStatisticsResponse;
import com.gongdel.promptserver.application.port.in.MyPromptsQueryUseCase;
import com.gongdel.promptserver.application.port.in.query.FavoriteQueryUseCase;
import com.gongdel.promptserver.common.security.CurrentUserProvider;
import com.gongdel.promptserver.domain.model.PromptSortType;
import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.model.Visibility;
import com.gongdel.promptserver.domain.model.favorite.FavoritePromptResult;
import com.gongdel.promptserver.domain.model.favorite.FavoriteSearchCondition;
import com.gongdel.promptserver.domain.model.my.MyPromptSearchCondition;
import com.gongdel.promptserver.domain.model.statistics.PromptStatisticsResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * 내 프롬프트 목록/통계 조회 API 컨트롤러입니다.
 */
@Slf4j
@Tag(name = "내 프롬프트 조회", description = "내 프롬프트 목록/통계 API")
@RestController
@RequestMapping("/api/v1/prompts/my")
@RequiredArgsConstructor
public class MyPromptQueryController {
    private final MyPromptsQueryUseCase myPromptsQueryUseCase;
    private final FavoriteQueryUseCase favoriteQueryUseCase;
    private final CurrentUserProvider currentUserProvider;

    /**
     * 내 프롬프트 목록을 조회합니다.
     *
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 내 프롬프트 목록 페이지
     */
    @Operation(summary = "내 프롬프트 목록 조회", description = "내가 생성한 프롬프트 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "프롬프트 목록 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<PageResponse<MyPromptListResponse>> getMyPrompts(
        @Parameter(description = "상태 필터", required = false) @RequestParam(required = false) Set<PromptStatus> statusFilters,
        @Parameter(description = "공개 범위 필터", required = false) @RequestParam(required = false) Set<Visibility> visibilityFilters,
        @Parameter(description = "검색어", required = false) @RequestParam(required = false) String searchKeyword,
        @Parameter(description = "정렬 타입", required = false) @RequestParam(required = false) PromptSortType sortType,
        @Parameter(description = "페이지 번호", required = false, example = "0") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "페이지 크기", required = false, example = "20") @RequestParam(defaultValue = "20") int size) {
        org.springframework.util.Assert.isTrue(page >= 0, "Page index must not be negative");
        org.springframework.util.Assert.isTrue(size > 0, "Page size must be greater than zero");
        Long userId = currentUserProvider.getCurrentUser().getId();
        log.info(
            "Request my prompts: userId={}, page={}, size={}, statusFilters={}, visibilityFilters={}, searchKeyword={}, sortType={}",
            userId, page, size, statusFilters, visibilityFilters, searchKeyword, sortType);
        Pageable pageable = PageRequest.of(page, size);
        MyPromptSearchCondition condition = MyPromptSearchCondition.builder()
            .userId(userId)
            .statusFilters(statusFilters)
            .visibilityFilters(visibilityFilters)
            .searchKeyword(searchKeyword)
            .sortType(sortType)
            .pageable(pageable)
            .build();
        Page<MyPromptListResponse> result = myPromptsQueryUseCase.findMyPrompts(condition)
            .map(MyPromptListResponse::from);
        log.info("My prompts successfully retrieved for userId={}, page={}, size={}", userId, page, size);
        return ResponseEntity.ok(PageResponse.from(result));
    }

        /**
         * 내 프롬프트 통계를 조회합니다.
         *
         * @return 내 프롬프트 상태별 통계
         */
        @Operation(summary = "내 프롬프트 통계 조회", description = "내가 생성한 프롬프트의 상태별 통계를 조회합니다.")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프롬프트 통계 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
        })
        @GetMapping("/statistics")
        public ResponseEntity<MyPromptStatisticsResponse> getMyPromptStatistics() {
            Long userId = currentUserProvider.getCurrentUserId();
            log.info("Request my prompt statistics: userId={}", userId);
            PromptStatisticsResult result = myPromptsQueryUseCase.getMyPromptStatistics(userId);
            log.info("My prompt statistics successfully retrieved for userId={}", userId);
            return ResponseEntity.ok(MyPromptStatisticsResponse.from(result));
        }

    /**
     * 내 즐겨찾기 프롬프트 목록을 조회합니다.
     *
     * @param page          페이지 번호 (0부터 시작)
     * @param size          페이지 크기
     * @param sort          정렬 기준 (createdAt, title)
     * @param order         정렬 순서 (asc, desc)
     * @param searchKeyword 통합 검색어 (제목, 설명, 태그 등)
     * @return 내 즐겨찾기 프롬프트 목록 페이지
     */
    @Operation(summary = "내 즐겨찾기 프롬프트 목록 조회", description = "내가 즐겨찾기한 프롬프트 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "즐겨찾기 목록 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/favorites")
    public ResponseEntity<PageResponse<FavoritePromptResponse>> getMyFavoritePrompts(
        @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size,
        @Parameter(description = "정렬 기준 (createdAt, title)", example = "createdAt") @RequestParam(defaultValue = "createdAt") String sort,
        @Parameter(description = "정렬 순서 (asc, desc)", example = "desc") @RequestParam(defaultValue = "desc") String order,
        @Parameter(description = "검색어", required = false) @RequestParam(required = false) String searchKeyword) {
        org.springframework.util.Assert.isTrue(page >= 0, "Page index must not be negative");
        org.springframework.util.Assert.isTrue(size > 0, "Page size must be greater than zero");
        Long userId = currentUserProvider.getCurrentUserId();
        log.info("Request my favorite prompts: userId={}, page={}, size={}, sort={}, order={}, searchKeyword={}",
            userId, page, size, sort, order, searchKeyword);
        Pageable pageable = PageRequest.of(page, size);
        String sortType = sort + "," + order;
        FavoriteSearchCondition condition = FavoriteSearchCondition.builder()
            .userId(userId)
            .searchKeyword(searchKeyword)
            .sortType(sortType)
            .pageable(pageable)
            .build();
        Page<FavoritePromptResult> resultPage = favoriteQueryUseCase.searchFavorites(condition);
        Page<FavoritePromptResponse> result = resultPage.map(FavoritePromptResponse::from);
        log.info("My favorite prompts successfully retrieved for userId={}, page={}, size={}", userId, page,
            size);
        return ResponseEntity.ok(PageResponse.from(result));
    }

    /**
     * 내 즐겨찾기 프롬프트 개수를 조회합니다.
     *
     * @return 내 즐겨찾기 수 응답 객체
     */
    @Operation(summary = "내 즐겨찾기 프롬프트 개수 조회", description = "내가 즐겨찾기한 프롬프트의 개수를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "내 즐겨찾기 개수 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/favorites/count")
    public ResponseEntity<FavoriteCountResponse> getMyFavoriteCount() {
        Long userId = currentUserProvider.getCurrentUserId();
        log.info("Request my favorite count: userId={}", userId);
        long count = favoriteQueryUseCase.countByUser(userId);
        log.info("My favorite count for userId={}: {}", userId, count);
        return ResponseEntity.ok(FavoriteCountResponse.of(count));
    }

    /**
     * 내가 생성한 프롬프트의 총 좋아요 수를 조회합니다.
     *
     * @return 내 프롬프트 총 좋아요 수
     * @throws IllegalArgumentException 사용자 ID가 null인 경우
     */
    @Operation(summary = "내 프롬프트 총 좋아요 수 조회", description = "내가 생성한 프롬프트의 총 좋아요 수를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "총 좋아요 수 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/like-statistics")
    public ResponseEntity<MyPromptLikeStatisticsResponse> getMyPromptLikeStatistics() {
        Long userId = currentUserProvider.getCurrentUserId();
        log.info("Request my prompt total like count: userId={}", userId);
        long totalLikeCount = myPromptsQueryUseCase.getMyTotalLikeCount(userId);
        log.info("Successfully retrieved total like count for userId={}, totalLikeCount={}", userId,
            totalLikeCount);
        return ResponseEntity.ok(MyPromptLikeStatisticsResponse.of(totalLikeCount));

        }
}
