package com.gongdel.promptserver.adapter.out.persistence.command;

import com.gongdel.promptserver.application.port.out.command.BlacklistTokenPort;
import com.gongdel.promptserver.application.port.out.command.DeleteRefreshTokenPort;
import com.gongdel.promptserver.application.port.out.command.LogoutPort;
import com.gongdel.promptserver.domain.exception.InvalidJwtException;
import com.gongdel.promptserver.domain.logout.LogoutToken;
import com.gongdel.promptserver.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 로그아웃 처리를 위한 어댑터입니다.
 * 토큰을 블랙리스트에 등록하고 관련 리소스를 정리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class LogoutCommandAdapter implements LogoutPort {
    private static final String LOGOUT_ERROR_MESSAGE = "Failed to process logout";
    private static final String BLACKLIST_ERROR_MESSAGE = "Failed to blacklist token";
    private static final String DELETE_REFRESH_ERROR_MESSAGE = "Failed to delete refresh token";
    private static final String VALIDATION_ERROR_MESSAGE = "Invalid logout token: %s";
    private final BlacklistTokenPort blacklistTokenPort;
    private final DeleteRefreshTokenPort deleteRefreshTokenPort;

    /**
     * 로그아웃 처리를 수행합니다.
     * JWT 토큰을 블랙리스트에 등록하고 관련 리소스를 정리합니다.
     *
     * @param logoutToken 로그아웃할 토큰 정보
     * @throws InvalidJwtException 토큰이 유효하지 않은 경우
     */
    @Override
    public void logout(LogoutToken logoutToken) {
        try {
            validateLogoutToken(logoutToken);
            log.info("Starting logout process for user: [{}]", logoutToken.getUserId());
            processLogout(logoutToken);
            log.info("Successfully completed logout process for user: [{}]", logoutToken.getUserId());
        } catch (InvalidJwtException e) {
            log.error("Logout failed for user: [{}], error: {}", logoutToken != null ? logoutToken.getUserId() : "null", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during logout for user: [{}], error: {}", logoutToken.getUserId(), e.getMessage(), e);
            throw new InvalidJwtException(LOGOUT_ERROR_MESSAGE, e);
        }
    }

    /**
     * 로그아웃 토큰의 유효성을 검증합니다.
     *
     * @param logoutToken 검증할 로그아웃 토큰
     * @throws InvalidJwtException 토큰이 유효하지 않은 경우
     */
    private void validateLogoutToken(LogoutToken logoutToken) {
        try {
            Assert.notNull(logoutToken, "Logout token must not be null");
            Assert.notNull(logoutToken.getUserId(), "User ID must not be null");
            Assert.hasText(logoutToken.getTokenId(), "Token ID must not be empty");
            Assert.notNull(logoutToken.getExpiresAt(), "Expiration time must not be null");
        } catch (IllegalArgumentException e) {
            throw new InvalidJwtException(String.format(VALIDATION_ERROR_MESSAGE, e.getMessage()), e);
        }
    }

    /**
     * 로그아웃 처리를 수행합니다.
     *
     * @param logoutToken 로그아웃할 토큰 정보
     */
    private void processLogout(LogoutToken logoutToken) {
        blacklistToken(logoutToken);
        deleteRefreshToken(logoutToken);
    }

    /**
     * 토큰을 블랙리스트에 등록합니다.
     *
     * @param logoutToken 블랙리스트에 등록할 토큰 정보
     */
    private void blacklistToken(LogoutToken logoutToken) {
        final UserId userId = logoutToken.getUserId();
        log.debug("Blacklisting token for user: [{}]", userId);

        try {
            blacklistTokenPort.blacklistToken(
                logoutToken.getTokenId(),
                userId,
                logoutToken.getExpiresAt());
        } catch (Exception e) {
            log.error("Failed to blacklist token for user: [{}], error: {}", userId, e.getMessage());
            throw new InvalidJwtException(BLACKLIST_ERROR_MESSAGE, e);
        }
    }

    /**
     * 리프레시 토큰을 삭제합니다.
     *
     * @param logoutToken 삭제할 토큰 정보
     */
    private void deleteRefreshToken(LogoutToken logoutToken) {
        final UserId userId = logoutToken.getUserId();
        log.debug("Deleting refresh token for user: [{}]", userId);

        try {
            deleteRefreshTokenPort.deleteByUserId(userId);
        } catch (Exception e) {
            log.error("Failed to delete refresh token for user: [{}], error: {}", userId, e.getMessage());
            throw new InvalidJwtException(DELETE_REFRESH_ERROR_MESSAGE, e);
        }
    }
}
