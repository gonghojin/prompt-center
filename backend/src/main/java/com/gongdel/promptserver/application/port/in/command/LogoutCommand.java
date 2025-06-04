package com.gongdel.promptserver.application.port.in.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

/**
 * 로그아웃 처리를 위한 커맨드 객체
 *
 */
@Getter
@Builder
public class LogoutCommand {

    @NotBlank(message = "액세스 토큰은 필수입니다")
    private final String accessToken;
}
