package com.gongdel.promptserver.adapter.in.rest.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 토큰 갱신 요청 DTO입니다.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@Schema(description = "토큰 갱신 요청 DTO")
public class TokenRefreshRequest {
    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", required = true)
    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    private String refreshToken;
}
