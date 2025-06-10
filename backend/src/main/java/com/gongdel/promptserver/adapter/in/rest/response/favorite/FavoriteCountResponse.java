package com.gongdel.promptserver.adapter.in.rest.response.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 즐겨찾기 개수 응답 DTO입니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteCountResponse {
    /**
     * 즐겨찾기 개수
     */
    @Schema(description = "즐겨찾기 개수", example = "5")
    private long count;

    /**
     * FavoriteCountResponse 생성 메서드
     *
     * @param count 즐겨찾기 개수
     * @return FavoriteCountResponse 객체
     */
    public static FavoriteCountResponse of(long count) {
        return FavoriteCountResponse.builder().count(count).build();
    }
}
