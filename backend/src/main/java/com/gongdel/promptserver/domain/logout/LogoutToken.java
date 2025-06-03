package com.gongdel.promptserver.domain.logout;

import com.gongdel.promptserver.domain.user.UserId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 로그아웃 처리를 위한 토큰 정보를 담는 도메인 클래스입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogoutToken {
    private UserId userId;
    private String tokenId;
    private LocalDateTime expiresAt;

    private LogoutToken(UserId userId, String tokenId, LocalDateTime expiresAt) {
        this.userId = userId;
        this.tokenId = tokenId;
        this.expiresAt = expiresAt;
    }

    /**
     * LogoutToken을 생성합니다.
     *
     * @param userId    사용자 ID
     * @param tokenId   토큰 ID
     * @param expiresAt 만료 시간
     * @return 생성된 LogoutToken
     */
    public static LogoutToken create(UserId userId, String tokenId, LocalDateTime expiresAt) {
        return new LogoutToken(userId, tokenId, expiresAt);
    }
}
