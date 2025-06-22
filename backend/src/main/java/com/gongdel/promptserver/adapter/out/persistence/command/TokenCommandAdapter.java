package com.gongdel.promptserver.adapter.out.persistence.command;

import com.gongdel.promptserver.adapter.out.persistence.entity.RefreshTokenEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.TokenBlacklistEntity;
import com.gongdel.promptserver.adapter.out.persistence.mapper.TokenMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.RefreshTokenRepository;
import com.gongdel.promptserver.adapter.out.persistence.repository.TokenBlacklistRepository;
import com.gongdel.promptserver.application.port.out.command.BlacklistTokenPort;
import com.gongdel.promptserver.application.port.out.command.DeleteRefreshTokenPort;
import com.gongdel.promptserver.application.port.out.command.SaveRefreshTokenPort;
import com.gongdel.promptserver.domain.exception.TokenException;
import com.gongdel.promptserver.domain.exception.TokenValidationException;
import com.gongdel.promptserver.domain.refreshtoken.RefreshToken;
import com.gongdel.promptserver.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 토큰 관련 명령을 처리하는 어댑터입니다.
 * 리프레시 토큰의 저장, 삭제 및 블랙리스트 관리를 담당합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCommandAdapter implements SaveRefreshTokenPort, DeleteRefreshTokenPort, BlacklistTokenPort {
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final TokenMapper tokenMapper;

    /**
     * 리프레시 토큰을 저장합니다.
     *
     * @param refreshToken 저장할 리프레시 토큰
     * @throws TokenException           토큰 저장 실패 시
     * @throws TokenValidationException 입력값 검증 실패 시
     */
    @Override
    @Transactional
    public void saveRefreshToken(RefreshToken refreshToken) {
        try {
            validateRefreshToken(refreshToken);
            log.debug("Starting to save refresh token for userId: {}", refreshToken.getUserId());

            RefreshTokenEntity entity = tokenMapper.toEntity(refreshToken);
            refreshTokenRepository.save(entity);
            log.info("Successfully saved refresh token for userId: {}", refreshToken.getUserId());
        } catch (TokenValidationException e) {
            log.error("Token validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to save refresh token for userId: {}", refreshToken.getUserId(), e);
            throw TokenException.saveFailed(refreshToken.getUserId().toString(), e);
        }
    }

    /**
     * 사용자 ID에 해당하는 리프레시 토큰을 삭제합니다.
     *
     * @param userId 삭제할 리프레시 토큰의 사용자 ID
     * @throws TokenException           토큰 삭제 실패 시
     * @throws TokenValidationException 입력값 검증 실패 시
     */
    @Override
    @Transactional
    public void deleteByUserId(UserId userId) {
        try {
            validateUserId(userId);
            log.debug("Starting to delete refresh token for userId: {}", userId.getValue());

            refreshTokenRepository.deleteByUserId(userId.getValue().toString());
            log.info("Successfully deleted refresh token for userId: {}", userId.getValue());
        } catch (TokenValidationException e) {
            log.error("Token validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete refresh token for userId: {}", userId.getValue(), e);
            throw TokenException.deleteFailed(userId.getValue().toString(), e);
        }
    }

    /**
     * 특정 리프레시 토큰을 삭제합니다.
     *
     * @param token 삭제할 리프레시 토큰
     * @throws TokenException           토큰 삭제 실패 시
     * @throws TokenValidationException 입력값 검증 실패 시
     */
    @Override
    @Transactional
    public void deleteRefreshToken(String token) {
        try {
            validateToken(token);
            log.debug("Starting to delete refresh token: {}", token);

            refreshTokenRepository.deleteByToken(token);
            log.info("Successfully deleted refresh token");
        } catch (TokenValidationException e) {
            log.error("Token validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete refresh token", e);
            throw TokenException.deleteFailed(token, e);
        }
    }

    /**
     * 토큰을 블랙리스트에 추가합니다.
     *
     * @param tokenId   블랙리스트에 추가할 토큰 ID
     * @param userId    토큰 소유자의 사용자 ID
     * @param expiresAt 토큰 만료 시간
     * @throws TokenException           토큰 블랙리스트 추가 실패 시
     * @throws TokenValidationException 입력값 검증 실패 시
     */
    @Override
    @Transactional
    public void blacklistToken(String tokenId, UserId userId, LocalDateTime expiresAt) {
        try {
            validateBlacklistInput(tokenId, userId, expiresAt);
            log.debug("Starting to blacklist token: tokenId={}, userId={}", tokenId, userId.getValue());

            TokenBlacklistEntity entity = TokenBlacklistEntity.builder()
                .tokenId(tokenId)
                .userId(userId.getValue().toString())
                .expiresAt(expiresAt)
                .build();
            tokenBlacklistRepository.save(entity);
            log.info("Successfully blacklisted token: tokenId={}, userId={}", tokenId, userId.getValue());
        } catch (TokenValidationException e) {
            log.error("Token validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to blacklist token: tokenId={}, userId={}", tokenId, userId.getValue(), e);
            throw TokenException.blacklistFailed(tokenId, e);
        }
    }

    private void validateRefreshToken(RefreshToken refreshToken) {
        if (refreshToken == null) {
            throw TokenValidationException.nullRefreshToken();
        }
    }

    private void validateUserId(UserId userId) {
        if (userId == null) {
            throw TokenValidationException.nullUserId();
        }
    }

    private void validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw TokenValidationException.emptyToken();
        }
    }

    private void validateBlacklistInput(String tokenId, UserId userId, LocalDateTime expiresAt) {
        if (!StringUtils.hasText(tokenId)) {
            throw TokenValidationException.emptyToken();
        }
        if (userId == null) {
            throw TokenValidationException.nullUserId();
        }
        if (expiresAt == null) {
            throw TokenValidationException.nullExpiresAt();
        }
        if (expiresAt.isBefore(LocalDateTime.now())) {
            throw TokenValidationException.invalidExpirationTime();
        }
    }
}
