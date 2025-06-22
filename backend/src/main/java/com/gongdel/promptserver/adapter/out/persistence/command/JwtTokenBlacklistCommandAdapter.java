package com.gongdel.promptserver.adapter.out.persistence.command;

import com.gongdel.promptserver.application.port.out.command.SaveTokenBlacklistPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 토큰 블랙리스트 저장을 위한 어댑터입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenBlacklistCommandAdapter implements SaveTokenBlacklistPort {
    // 실제 구현에서는 JPA Repository 등 의존성 주입 필요

    /**
     * 토큰을 블랙리스트에 추가합니다.
     *
     * @param token     블랙리스트에 추가할 토큰
     * @param expiresAt 만료 일시
     */
    @Override
    public void addToBlacklist(String token, LocalDateTime expiresAt) {
        log.debug("Adding token to blacklist: {}, expires at: {}", token, expiresAt);
        // TODO: 블랙리스트 저장 로직 구현
    }
}
