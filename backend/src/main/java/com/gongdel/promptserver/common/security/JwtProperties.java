package com.gongdel.promptserver.common.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * JWT 관련 설정 프로퍼티를 담는 클래스입니다.
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    /**
     * JWT 서명에 사용할 시크릿 키
     */
    @NotBlank(message = "JWT secret key must not be blank")
    private String secret;

    /**
     * 액세스 토큰 유효기간 (밀리초)
     */
    @Positive(message = "Access token validity must be positive")
    private long accessTokenValidityInMs;

    /**
     * 리프레시 토큰 유효기간 (밀리초)
     */
    @Positive(message = "Refresh token validity must be positive")
    private long refreshTokenValidityInMs;
}
