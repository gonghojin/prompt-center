package com.gongdel.promptserver.application.port.out.command;

import com.gongdel.promptserver.domain.user.UserId;

import java.time.LocalDateTime;

/**
 * 토큰 블랙리스트 등록을 위한 포트입니다.
 */
public interface BlacklistTokenPort {
    /**
     * 토큰을 블랙리스트에 등록합니다.
     *
     * @param tokenId   JWT 토큰 ID(jti)
     * @param userId    사용자 ID
     * @param expiresAt 토큰 만료 일시
     */
    void blacklistToken(String tokenId, UserId userId, LocalDateTime expiresAt);
}
