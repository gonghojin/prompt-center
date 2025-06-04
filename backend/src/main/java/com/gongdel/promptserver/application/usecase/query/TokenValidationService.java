package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.port.out.query.CheckTokenBlacklistPort;
import com.gongdel.promptserver.common.security.JwtTokenProvider;
import com.gongdel.promptserver.domain.exception.InvalidJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 토큰 검증을 담당하는 서비스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenValidationService {
    private final JwtTokenProvider jwtTokenProvider;
    private final CheckTokenBlacklistPort checkTokenBlacklistPort;

    /**
     * JWT 토큰의 유효성을 검증합니다.
     *
     * @param token 검증할 JWT 토큰
     * @return 유효하면 true, 아니면 false
     * @throws InvalidJwtException 토큰이 유효하지 않거나 블랙리스트에 등록된 경우
     */
    public boolean validateToken(String token) {
        // 1. 토큰 형식 및 서명 검증
        if (!jwtTokenProvider.validateToken(token)) {
            log.warn("Invalid JWT token format or signature");
            throw new InvalidJwtException("유효하지 않은 토큰입니다.");
        }

        // 2. 블랙리스트 검증
        String tokenId = jwtTokenProvider.getTokenId(token);
        if (checkTokenBlacklistPort.isBlacklisted(tokenId)) {
            log.warn("Token is blacklisted: {}", tokenId);
            throw new InvalidJwtException("블랙리스트에 등록된 토큰입니다.");
        }

        log.debug("Token validation successful: {}", tokenId);
        return true;
    }
}
