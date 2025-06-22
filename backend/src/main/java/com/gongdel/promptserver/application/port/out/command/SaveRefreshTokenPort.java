package com.gongdel.promptserver.application.port.out.command;

import com.gongdel.promptserver.domain.refreshtoken.RefreshToken;

/**
 * 리프레시 토큰 저장을 위한 포트입니다.
 */
public interface SaveRefreshTokenPort {
    /**
     * 리프레시 토큰을 저장합니다.
     *
     * @param refreshToken 저장할 리프레시 토큰
     */
    void saveRefreshToken(RefreshToken refreshToken);
}
