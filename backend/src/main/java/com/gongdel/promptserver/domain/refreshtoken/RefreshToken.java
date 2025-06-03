package com.gongdel.promptserver.domain.refreshtoken;

import com.gongdel.promptserver.domain.user.UserId;
import lombok.Value;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 리프레시 토큰 도메인 모델입니다.
 * 사용자의 인증 상태를 유지하기 위한 리프레시 토큰을 표현합니다.
 */
@Value
public class RefreshToken {
    UserId userId;
    String token;
    LocalDateTime expiresAt;

    /**
     * 리프레시 토큰의 유효성을 검증합니다.
     *
     * @return 유효하면 true, 아니면 false
     */
    public boolean isValid() {
        return expiresAt.isAfter(LocalDateTime.now());
    }

    /**
     * 리프레시 토큰을 생성합니다.
     *
     * @param userId    사용자 ID
     * @param token     토큰 문자열
     * @param expiresAt 만료 일시
     * @return RefreshToken 인스턴스
     * @throws IllegalArgumentException userId가 null이거나, token이 비어있거나, expiresAt이
     *                                  null인 경우
     */
    public static RefreshToken create(UserId userId, String token, LocalDateTime expiresAt) {
        Assert.notNull(userId, "UserId must not be null");
        Assert.hasText(token, "Token must not be empty");
        Assert.notNull(expiresAt, "ExpiresAt must not be null");

        return new RefreshToken(userId, token, expiresAt);
    }
}
