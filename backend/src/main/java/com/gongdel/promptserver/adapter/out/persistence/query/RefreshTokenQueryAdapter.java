package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.entity.RefreshTokenEntity;
import com.gongdel.promptserver.adapter.out.persistence.repository.RefreshTokenRepository;
import com.gongdel.promptserver.application.port.out.query.LoadRefreshTokenPort;
import com.gongdel.promptserver.domain.exception.TokenException;
import com.gongdel.promptserver.domain.exception.TokenValidationException;
import com.gongdel.promptserver.domain.refreshtoken.RefreshToken;
import com.gongdel.promptserver.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * 리프레시 토큰을 조회하는 어댑터입니다.
 * 사용자 ID로 리프레시 토큰을 조회하고, 토큰의 유효성을 검증하는 기능을 제공합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenQueryAdapter implements LoadRefreshTokenPort {
    private static final String ERROR_INVALID_USER_ID = "Invalid user ID: {}";
    private static final String ERROR_INVALID_TOKEN = "Invalid token format";
    private static final String ERROR_TOKEN_EXPIRED = "Token expired at: {}";
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 사용자 ID로 리프레시 토큰을 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 조회된 리프레시 토큰 (Optional)
     * @throws TokenValidationException 사용자 ID가 null인 경우
     */
    @Override
    public Optional<RefreshToken> findByUserId(UserId userId) {
        log.debug("Finding refresh token for user ID: {}", userId);

        try {
            Assert.notNull(userId, "User ID must not be null");
            return refreshTokenRepository.findByUserId(userId.toString())
                .map(this::toDomain);
        } catch (IllegalArgumentException e) {
            log.error(ERROR_INVALID_USER_ID, userId, e);
            throw TokenValidationException.nullUserId();
        } catch (Exception e) {
            log.error("Failed to find refresh token for user ID: {}", userId, e);
            throw TokenException.notFound(userId.toString());
        }
    }

    /**
     * 리프레시 토큰의 유효성을 검증합니다.
     *
     * @param token 검증할 리프레시 토큰
     * @return 토큰의 유효성 여부
     * @throws TokenValidationException 토큰이 null이거나 비어있는 경우
     */
    @Override
    public boolean isRefreshTokenValid(String token) {
        log.debug("Validating refresh token");

        try {
            Assert.hasText(token, "Token must not be empty");
            return refreshTokenRepository.findByToken(token)
                .map(this::isTokenNotExpired)
                .orElse(false);
        } catch (IllegalArgumentException e) {
            log.error(ERROR_INVALID_TOKEN, e);
            throw TokenValidationException.emptyToken();
        } catch (Exception e) {
            log.error("Failed to validate refresh token", e);
            throw TokenValidationException.invalidTokenFormat();
        }
    }

    /**
     * 엔티티를 도메인 객체로 변환합니다.
     *
     * @param entity 변환할 리프레시 토큰 엔티티
     * @return 변환된 리프레시 토큰 도메인 객체
     * @throws TokenValidationException 엔티티의 필수 필드가 null인 경우
     */
    private RefreshToken toDomain(RefreshTokenEntity entity) {
        try {
            Assert.notNull(entity, "Entity must not be null");
            Assert.hasText(entity.getUserId(), "User ID must not be empty");
            Assert.hasText(entity.getToken(), "Token must not be empty");
            Assert.notNull(entity.getExpiresAt(), "Expiration time must not be null");

            try {
                UUID userId = UUID.fromString(entity.getUserId());
                return RefreshToken.create(
                    new UserId(userId),
                    entity.getToken(),
                    entity.getExpiresAt());
            } catch (IllegalArgumentException e) {
                log.error("Invalid UUID format for user ID: {}", entity.getUserId(), e);
                throw TokenValidationException.invalidTokenFormat();
            }
        } catch (IllegalArgumentException e) {
            log.error("Failed to convert entity to domain: {}", entity, e);
            throw determineValidationException(entity, e);
        }
    }

    /**
     * 토큰의 만료 여부를 확인합니다.
     *
     * @param entity 확인할 리프레시 토큰 엔티티
     * @return 토큰의 만료 여부
     * @throws TokenValidationException 만료 시간이 null인 경우
     */
    private boolean isTokenNotExpired(RefreshTokenEntity entity) {
        try {
            Assert.notNull(entity.getExpiresAt(), "Expiration time must not be null");

            LocalDateTime now = LocalDateTime.now();
            boolean isNotExpired = entity.getExpiresAt().isAfter(now);
            if (!isNotExpired) {
                log.debug(ERROR_TOKEN_EXPIRED, entity.getExpiresAt());
            }
            return isNotExpired;
        } catch (IllegalArgumentException e) {
            log.error("Invalid expiration time for token", e);
            throw TokenValidationException.nullExpiresAt();
        }
    }

    private TokenValidationException determineValidationException(RefreshTokenEntity entity, Throwable cause) {
        if (entity == null) {
            return new TokenValidationException("리프레시 토큰은 null일 수 없습니다.", cause);
        }
        if (entity.getUserId() == null || entity.getUserId().isEmpty()) {
            return new TokenValidationException("사용자 ID는 null일 수 없습니다.", cause);
        }
        if (entity.getToken() == null || entity.getToken().isEmpty()) {
            return new TokenValidationException("토큰은 비어있을 수 없습니다.", cause);
        }
        if (entity.getExpiresAt() == null) {
            return new TokenValidationException("만료 시간은 null일 수 없습니다.", cause);
        }
        return new TokenValidationException("토큰 형식이 올바르지 않습니다.", cause);
    }
}
