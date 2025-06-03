package com.gongdel.promptserver.common.security;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * JWT 관련 설정을 담당하는 설정 클래스입니다.
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {
    private final JwtProperties jwtProperties;

    /**
     * JWT 서명에 사용할 SecretKey를 생성합니다.
     * 시크릿 키는 Base64로 디코딩된 바이트 배열을 사용하여 HMAC-SHA 키를 생성합니다.
     *
     * @return SecretKey 객체
     */
    @Bean
    public SecretKey jwtSecretKey() {
        String secretKey = jwtProperties.getSecret();
        Assert.hasText(secretKey, "JWT secret key must not be empty");

        byte[] keyBytes = Base64.getDecoder().decode(secretKey.getBytes(StandardCharsets.UTF_8));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 액세스 토큰의 유효기간을 반환합니다.
     *
     * @return 액세스 토큰 유효기간 (밀리초)
     */
    @Bean
    @Qualifier("accessTokenValidityInMs")
    public long jwtAccessTokenValidityInMs() {
        return jwtProperties.getAccessTokenValidityInMs();
    }

    /**
     * 리프레시 토큰의 유효기간을 반환합니다.
     *
     * @return 리프레시 토큰 유효기간 (밀리초)
     */
    @Bean
    @Qualifier("refreshTokenValidityInMs")
    public long jwtRefreshTokenValidityInMs() {
        return jwtProperties.getRefreshTokenValidityInMs();
    }
}
