package com.gongdel.promptserver.application.port.out.command;

import com.gongdel.promptserver.domain.user.UserId;

/**
 * 리프레시 토큰 삭제를 위한 포트입니다.
 */
public interface DeleteRefreshTokenPort {
    /**
     * 사용자의 리프레시 토큰을 삭제합니다.
     *
     * @param userId 사용자 ID
     */
    void deleteByUserId(UserId userId);

    /**
     * 특정 리프레시 토큰을 삭제합니다.
     *
     * @param token 삭제할 토큰 값
     */
    void deleteRefreshToken(String token);
}
