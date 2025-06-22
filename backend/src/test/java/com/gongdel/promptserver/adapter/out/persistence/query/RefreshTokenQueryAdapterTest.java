package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.entity.RefreshTokenEntity;
import com.gongdel.promptserver.adapter.out.persistence.repository.RefreshTokenRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenQueryAdapter 테스트")
class RefreshTokenQueryAdapterTest {

    private static final LocalDateTime FIXED_NOW = LocalDateTime.now().plusHours(3);
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @InjectMocks
    private RefreshTokenQueryAdapter refreshTokenQueryAdapter;

    @Nested
    @DisplayName("findByUserId(UserId) 메서드는")
    class FindByUserIdTest {

        private UserId userId;
        private RefreshTokenEntity mockEntity;
        private String userIdString;

        @BeforeEach
        void setUp() {
            userId = new UserId(UUID.randomUUID());
            userIdString = userId.toString();
            mockEntity = RefreshTokenEntity.builder()
                .userId(userIdString)
                .token("test-token")
                .expiresAt(FIXED_NOW.plusHours(1))
                .build();
        }

        @Test
        @DisplayName("리프레시 토큰을 성공적으로 조회한다")
        void givenValidUserId_whenFindByUserId_thenReturnsRefreshToken() {
            // Given
            when(refreshTokenRepository.findByUserId(userIdString))
                .thenReturn(Optional.of(mockEntity));

            // When
            Optional<RefreshToken> result = refreshTokenQueryAdapter.findByUserId(userId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getUserId().toString()).isEqualTo(userIdString);
            verify(refreshTokenRepository).findByUserId(userIdString);
        }

        @Test
        @DisplayName("리프레시 토큰이 존재하지 않으면 빈 Optional을 반환한다")
        void givenNonExistentUserId_whenFindByUserId_thenReturnsEmpty() {
            // Given
            when(refreshTokenRepository.findByUserId(userIdString))
                .thenReturn(Optional.empty());

            // When
            Optional<RefreshToken> result = refreshTokenQueryAdapter.findByUserId(userId);

            // Then
            assertThat(result).isEmpty();
            verify(refreshTokenRepository).findByUserId(userIdString);
        }

        @Test
        @DisplayName("null UserId가 전달되면 TokenValidationException을 던진다")
        void givenNullUserId_whenFindByUserId_thenThrowsTokenValidationException() {
            // When & Then
            assertThatThrownBy(() -> refreshTokenQueryAdapter.findByUserId(null))
                .isInstanceOf(TokenValidationException.class)
                .hasMessageContaining("사용자 ID는 null일 수 없습니다");
        }

        @Test
        @DisplayName("데이터베이스 오류 발생 시 TokenException을 던진다")
        void givenDatabaseError_whenFindByUserId_thenThrowsTokenException() {
            // Given
            when(refreshTokenRepository.findByUserId(userIdString))
                .thenThrow(new DataAccessException("Database error") {
                });

            // When & Then
            assertThatThrownBy(() -> refreshTokenQueryAdapter.findByUserId(userId))
                .isInstanceOf(TokenException.class)
                .hasMessageContaining("토큰을 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("isRefreshTokenValid(String) 메서드는")
    class IsRefreshTokenValidTest {

        private String validToken;
        private RefreshTokenEntity mockEntity;

        @BeforeEach
        void setUp() {
            validToken = "valid-token";
            mockEntity = RefreshTokenEntity.builder()
                .userId(UUID.randomUUID().toString())
                .token(validToken)
                .expiresAt(FIXED_NOW.plusHours(1))
                .build();
        }

        @Test
        @DisplayName("유효한 토큰이면 true를 반환한다")
        void givenValidToken_whenIsRefreshTokenValid_thenReturnsTrue() {
            // Given
            when(refreshTokenRepository.findByToken(validToken))
                .thenReturn(Optional.of(mockEntity));

            // When
            boolean result = refreshTokenQueryAdapter.isRefreshTokenValid(validToken);

            // Then
            assertThat(result).isTrue();
            verify(refreshTokenRepository).findByToken(validToken);
        }

        @Test
        @DisplayName("만료된 토큰이면 false를 반환한다")
        void givenExpiredToken_whenIsRefreshTokenValid_thenReturnsFalse() {
            // Given
            mockEntity.setExpiresAt(FIXED_NOW.minusDays(1));
            when(refreshTokenRepository.findByToken(validToken))
                .thenReturn(Optional.of(mockEntity));

            // When
            boolean result = refreshTokenQueryAdapter.isRefreshTokenValid(validToken);

            // Then
            assertThat(result).isFalse();
            verify(refreshTokenRepository).findByToken(validToken);
        }

        @Test
        @DisplayName("토큰이 존재하지 않으면 false를 반환한다")
        void givenNonExistentToken_whenIsRefreshTokenValid_thenReturnsFalse() {
            // Given
            when(refreshTokenRepository.findByToken(validToken))
                .thenReturn(Optional.empty());

            // When
            boolean result = refreshTokenQueryAdapter.isRefreshTokenValid(validToken);

            // Then
            assertThat(result).isFalse();
            verify(refreshTokenRepository).findByToken(validToken);
        }

        @Test
        @DisplayName("null 토큰이 전달되면 TokenValidationException을 던진다")
        void givenNullToken_whenIsRefreshTokenValid_thenThrowsTokenValidationException() {
            // When & Then
            assertThatThrownBy(() -> refreshTokenQueryAdapter.isRefreshTokenValid(null))
                .isInstanceOf(TokenValidationException.class)
                .hasMessageContaining("토큰은 비어있을 수 없습니다");
        }

        @Test
        @DisplayName("빈 토큰이 전달되면 TokenValidationException을 던진다")
        void givenEmptyToken_whenIsRefreshTokenValid_thenThrowsTokenValidationException() {
            // When & Then
            assertThatThrownBy(() -> refreshTokenQueryAdapter.isRefreshTokenValid(""))
                .isInstanceOf(TokenValidationException.class)
                .hasMessageContaining("토큰은 비어있을 수 없습니다");
        }

        @Test
        @DisplayName("데이터베이스 오류 발생 시 TokenValidationException을 던진다")
        void givenDatabaseError_whenIsRefreshTokenValid_thenThrowsTokenValidationException() {
            // Given
            when(refreshTokenRepository.findByToken(validToken))
                .thenThrow(new DataAccessException("Database error") {
                });

            // When & Then
            assertThatThrownBy(() -> refreshTokenQueryAdapter.isRefreshTokenValid(validToken))
                .isInstanceOf(TokenValidationException.class)
                .hasMessageContaining("토큰 형식이 올바르지 않습니다");
        }
    }
}
