package com.gongdel.promptserver.adapter.out.persistence.command;

import com.gongdel.promptserver.adapter.out.persistence.entity.RefreshTokenEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.TokenBlacklistEntity;
import com.gongdel.promptserver.adapter.out.persistence.mapper.TokenMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.RefreshTokenRepository;
import com.gongdel.promptserver.adapter.out.persistence.repository.TokenBlacklistRepository;
import com.gongdel.promptserver.domain.exception.TokenException;
import com.gongdel.promptserver.domain.exception.TokenValidationException;
import com.gongdel.promptserver.domain.refreshtoken.RefreshToken;
import com.gongdel.promptserver.domain.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenCommandAdapter 테스트")
class TokenCommandAdapterTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private TokenBlacklistRepository tokenBlacklistRepository;

    @Mock
    private TokenMapper tokenMapper;

    @InjectMocks
    private TokenCommandAdapter tokenCommandAdapter;

    private UserId userId;
    private RefreshToken refreshToken;
    private RefreshTokenEntity refreshTokenEntity;
    private LocalDateTime expiresAt;

    @BeforeEach
    void setUp() {
        userId = new UserId(UUID.randomUUID());
        refreshToken = mock(RefreshToken.class);
        refreshTokenEntity = mock(RefreshTokenEntity.class);
        expiresAt = LocalDateTime.now().plusHours(1);
    }

    @Nested
    @DisplayName("saveRefreshToken() 메서드는")
    class SaveRefreshTokenTest {

        @Test
        @DisplayName("리프레시 토큰을 성공적으로 저장한다")
        void givenValidRefreshToken_whenSaveRefreshToken_thenSavesSuccessfully() {
            // Given
            when(refreshToken.getUserId()).thenReturn(userId);
            when(tokenMapper.toEntity(any(RefreshToken.class))).thenReturn(refreshTokenEntity);

            // When
            tokenCommandAdapter.saveRefreshToken(refreshToken);

            // Then
            verify(tokenMapper).toEntity(refreshToken);
            verify(refreshTokenRepository).save(refreshTokenEntity);
        }

        @Test
        @DisplayName("null 리프레시 토큰이 전달되면 TokenValidationException을 던진다")
        void givenNullRefreshToken_whenSaveRefreshToken_thenThrowsTokenValidationException() {
            // When & Then
            assertThatThrownBy(() -> tokenCommandAdapter.saveRefreshToken(null))
                .isInstanceOf(TokenValidationException.class)
                .hasMessageContaining("리프레시 토큰은 null일 수 없습니다");
        }

        @Test
        @DisplayName("데이터베이스 오류 발생 시 TokenException을 던진다")
        void givenDatabaseError_whenSaveRefreshToken_thenThrowsTokenException() {
            // Given
            when(refreshToken.getUserId()).thenReturn(userId);
            when(tokenMapper.toEntity(any(RefreshToken.class))).thenReturn(refreshTokenEntity);
            when(refreshTokenRepository.save(any())).thenThrow(new DataAccessException("Database error") {
            });

            // When & Then
            assertThatThrownBy(() -> tokenCommandAdapter.saveRefreshToken(refreshToken))
                .isInstanceOf(TokenException.class)
                .hasMessageContaining(TokenException.saveFailed(userId.getValue().toString(), null).getMessage());
        }
    }

    @Nested
    @DisplayName("deleteByUserId() 메서드는")
    class DeleteByUserIdTest {

        @Test
        @DisplayName("사용자 ID에 해당하는 리프레시 토큰을 성공적으로 삭제한다")
        void givenValidUserId_whenDeleteByUserId_thenDeletesSuccessfully() {
            // When
            tokenCommandAdapter.deleteByUserId(userId);

            // Then
            verify(refreshTokenRepository).deleteByUserId(userId.getValue().toString());
        }

        @Test
        @DisplayName("null 사용자 ID가 전달되면 TokenValidationException을 던진다")
        void givenNullUserId_whenDeleteByUserId_thenThrowsTokenValidationException() {
            // When & Then
            assertThatThrownBy(() -> tokenCommandAdapter.deleteByUserId(null))
                .isInstanceOf(TokenValidationException.class)
                .hasMessageContaining("사용자 ID는 null일 수 없습니다");
        }

        @Test
        @DisplayName("데이터베이스 오류 발생 시 TokenException을 던진다")
        void givenDatabaseError_whenDeleteByUserId_thenThrowsTokenException() {
            // Given
            doThrow(new DataAccessException("Database error") {
            })
                .when(refreshTokenRepository).deleteByUserId(any());

            // When & Then
            assertThatThrownBy(() -> tokenCommandAdapter.deleteByUserId(userId))
                .isInstanceOf(TokenException.class)
                .hasMessageContaining(TokenException.deleteFailed(userId.getValue().toString(), null).getMessage());
        }
    }

    @Nested
    @DisplayName("deleteRefreshToken() 메서드는")
    class DeleteRefreshTokenTest {

        private static final String TOKEN = "valid-token";

        @Test
        @DisplayName("리프레시 토큰을 성공적으로 삭제한다")
        void givenValidToken_whenDeleteRefreshToken_thenDeletesSuccessfully() {
            // When
            tokenCommandAdapter.deleteRefreshToken(TOKEN);

            // Then
            verify(refreshTokenRepository).deleteByToken(TOKEN);
        }

        @Test
        @DisplayName("빈 토큰이 전달되면 TokenValidationException을 던진다")
        void givenEmptyToken_whenDeleteRefreshToken_thenThrowsTokenValidationException() {
            // When & Then
            assertThatThrownBy(() -> tokenCommandAdapter.deleteRefreshToken(""))
                .isInstanceOf(TokenValidationException.class)
                .hasMessageContaining("토큰은 비어있을 수 없습니다");
        }

        @Test
        @DisplayName("데이터베이스 오류 발생 시 TokenException을 던진다")
        void givenDatabaseError_whenDeleteRefreshToken_thenThrowsTokenException() {
            // Given
            doThrow(new DataAccessException("Database error") {
            })
                .when(refreshTokenRepository).deleteByToken(any());

            // When & Then
            assertThatThrownBy(() -> tokenCommandAdapter.deleteRefreshToken(TOKEN))
                .isInstanceOf(TokenException.class)
                .hasMessageContaining(TokenException.deleteFailed(TOKEN, null).getMessage());
        }
    }

    @Nested
    @DisplayName("blacklistToken() 메서드는")
    class BlacklistTokenTest {

        private static final String TOKEN_ID = "valid-token-id";

        @Test
        @DisplayName("토큰을 성공적으로 블랙리스트에 추가한다")
        void givenValidInput_whenBlacklistToken_thenAddsToBlacklistSuccessfully() {
            // When
            tokenCommandAdapter.blacklistToken(TOKEN_ID, userId, expiresAt);

            // Then
            verify(tokenBlacklistRepository).save(any(TokenBlacklistEntity.class));
        }

        @Test
        @DisplayName("빈 토큰 ID가 전달되면 TokenValidationException을 던진다")
        void givenEmptyTokenId_whenBlacklistToken_thenThrowsTokenValidationException() {
            // When & Then
            assertThatThrownBy(() -> tokenCommandAdapter.blacklistToken("", userId, expiresAt))
                .isInstanceOf(TokenValidationException.class)
                .hasMessageContaining("토큰은 비어있을 수 없습니다");
        }

        @Test
        @DisplayName("null 사용자 ID가 전달되면 TokenValidationException을 던진다")
        void givenNullUserId_whenBlacklistToken_thenThrowsTokenValidationException() {
            // When & Then
            assertThatThrownBy(() -> tokenCommandAdapter.blacklistToken(TOKEN_ID, null, expiresAt))
                .isInstanceOf(TokenValidationException.class)
                .hasMessageContaining("사용자 ID는 null일 수 없습니다");
        }

        @Test
        @DisplayName("null 만료 시간이 전달되면 TokenValidationException을 던진다")
        void givenNullExpiresAt_whenBlacklistToken_thenThrowsTokenValidationException() {
            // When & Then
            assertThatThrownBy(() -> tokenCommandAdapter.blacklistToken(TOKEN_ID, userId, null))
                .isInstanceOf(TokenValidationException.class)
                .hasMessageContaining("만료 시간은 null일 수 없습니다");
        }

        @Test
        @DisplayName("만료 시간이 현재 시간보다 이전이면 TokenValidationException을 던진다")
        void givenPastExpiresAt_whenBlacklistToken_thenThrowsTokenValidationException() {
            // Given
            LocalDateTime pastTime = LocalDateTime.now().minusHours(1);

            // When & Then
            assertThatThrownBy(() -> tokenCommandAdapter.blacklistToken(TOKEN_ID, userId, pastTime))
                .isInstanceOf(TokenValidationException.class)
                .hasMessageContaining("만료 시간이 올바르지 않습니다");
        }

        @Test
        @DisplayName("데이터베이스 오류 발생 시 TokenException을 던진다")
        void givenDatabaseError_whenBlacklistToken_thenThrowsTokenException() {
            // Given
            when(tokenBlacklistRepository.save(any())).thenThrow(new DataAccessException("Database error") {
            });

            // When & Then
            assertThatThrownBy(() -> tokenCommandAdapter.blacklistToken(TOKEN_ID, userId, expiresAt))
                .isInstanceOf(TokenException.class)
                .hasMessageContaining("토큰 블랙리스트 등록에 실패했습니다");
        }
    }
}
