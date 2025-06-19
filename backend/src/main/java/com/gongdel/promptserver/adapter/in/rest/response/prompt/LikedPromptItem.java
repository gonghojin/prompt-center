package com.gongdel.promptserver.adapter.in.rest.response.prompt;

import com.gongdel.promptserver.domain.like.LikedPromptResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 좋아요한 프롬프트 아이템 응답 DTO입니다.
 */
@Getter
@Builder
@Schema(description = "좋아요한 프롬프트 아이템 응답 DTO")
public class LikedPromptItem {
    @Schema(description = "프롬프트 ID", example = "123")
    private final Long promptId;
    @Schema(description = "프롬프트 제목", example = "AI 추천 프롬프트")
    private final String title;
    @Schema(description = "프롬프트 설명", example = "이미지 생성에 최적화된 프롬프트")
    private final String description;

    /**
     * 도메인 객체로부터 응답 DTO를 생성합니다.
     *
     * @param result LikedPromptResult
     * @return LikedPromptItem
     */
    public static LikedPromptItem from(LikedPromptResult result) {
        return LikedPromptItem.builder()
            .promptId(result.getId())
            .title(result.getTitle())
            .description(result.getDescription())
            .build();
    }
}
