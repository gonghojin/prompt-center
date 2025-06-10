package com.gongdel.promptserver.adapter.in.rest.controller.prompt;

import com.gongdel.promptserver.adapter.in.rest.response.favorite.FavoriteActionResponse;
import com.gongdel.promptserver.application.port.in.command.FavoriteCommandUseCase;
import com.gongdel.promptserver.common.security.CurrentUserProvider;
import com.gongdel.promptserver.domain.model.favorite.Favorite;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 즐겨찾기 추가/삭제 API 컨트롤러입니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/prompts")
@RequiredArgsConstructor
public class FavoriteCommandController {
    private final FavoriteCommandUseCase favoriteCommandUseCase;
    private final CurrentUserProvider currentUserProvider;

    /**
     * 프롬프트를 즐겨찾기에 추가합니다.
     *
     * @param id 프롬프트 UUID
     * @return 즐겨찾기 추가 응답
     * @throws IllegalArgumentException UUID가 null인 경우
     */
    @Operation(summary = "프롬프트 즐겨찾기 추가", description = "프롬프트를 즐겨찾기에 추가합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "즐겨찾기 추가 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "404", description = "프롬프트를 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "이미 즐겨찾기한 프롬프트"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/{id}/favorite")
    public ResponseEntity<FavoriteActionResponse> addFavorite(
        @Parameter(description = "프롬프트 UUID", required = true) @PathVariable("id") UUID id) {
        Assert.notNull(id, "Prompt ID cannot be null");
        Long userId = currentUserProvider.getCurrentUserId();
        log.info("Request add favorite: userId={}, promptUuid={}", userId, id);
        Favorite favorite = favoriteCommandUseCase.addFavorite(userId, id);
        log.info("Favorite added successfully: userId={}, promptUuid={}", userId, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(FavoriteActionResponse.from(favorite));

    }

    /**
     * 프롬프트 즐겨찾기를 삭제합니다.
     *
     * @param id 프롬프트 UUID
     * @throws IllegalArgumentException UUID가 null인 경우
     * @throws IllegalStateException    인증 정보가 없을 경우
     */
    @Operation(summary = "프롬프트 즐겨찾기 삭제", description = "프롬프트 즐겨찾기를 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "즐겨찾기 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "404", description = "즐겨찾기를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<Void> removeFavorite(
        @Parameter(description = "프롬프트 UUID", required = true) @PathVariable("id") UUID id) {
        Assert.notNull(id, "Prompt ID cannot be null");
        Long userId = currentUserProvider.getCurrentUserId();
        log.info("Request remove favorite: userId={}, promptUuid={}", userId, id);

        favoriteCommandUseCase.removeFavorite(userId, id);
        log.info("Favorite removed successfully: userId={}, promptUuid={}", userId, id);
        return ResponseEntity.noContent().build();
    }
}
