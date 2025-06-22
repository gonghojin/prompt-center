package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.refreshtoken.RefreshToken;
import com.gongdel.promptserver.domain.user.UserId;

import java.util.Optional;

/**
 * 토큰 조회를 위한 포트입니다.
 */
public interface LoadTokenPort {
    /**
     * 사용자 ID로 리프레시 토큰을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return Optional<RefreshToken>
     */
    Optional<RefreshToken> loadRefreshTokenByUserId(UserId userId);

    /**
     * 토큰 값으로 리프레시 토큰을 조회합니다.
     *
     * @param token 토큰 값
     * @return Optional<RefreshToken>
     */
    Optional<RefreshToken> loadRefreshTokenByToken(String token);
}
