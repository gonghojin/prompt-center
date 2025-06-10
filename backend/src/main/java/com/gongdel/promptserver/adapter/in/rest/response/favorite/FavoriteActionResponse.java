package com.gongdel.promptserver.adapter.in.rest.response.favorite;

import com.gongdel.promptserver.domain.model.favorite.Favorite;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 즐겨찾기 추가 응답 DTO입니다.
 */
@Getter
@Builder
@Schema(description = "즐겨찾기 추가 응답 DTO")
public class FavoriteActionResponse {
    @Schema(description = "즐겨찾기 고유 식별자", example = "1")
    private final Long id;
    @Schema(description = "프롬프트 템플릿 ID", example = "42")
    private final Long promptTemplateId;
    @Schema(description = "즐겨찾기 생성일시", example = "2024-03-21T10:00:00Z")
    private final LocalDateTime createdAt;

    /**
     * 도메인 객체(Favorite)로부터 응답 DTO를 생성합니다.
     *
     * @param favorite 즐겨찾기 도메인 객체
     * @return FavoriteActionResponse
     */
    public static FavoriteActionResponse from(Favorite favorite) {
        return FavoriteActionResponse.builder()
            .id(favorite.getId())
            .promptTemplateId(favorite.getPromptTemplateId())
            .createdAt(favorite.getCreatedAt())
            .build();
    }
}
