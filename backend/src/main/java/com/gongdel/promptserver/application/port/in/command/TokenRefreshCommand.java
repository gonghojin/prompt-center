package com.gongdel.promptserver.application.port.in.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

/**
 * 토큰 갱신 요청을 처리하기 위한 커맨드 객체입니다.
 * 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받기 위한 정보를 전달합니다.
 */
@Getter
@Builder
public class TokenRefreshCommand {
    /**
     * 리프레시 토큰
     * JWT 형식의 토큰으로, 새로운 액세스 토큰 발급에 사용됩니다.
     */
    @NotBlank(message = "리프레시 토큰은 필수입니다")
    private final String refreshToken;

}
