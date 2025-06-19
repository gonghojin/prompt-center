package com.gongdel.promptserver.adapter.in.rest.controller.prompt;

import com.gongdel.promptserver.adapter.in.rest.response.prompt.LikeResponse;
import com.gongdel.promptserver.application.port.in.command.like.PromptLikeCommandUseCase;
import com.gongdel.promptserver.common.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@Tag(name = "프롬프트 좋아요 - Command", description = "프롬프트 좋아요 추가/취소 API")
@RestController
@RequestMapping("/api/v1/prompts")
@RequiredArgsConstructor
public class PromptLikeCommandController {
    private final PromptLikeCommandUseCase promptLikeCommandUseCase;
    private final CurrentUserProvider currentUserProvider;


    @Operation(summary = "프롬프트 좋아요 추가", description = "프롬프트에 좋아요를 추가합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "좋아요 추가 성공")
    })
    @PostMapping("/{id}/like")
    public ResponseEntity<LikeResponse> addLike(
        @Parameter(description = "프롬프트 UUID", example = "1") @PathVariable UUID id) {
        Assert.notNull(id, "Prompt ID cannot be null");
        Long userId = currentUserProvider.getCurrentUserId();
        Assert.notNull(userId, "User ID must not be null");
        log.info("Adding like. userId={}, promptUuid={}", userId, id);

        long likeCount = promptLikeCommandUseCase.addLike(userId, id);
        return ResponseEntity.ok(LikeResponse.of(true, likeCount));
    }

    @Operation(summary = "프롬프트 좋아요 취소", description = "프롬프트에 좋아요를 취소합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "좋아요 취소 성공")
    })
    @DeleteMapping("/{id}/like")
    public ResponseEntity<LikeResponse> removeLike(
        @Parameter(description = "프롬프트 ID", example = "1") @PathVariable UUID id) {
        Assert.notNull(id, "Prompt ID cannot be null");
        Long userId = currentUserProvider.getCurrentUserId();
        Assert.notNull(userId, "User ID must not be null");
        log.info("Removing like. userId={}, promptTemplateId={}", userId, id);

        long likeCount = promptLikeCommandUseCase.removeLike(userId, id);
        return ResponseEntity.ok(LikeResponse.of(true, likeCount));
    }
}
