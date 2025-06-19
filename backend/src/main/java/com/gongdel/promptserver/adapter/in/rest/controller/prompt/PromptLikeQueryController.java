package com.gongdel.promptserver.adapter.in.rest.controller.prompt;

import com.gongdel.promptserver.adapter.in.rest.response.PageResponse;
import com.gongdel.promptserver.adapter.in.rest.response.prompt.LikedPromptItem;
import com.gongdel.promptserver.application.port.in.query.like.PromptLikeQueryUseCase;
import com.gongdel.promptserver.application.port.out.like.query.FindLikedPrompts;
import com.gongdel.promptserver.application.port.out.like.query.LoadPromptLikeStatus;
import com.gongdel.promptserver.common.security.CurrentUserProvider;
import com.gongdel.promptserver.domain.like.LikeStatus;
import com.gongdel.promptserver.domain.like.LikedPromptResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "프롬프트 좋아요 - Query", description = "프롬프트 좋아요 조회 API")
@RestController
@RequestMapping("/api/v1/prompts")
@RequiredArgsConstructor
public class PromptLikeQueryController {
    private final PromptLikeQueryUseCase promptLikeQueryUseCase;
    private final CurrentUserProvider currentUserProvider;

    @Operation(summary = "프롬프트 좋아요 상태/카운트 조회", description = "프롬프트에 대한 내 좋아요 여부 및 전체 좋아요 수를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "좋아요 상태/카운트 조회 성공")
    })
    @GetMapping("/{id}/like-status")
    public ResponseEntity<LikeStatus> getLikeStatus(
        @Parameter(description = "프롬프트 ID", example = "1") @PathVariable Long id) {
        Long userId = currentUserProvider.getCurrentUserId();
        Assert.notNull(userId, "User ID must not be null");
        log.info("Querying like status. userId={}, promptTemplateId={}", userId, id);

        LikeStatus status = promptLikeQueryUseCase.getLikeStatus(new LoadPromptLikeStatus(userId, id));
        return ResponseEntity.ok(status);
    }

    @Operation(summary = "내가 좋아요한 프롬프트 목록 조회", description = "내가 좋아요한 프롬프트 목록을 페이징 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "좋아요한 프롬프트 목록 조회 성공")
    })
    @GetMapping("/liked")
    public ResponseEntity<PageResponse<LikedPromptItem>> getLikedPrompts(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int size) {
        Long userId = currentUserProvider.getCurrentUserId();
        Assert.notNull(userId, "User ID must not be null");
        log.info("Querying liked prompts. userId={}, page={}, size={}", userId, page, size);

        Page<LikedPromptResult> result = promptLikeQueryUseCase.getLikedPrompts(
            new FindLikedPrompts(userId, PageRequest.of(page - 1, size)));
        Page<LikedPromptItem> mapped = result.map(LikedPromptItem::from);
        return ResponseEntity.ok(PageResponse.from(mapped));
    }
}
