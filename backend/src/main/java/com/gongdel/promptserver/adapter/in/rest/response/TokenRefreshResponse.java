package com.gongdel.promptserver.adapter.in.rest.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 토큰 갱신 응답 DTO입니다.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "토큰 갱신 응답 DTO")
public class TokenRefreshResponse {
    @Schema(description = "새로 발급된 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "토큰 타입", example = "Bearer")
    private String tokenType;
}
