package com.gongdel.promptserver.application.port.out.command;

import java.time.LocalDateTime;

/**
 * 토큰 블랙리스트 저장을 위한 아웃바운드 포트 인터페이스입니다.
 */
public interface SaveTokenBlacklistPort {
    /**
     * 토큰을 블랙리스트에 추가합니다.
     *
     * @param token     블랙리스트에 추가할 토큰
     * @param expiresAt 만료 일시
     */
    void addToBlacklist(String token, LocalDateTime expiresAt);
}
