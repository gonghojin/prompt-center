package com.gongdel.promptserver.common.security;

import com.gongdel.promptserver.domain.exception.InvalidJwtException;
import com.gongdel.promptserver.domain.exception.JwtErrorType;
import com.gongdel.promptserver.domain.user.UserId;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 토큰 생성 및 검증을 담당하는 유틸리티 클래스입니다.
 * <p>
 * 액세스 토큰/리프레시 토큰 발급, 파싱, 만료 체크, 클레임 추출 기능을 제공합니다.
 * </p>
 */
@Slf4j
@Component
public class JwtTokenProvider {
    private final SecretKey key;
    private final long accessTokenValidityInMs;
    private final long refreshTokenValidityInMs;

    /**
     * JwtTokenProvider 생성자
     *
     * @param key                      JWT 서명용 SecretKey
     * @param accessTokenValidityInMs  액세스 토큰 유효기간(ms)
     * @param refreshTokenValidityInMs 리프레시 토큰 유효기간(ms)
     */
    public JwtTokenProvider(
            SecretKey key,
            @Qualifier("accessTokenValidityInMs") long accessTokenValidityInMs,
            @Qualifier("refreshTokenValidityInMs") long refreshTokenValidityInMs) {
        Assert.notNull(key, "SecretKey must not be null");
        Assert.isTrue(accessTokenValidityInMs > 0, "AccessToken validity must be positive");
        Assert.isTrue(refreshTokenValidityInMs > 0, "RefreshToken validity must be positive");

        this.key = key;
        this.accessTokenValidityInMs = accessTokenValidityInMs;
        this.refreshTokenValidityInMs = refreshTokenValidityInMs;

        log.info("JwtTokenProvider initialized with accessTokenValidity={}ms, refreshTokenValidity={}ms",
                accessTokenValidityInMs, refreshTokenValidityInMs);
    }

    /**
     * 사용자 정보를 기반으로 액세스 토큰을 생성합니다.
     *
     * @param user 사용자 객체
     * @return JWT 액세스 토큰
     * @throws IllegalArgumentException user가 null이거나, email/name이 비어있는 경우
     */
    public String generateAccessToken(com.gongdel.promptserver.domain.user.User user) {
        Assert.notNull(user, "User must not be null");
        Assert.notNull(user.getUuid(), "UserId must not be null");
        Assert.hasText(user.getEmail().getValue(), "Email must not be empty");
        Assert.hasText(user.getName(), "Name must not be empty");

        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidityInMs);

        String token = Jwts.builder()
            .setSubject(user.getUuid().getValue().toString())
            .claim("email", user.getEmail().getValue())
            .claim("name", user.getName())
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.debug("Generated access token for userId={}, email={}, name={}", user.getUuid(), user.getEmail().getValue(),
            user.getName());
        return token;
    }

    /**
     * 사용자 정보를 기반으로 리프레시 토큰을 생성합니다.
     *
     * @param user 사용자 객체
     * @return JWT 리프레시 토큰
     * @throws IllegalArgumentException user가 null인 경우
     */
    public String generateRefreshToken(com.gongdel.promptserver.domain.user.User user) {
        Assert.notNull(user, "User must not be null");
        Assert.notNull(user.getUuid(), "UserId must not be null");

        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenValidityInMs);

        String token = Jwts.builder()
            .setSubject(user.getUuid().getValue().toString())
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.debug("Generated refresh token for userId={}", user.getUuid());
        return token;
    }

    /**
     * 토큰의 유효성을 검증합니다.
     *
     * @param token JWT 토큰
     * @return 유효하면 true, 아니면 false
     * @throws InvalidJwtException 토큰이 유효하지 않은 경우
     */
    public boolean validateToken(String token) {
        Assert.hasText(token, "Token must not be empty");
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            log.debug("JWT token is valid");
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            throw new InvalidJwtException(JwtErrorType.EXPIRED_JWT.getMessage(), e);
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            throw new InvalidJwtException(JwtErrorType.UNSUPPORTED_JWT.getMessage(), e);
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
            throw new InvalidJwtException(JwtErrorType.MALFORMED_JWT.getMessage(), e);
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            throw new InvalidJwtException(JwtErrorType.JWT_SIGNATURE_ERROR.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.warn("JWT token is empty or null: {}", e.getMessage());
            throw new InvalidJwtException(JwtErrorType.INVALID_JWT.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during JWT validation: {}", e.getMessage(), e);
            throw new InvalidJwtException(JwtErrorType.INVALID_JWT.getMessage(), e);
        }
    }

    /**
     * 토큰에서 클레임(Claims)을 추출합니다.
     *
     * @param token JWT 토큰
     * @return Claims 객체
     * @throws InvalidJwtException 토큰이 유효하지 않거나 파싱에 실패한 경우
     */
    public Claims getClaims(String token) {
        Assert.hasText(token, "Token must not be empty");
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            log.debug("Extracted claims from token");
            return claims;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired while extracting claims: {}", e.getMessage());
            throw new InvalidJwtException(JwtErrorType.EXPIRED_JWT.getMessage(), e);
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token while extracting claims: {}", e.getMessage());
            throw new InvalidJwtException(JwtErrorType.UNSUPPORTED_JWT.getMessage(), e);
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token while extracting claims: {}", e.getMessage());
            throw new InvalidJwtException(JwtErrorType.MALFORMED_JWT.getMessage(), e);
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature while extracting claims: {}", e.getMessage());
            throw new InvalidJwtException(JwtErrorType.JWT_SIGNATURE_ERROR.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.warn("JWT token is empty or null while extracting claims: {}", e.getMessage());
            throw new InvalidJwtException(JwtErrorType.INVALID_JWT.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to extract claims from token: {}", e.getMessage(), e);
            throw new InvalidJwtException(JwtErrorType.INVALID_JWT.getMessage(), e);
        }
    }

    /**
     * 토큰에서 사용자 ID를 추출합니다.
     *
     * @param token JWT 토큰
     * @return 사용자 ID
     * @throws InvalidJwtException      토큰이 유효하지 않거나 파싱에 실패한 경우
     * @throws IllegalArgumentException token이 비어있는 경우
     */
    public UserId getUserId(String token) {
        Assert.hasText(token, "Token must not be empty");
        try {
            String uuidStr = getClaims(token).getSubject();
            UUID uuid = UUID.fromString(uuidStr);
            UserId userId = new UserId(uuid);
            log.debug("Extracted userId from token: {}", userId);
            return userId;
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format in token: {}", e.getMessage());
            throw new InvalidJwtException(JwtErrorType.INVALID_JWT.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to extract userId from token: {}", e.getMessage(), e);
            throw new InvalidJwtException(JwtErrorType.INVALID_JWT.getMessage(), e);
        }
    }

    /**
     * 토큰 만료일을 반환합니다.
     *
     * @param token JWT 토큰
     * @return 만료일(Date)
     * @throws InvalidJwtException 토큰이 유효하지 않거나 파싱에 실패한 경우
     */
    public Date getExpiration(String token) {
        Assert.hasText(token, "Token must not be empty");
        try {
            Date expiration = getClaims(token).getExpiration();
            log.debug("Extracted expiration from token: {}", expiration);
            return expiration;
        } catch (Exception e) {
            log.error("Failed to extract expiration from token: {}", e.getMessage(), e);
            throw new InvalidJwtException(JwtErrorType.INVALID_JWT.getMessage(), e);
        }
    }

    /**
     * JWT 토큰에서 토큰 ID(jti)를 추출합니다.
     *
     * @param token JWT 토큰
     * @return 토큰 ID
     * @throws InvalidJwtException 토큰이 유효하지 않거나 파싱에 실패한 경우
     */
    public String getTokenId(String token) {
        Assert.hasText(token, "Token must not be empty");
        try {
            String tokenId = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getId();
            log.debug("Extracted token ID: {}", tokenId);
            return tokenId;
        } catch (Exception e) {
            log.error("Failed to extract token ID: {}", e.getMessage(), e);
            throw new InvalidJwtException(JwtErrorType.INVALID_JWT.getMessage(), e);
        }
    }

    /**
     * JWT 토큰의 만료 시간을 LocalDateTime으로 반환합니다.
     *
     * @param token JWT 토큰
     * @return 만료 시간
     * @throws InvalidJwtException 토큰이 유효하지 않거나 파싱에 실패한 경우
     */
    public LocalDateTime getExpirationDate(String token) {
        Assert.hasText(token, "Token must not be empty");
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            LocalDateTime expirationDate = expiration.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            log.debug("Extracted expiration date: {}", expirationDate);
            return expirationDate;
        } catch (Exception e) {
            log.error("Failed to extract expiration date: {}", e.getMessage(), e);
            throw new InvalidJwtException(JwtErrorType.INVALID_JWT.getMessage(), e);
        }
    }
}
