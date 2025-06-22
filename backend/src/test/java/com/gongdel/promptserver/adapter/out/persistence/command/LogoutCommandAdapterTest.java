package com.gongdel.promptserver.adapter.out.persistence.command;

import com.gongdel.promptserver.application.port.out.command.BlacklistTokenPort;
import com.gongdel.promptserver.application.port.out.command.DeleteRefreshTokenPort;
import com.gongdel.promptserver.domain.exception.InvalidJwtException;
import com.gongdel.promptserver.domain.logout.LogoutToken;
import com.gongdel.promptserver.domain.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LogoutCommandAdapter 테스트")
class LogoutCommandAdapterTest {

    @Mock
    private BlacklistTokenPort blacklistTokenPort;

    @Mock
    private DeleteRefreshTokenPort deleteRefreshTokenPort;

    @InjectMocks
    private LogoutCommandAdapter logoutCommandAdapter;

    @Nested
    @DisplayName("logout(LogoutToken) 메서드는")
    class LogoutTest {

        private LogoutToken logoutToken;
        private UserId userId;
        private String tokenId;
        private LocalDateTime expiresAt;

        @BeforeEach
        void setUp() {
            userId = new UserId(UUID.randomUUID());
            tokenId = UUID.randomUUID().toString();
            expiresAt = LocalDateTime.now().plusHours(1);
            logoutToken = LogoutToken.create(userId, tokenId, expiresAt);
        }

        @Test
        @DisplayName("로그아웃 처리를 성공적으로 수행한다")
        void givenValidLogoutToken_whenLogout_thenSuccess() {
            // Given
            doNothing().when(blacklistTokenPort).blacklistToken(any(), any(), any());
            doNothing().when(deleteRefreshTokenPort).deleteByUserId(any());

            // When
            logoutCommandAdapter.logout(logoutToken);

            // Then
            verify(blacklistTokenPort).blacklistToken(tokenId, userId, expiresAt);
            verify(deleteRefreshTokenPort).deleteByUserId(userId);
        }

        @Test
        @DisplayName("null LogoutToken이 전달되면 InvalidJwtException을 던진다")
        void givenNullLogoutToken_whenLogout_thenThrowsInvalidJwtException() {
            // When & Then
            assertThatThrownBy(() -> logoutCommandAdapter.logout(null))
                .isInstanceOf(InvalidJwtException.class)
                .hasMessageContaining("Logout token must not be null");
        }

        @Test
        @DisplayName("null UserId가 포함된 LogoutToken이 전달되면 InvalidJwtException을 던진다")
        void givenLogoutTokenWithNullUserId_whenLogout_thenThrowsInvalidJwtException() {
            // Given
            LogoutToken invalidToken = LogoutToken.create(null, tokenId, expiresAt);

            // When & Then
            assertThatThrownBy(() -> logoutCommandAdapter.logout(invalidToken))
                .isInstanceOf(InvalidJwtException.class)
                .hasMessageContaining("User ID must not be null");
        }

        @Test
        @DisplayName("빈 TokenId가 포함된 LogoutToken이 전달되면 InvalidJwtException을 던진다")
        void givenLogoutTokenWithEmptyTokenId_whenLogout_thenThrowsInvalidJwtException() {
            // Given
            LogoutToken invalidToken = LogoutToken.create(userId, "", expiresAt);

            // When & Then
            assertThatThrownBy(() -> logoutCommandAdapter.logout(invalidToken))
                .isInstanceOf(InvalidJwtException.class)
                .hasMessageContaining("Token ID must not be empty");
        }

        @Test
        @DisplayName("null ExpiresAt이 포함된 LogoutToken이 전달되면 InvalidJwtException을 던진다")
        void givenLogoutTokenWithNullExpiresAt_whenLogout_thenThrowsInvalidJwtException() {
            // Given
            LogoutToken invalidToken = LogoutToken.create(userId, tokenId, null);

            // When & Then
            assertThatThrownBy(() -> logoutCommandAdapter.logout(invalidToken))
                .isInstanceOf(InvalidJwtException.class)
                .hasMessageContaining("Expiration time must not be null");
        }

        @Test
        @DisplayName("블랙리스트 토큰 등록 실패 시 InvalidJwtException을 던진다")
        void givenBlacklistTokenFailure_whenLogout_thenThrowsInvalidJwtException() {
            // Given
            doThrow(new RuntimeException("Blacklist error"))
                .when(blacklistTokenPort).blacklistToken(any(), any(), any());

            // When & Then
            assertThatThrownBy(() -> logoutCommandAdapter.logout(logoutToken))
                .isInstanceOf(InvalidJwtException.class)
                .hasMessageContaining("Failed to blacklist token");

            verify(deleteRefreshTokenPort, never()).deleteByUserId(any());
        }

        @Test
        @DisplayName("리프레시 토큰 삭제 실패 시 InvalidJwtException을 던진다")
        void givenDeleteRefreshTokenFailure_whenLogout_thenThrowsInvalidJwtException() {
            // Given
            doNothing().when(blacklistTokenPort).blacklistToken(any(), any(), any());
            doThrow(new RuntimeException("Delete error"))
                .when(deleteRefreshTokenPort).deleteByUserId(any());

            // When & Then
            assertThatThrownBy(() -> logoutCommandAdapter.logout(logoutToken))
                .isInstanceOf(InvalidJwtException.class)
                .hasMessageContaining("Failed to delete refresh token");

            verify(blacklistTokenPort).blacklistToken(tokenId, userId, expiresAt);
        }
    }
}
