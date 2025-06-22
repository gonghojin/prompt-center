package com.gongdel.promptserver.adapter.in.rest.response.view;

import com.gongdel.promptserver.domain.model.statistics.TopViewedPrompt;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 인기 프롬프트 응답 DTO입니다.
 */
@Getter
@Builder
@Schema(description = "인기 프롬프트 응답 DTO")
public class TopViewedPromptResponse {

    @Schema(description = "프롬프트 템플릿 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
    private final UUID promptTemplateUuid;

    @Schema(description = "프롬프트 제목", example = "효과적인 마케팅 카피 작성 프롬프트")
    private final String title;

    @Schema(description = "총 조회수", example = "1247")
    private final long viewCount;

    @Schema(description = "작성자명", example = "홍길동")
    private final String authorName;

    @Schema(description = "카테고리명", example = "마케팅")
    private final String categoryName;

    @Schema(description = "마지막 조회 시점", example = "2024-01-15T10:30:00")
    private final LocalDateTime lastViewedAt;

    /**
     * TopViewedPromptDto로부터 응답 DTO 생성
     * Long ID를 UUID로 변환하는 로직이 필요합니다.
     */
    public static TopViewedPromptResponse from(TopViewedPrompt dto, UUID promptTemplateUuid) {
        return TopViewedPromptResponse.builder()
            .promptTemplateUuid(promptTemplateUuid)
            .title(dto.getTitle())
            .viewCount(dto.getViewCount() != null ? dto.getViewCount() : 0L)
            .authorName(dto.getAuthorName())
            .categoryName(dto.getCategoryName())
            .lastViewedAt(dto.getLastViewedAt())
            .build();
    }

    /**
     * TopViewedPromptDto로부터 응답 DTO 생성 (UUID 변환 없이)
     * 이 메서드는 서비스 계층에서 UUID 변환을 처리할 때 사용합니다.
     */
    public static TopViewedPromptResponse fromWithUuid(TopViewedPrompt dto, UUID promptTemplateUuid) {
        return from(dto, promptTemplateUuid);
    }

    /**
     * TopViewedPrompt 리스트를 응답 DTO 리스트로 변환
     * Long ID를 UUID로 변환하는 로직을 포함합니다.
     */
    public static List<TopViewedPromptResponse> fromList(List<TopViewedPrompt> dtoList) {
        return dtoList.stream()
            .map(dto -> {
                // Long ID를 기반으로 UUID 생성 (임시 방식)
                UUID tempUuid = UUID.nameUUIDFromBytes(dto.getPromptTemplateId().toString().getBytes());
                return from(dto, tempUuid);
            })
            .collect(Collectors.toList());
    }
}
