package com.gongdel.promptserver.adapter.in.rest.response.prompt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 프롬프트 삭제 결과를 클라이언트에 응답하기 위한 DTO 클래스입니다.
 */
@Getter
@Builder
@Schema(description = "프롬프트 삭제 결과 응답 DTO")
public class DeletePromptResponse {
    @Schema(description = "프롬프트 UUID", example = "b7e6c1a2-3f4d-4e2a-9c1a-123456789abc")
    private final UUID id;

    @Schema(description = "프롬프트 제목", example = "ChatGPT 활용법")
    private final String title;

    @Schema(description = "이전 상태", example = "PUBLISHED")
    private final String previousStatus;

    @Schema(description = "삭제 일시", example = "2024-03-21T10:00:00Z")
    private final LocalDateTime deletedAt;

    /**
     * 서비스 계층의 DeletePromptResponse로부터 컨트롤러 응답 DTO를 생성합니다.
     *
     * @param response 서비스 계층의 삭제 응답 DTO
     * @return 컨트롤러 계층의 삭제 응답 DTO
     */
    public static DeletePromptResponse from(com.gongdel.promptserver.application.dto.DeletePromptResponse response) {
        return DeletePromptResponse.builder()
            .id(response.getUuid())
            .title(response.getTitle())
            .previousStatus(response.getPreviousStatus() != null ? response.getPreviousStatus().name() : null)
            .deletedAt(response.getDeletedAt())
            .build();
    }
}
