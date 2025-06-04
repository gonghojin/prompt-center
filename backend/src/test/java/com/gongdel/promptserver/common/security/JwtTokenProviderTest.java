package com.gongdel.promptserver.common.security;

import com.gongdel.promptserver.domain.user.Email;
import com.gongdel.promptserver.domain.user.User;
import com.gongdel.promptserver.domain.user.UserId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtTokenProvider 테스트")
class JwtTokenProviderTest {
    private final long accessTokenValidityInMs = 1000 * 60 * 10; // 10분
    private final long refreshTokenValidityInMs = 1000 * 60 * 60 * 24 * 7; // 7일
    private JwtTokenProvider jwtTokenProvider;
    private SecretKey secretKey;
    private User user;

    @BeforeEach
    void setUp() {
        String secret = Base64.getEncoder()
            .encodeToString("test-secret-key-12345678901234567890".getBytes(StandardCharsets.UTF_8));
        secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        jwtTokenProvider = new JwtTokenProvider(secretKey, accessTokenValidityInMs, refreshTokenValidityInMs);
        user = User.builder()
            .id(1L)
            .uuid(new UserId(UUID.randomUUID()))
            .email(new Email("test@example.com"))
            .name("홍길동")
            .build();
    }

    @Nested
    @DisplayName("generateAccessToken(User) 메서드는")
    class GenerateAccessTokenTest {
        @Test
        @DisplayName("정상적으로 access token을 생성하고 name, email 클레임을 포함한다")
        void givenValidUser_whenGenerateAccessToken_thenTokenContainsNameAndEmail() {
            // Given & When
            String token = jwtTokenProvider.generateAccessToken(user);
            // Then
            Claims claims = jwtTokenProvider.getClaims(token);
            assertThat(claims.getSubject()).isEqualTo(user.getUuid().getValue().toString());
            assertThat(claims.get("email", String.class)).isEqualTo(user.getEmail().getValue());
            assertThat(claims.get("name", String.class)).isEqualTo(user.getName());
        }

        @Test
        @DisplayName("user가 null이면 IllegalArgumentException을 던진다")
        void givenNullUser_whenGenerateAccessToken_thenThrowsException() {
            // When & Then
            assertThatThrownBy(() -> jwtTokenProvider.generateAccessToken(null))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("generateRefreshToken(User) 메서드는")
    class GenerateRefreshTokenTest {
        @Test
        @DisplayName("정상적으로 refresh token을 생성하고 name 클레임은 포함하지 않는다")
        void givenValidUser_whenGenerateRefreshToken_thenNoNameClaim() {
            // Given & When
            String token = jwtTokenProvider.generateRefreshToken(user);
            // Then
            Claims claims = jwtTokenProvider.getClaims(token);
            assertThat(claims.getSubject()).isEqualTo(user.getUuid().getValue().toString());
            assertThat(claims.get("email")).isNull();
            assertThat(claims.get("name")).isNull();
        }

        @Test
        @DisplayName("user가 null이면 IllegalArgumentException을 던진다")
        void givenNullUser_whenGenerateRefreshToken_thenThrowsException() {
            // When & Then
            assertThatThrownBy(() -> jwtTokenProvider.generateRefreshToken(null))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("validateToken(String) 메서드는")
    class ValidateTokenTest {
        @Test
        @DisplayName("정상 토큰이면 true를 반환한다")
        void givenValidToken_whenValidateToken_thenReturnsTrue() {
            // Given
            String token = jwtTokenProvider.generateAccessToken(user);
            // When & Then
            assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        }

        @Test
        @DisplayName("빈 토큰이면 예외를 던진다")
        void givenEmptyToken_whenValidateToken_thenThrowsException() {
            // When & Then
            assertThatThrownBy(() -> jwtTokenProvider.validateToken(" "))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("getClaims(String) 메서드는")
    class GetClaimsTest {
        @Test
        @DisplayName("정상 토큰에서 클레임을 추출한다")
        void givenValidToken_whenGetClaims_thenReturnsClaims() {
            // Given
            String token = jwtTokenProvider.generateAccessToken(user);
            // When
            Claims claims = jwtTokenProvider.getClaims(token);
            // Then
            assertThat(claims.getSubject()).isEqualTo(user.getUuid().getValue().toString());
        }

        @Test
        @DisplayName("빈 토큰이면 예외를 던진다")
        void givenEmptyToken_whenGetClaims_thenThrowsException() {
            // When & Then
            assertThatThrownBy(() -> jwtTokenProvider.getClaims(" "))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("getUserId(String) 메서드는")
    class GetUserIdTest {
        @Test
        @DisplayName("정상 토큰에서 UserId를 추출한다")
        void givenValidToken_whenGetUserId_thenReturnsUserId() {
            // Given
            String token = jwtTokenProvider.generateAccessToken(user);
            // When
            UserId userId = jwtTokenProvider.getUserId(token);
            // Then
            assertThat(userId).isEqualTo(user.getUuid());
        }
    }

    @Nested
    @DisplayName("getExpiration(String) 메서드는")
    class GetExpirationTest {
        @Test
        @DisplayName("정상 토큰에서 만료일을 추출한다")
        void givenValidToken_whenGetExpiration_thenReturnsDate() {
            // Given
            String token = jwtTokenProvider.generateAccessToken(user);
            // When
            Date expiration = jwtTokenProvider.getExpiration(token);
            // Then
            assertThat(expiration).isNotNull();
        }
    }
}
