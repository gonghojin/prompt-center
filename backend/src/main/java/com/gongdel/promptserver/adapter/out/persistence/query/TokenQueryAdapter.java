package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.mapper.TokenMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.RefreshTokenRepository;
import com.gongdel.promptserver.adapter.out.persistence.repository.TokenBlacklistRepository;
import com.gongdel.promptserver.application.port.out.query.CheckTokenBlacklistPort;
import com.gongdel.promptserver.application.port.out.query.LoadTokenPort;
import com.gongdel.promptserver.domain.refreshtoken.RefreshToken;
import com.gongdel.promptserver.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 토큰 조회를 위한 어댑터입니다.
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenQueryAdapter implements LoadTokenPort, CheckTokenBlacklistPort {
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final TokenMapper tokenMapper;

    @Override
    public Optional<RefreshToken> loadRefreshTokenByUserId(UserId userId) {
        return refreshTokenRepository.findByUserId(userId.getValue().toString())
            .map(tokenMapper::toDomain);
    }

    @Override
    public Optional<RefreshToken> loadRefreshTokenByToken(String token) {
        return refreshTokenRepository.findByToken(token)
            .map(tokenMapper::toDomain);
    }

    @Override
    public boolean isBlacklisted(String tokenId) {
        return tokenBlacklistRepository.existsByTokenId(tokenId);
    }
}
