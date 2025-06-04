package com.gongdel.promptserver.domain.model.auth;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 블랙리스트에 등록된 토큰 정보를 나타내는 값 객체입니다.
 */
@Getter
@ToString
@EqualsAndHashCode
@Builder
public class TokenBlacklist {
    /** 블랙리스트에 등록된 토큰 값 */
    private final String token;
    /** 토큰 만료 일시 */
    private final LocalDateTime expiresAt;

    /**
     * 토큰 블랙리스트 객체를 생성합니다.
     *
     * @param token     블랙리스트에 등록할 토큰
     * @param expiresAt 토큰 만료 일시
     */
    public TokenBlacklist(String token, LocalDateTime expiresAt) {
        Assert.hasText(token, "Token must not be empty");
        Assert.notNull(expiresAt, "ExpiresAt must not be null");
        this.token = token;
        this.expiresAt = expiresAt;
    }

}
