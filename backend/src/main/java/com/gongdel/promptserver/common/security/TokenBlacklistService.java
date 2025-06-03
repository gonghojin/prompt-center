package com.gongdel.promptserver.common.security;

import com.gongdel.promptserver.adapter.out.persistence.entity.TokenBlacklistEntity;
import com.gongdel.promptserver.adapter.out.persistence.repository.TokenBlacklistRepository;
import com.gongdel.promptserver.domain.user.UserId;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 토큰 블랙리스트 관리 서비스
 * <p>
 * JWT 토큰의 블랙리스트 등록 및 검증 기능을 제공합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final TokenBlacklistRepository tokenBlacklistRepository;

    /**
     * 토큰을 블랙리스트에 등록합니다.
     *
     * @param tokenId   JWT 토큰 ID(jti)
     * @param userId    사용자 ID
     * @param expiresAt 토큰 만료 일시
     */
    @Transactional
    public void blacklistToken(String tokenId, UserId userId, LocalDateTime expiresAt) {
        TokenBlacklistEntity entity = TokenBlacklistEntity.builder()
                .tokenId(tokenId)
                .userId(userId.getValue().toString())
                .expiresAt(expiresAt)
                .build();

        tokenBlacklistRepository.save(entity);
    }

    /**
     * 해당 토큰이 블랙리스트에 등록되어 있는지 확인합니다.
     *
     * @param tokenId JWT 토큰 ID(jti)
     * @return 블랙리스트 등록 여부
     */
    @Transactional(readOnly = true)
    public boolean isTokenBlacklisted(String tokenId) {
        return tokenBlacklistRepository.existsByTokenId(tokenId);
    }
}
