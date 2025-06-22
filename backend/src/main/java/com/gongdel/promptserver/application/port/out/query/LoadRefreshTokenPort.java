package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.refreshtoken.RefreshToken;
import com.gongdel.promptserver.domain.user.UserId;

import java.util.Optional;

/**
 * 리프레시 토큰 조회를 위한 포트입니다.
 */
public interface LoadRefreshTokenPort {
    /**
     * 사용자 ID로 리프레시 토큰을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return Optional<RefreshToken>
     */
    Optional<RefreshToken> findByUserId(UserId userId);

    /**
     * 리프레시 토큰의 유효성을 검증합니다.
     *
     * @param token 리프레시 토큰
     * @return 유효하면 true, 아니면 false
     */
    boolean isRefreshTokenValid(String token);
}
