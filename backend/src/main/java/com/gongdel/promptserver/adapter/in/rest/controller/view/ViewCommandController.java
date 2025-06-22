package com.gongdel.promptserver.adapter.in.rest.controller.view;

import com.gongdel.promptserver.adapter.in.rest.request.view.RecordViewRequest;
import com.gongdel.promptserver.adapter.in.rest.response.view.ViewResponse;
import com.gongdel.promptserver.adapter.in.rest.util.ClientIpExtractor;
import com.gongdel.promptserver.application.port.in.ViewCommandUseCase;
import com.gongdel.promptserver.application.port.in.command.view.RecordViewCommand;
import com.gongdel.promptserver.common.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 프롬프트 조회수 기록 관련 REST 컨트롤러입니다.
 * 로그인/비로그인 사용자 모두 지원합니다.
 */
@Slf4j
@Tag(name = "프롬프트 조회수 - Command", description = "프롬프트 조회수 기록 API")
@RestController
@RequestMapping("/api/v1/prompts")
@RequiredArgsConstructor
public class ViewCommandController {

    private final ViewCommandUseCase viewCommandUseCase;
    private final CurrentUserProvider currentUserProvider;

    /**
     * 프롬프트 조회수를 기록합니다.
     * 로그인 사용자는 자동으로 인식되며, 비로그인 사용자는 익명 ID를 제공할 수 있습니다.
     *
     * @param promptId    프롬프트 UUID
     * @param request     조회 기록 요청 (익명 ID 포함 가능)
     * @param httpRequest HTTP 요청 (IP 추출용)
     * @return 조회수 기록 결과
     */
    @Operation(
        summary = "프롬프트 조회수 기록",
        description = "프롬프트 조회를 기록하고 총 조회수를 반환합니다. " +
            "로그인한 사용자의 경우 Authorization 헤더에 Bearer 토큰을 포함해주세요. " +
            "비로그인 사용자는 토큰 없이 요청 가능하며, 익명 ID를 요청 본문에 포함할 수 있습니다.",
        security = {
            @SecurityRequirement(name = "bearerAuth")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회수 기록 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 토큰이 유효하지 않음 (로그인 사용자만 해당)"),
        @ApiResponse(responseCode = "404", description = "프롬프트를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/{promptId}/view")
    public ResponseEntity<ViewResponse> recordView(
        @Parameter(description = "프롬프트 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
        @PathVariable UUID promptId,
        @Parameter(
            name = "X-Forwarded-For",
            description = "클라이언트의 실제 IP 주소 (프록시 환경에서 사용)",
            example = "203.0.113.195",
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string"),
            required = false
        )
        @RequestHeader(value = "X-Forwarded-For", required = false) String xForwardedFor,

        @Parameter(description = "조회 기록 요청 (익명 ID 포함 가능, 비로그인 사용자 전용)")
        @RequestBody(required = false) RecordViewRequest request,

        HttpServletRequest httpRequest) {

        Assert.notNull(promptId, "Prompt ID cannot be null");

        // 클라이언트 IP 추출
        String clientIp = ClientIpExtractor.getClientIpAddress(httpRequest);
        log.debug("Recording view for prompt: {}, clientIp: {}", promptId, clientIp);

        // 현재 사용자 정보 확인 (로그인 여부)
        Long currentUserId = getCurrentUserIdSafely();

        // 커맨드 생성
        RecordViewCommand command = createRecordViewCommand(promptId, request, currentUserId, clientIp);

        // 조회수 기록 실행
        long totalViewCount = viewCommandUseCase.recordView(command);

        // 응답 생성 (새로운 조회인지는 서비스에서 판단하기 어려우므로 true로 설정)
        ViewResponse response = ViewResponse.success(totalViewCount, true);

        log.info("View recorded successfully: promptId={}, userId={}, totalCount={}",
            promptId, currentUserId, totalViewCount);

        return ResponseEntity.ok(response);
    }

    /**
     * 현재 사용자 ID를 안전하게 조회합니다.
     * 비로그인 상태에서는 null을 반환합니다.
     *
     * @return 사용자 ID (비로그인 시 null)
     */
    private Long getCurrentUserIdSafely() {
        try {
            return currentUserProvider.getCurrentUserId();
        } catch (Exception e) {
            log.debug("User not logged in: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 조회 기록 커맨드를 생성합니다.
     *
     * @param promptId      프롬프트 UUID
     * @param request       요청 객체
     * @param currentUserId 현재 사용자 ID
     * @param clientIp      클라이언트 IP
     * @return 조회 기록 커맨드
     */
    private RecordViewCommand createRecordViewCommand(UUID promptId, RecordViewRequest request,
                                                      Long currentUserId, String clientIp) {
        if (currentUserId != null) {
            // 로그인 사용자
            return RecordViewCommand.forUser(promptId, currentUserId, clientIp);
        } else {
            // 비로그인 사용자
            String anonymousId = (request != null) ? request.getAnonymousId() : null;
            return RecordViewCommand.forGuest(promptId, clientIp, anonymousId);
        }
    }
}
