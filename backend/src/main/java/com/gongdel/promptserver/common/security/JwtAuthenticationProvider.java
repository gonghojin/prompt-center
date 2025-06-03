package com.gongdel.promptserver.common.security;

import com.gongdel.promptserver.application.port.out.query.LoadUserPort;
import com.gongdel.promptserver.domain.user.User;
import com.gongdel.promptserver.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collections;

/**
 * JWT 토큰에서 인증(Authentication) 객체를 생성하는 컴포넌트입니다.
 * <p>
 * JWT 토큰에서 사용자 ID를 추출하고, LoadUserPort로 사용자 정보를 조회하여
 * Spring Security의 Authentication 객체를 반환합니다.
 * 인증 성공/실패에 대해 로깅을 공통적으로 처리합니다.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider {
    private final JwtTokenProvider jwtTokenProvider;
    private final LoadUserPort loadUserPort;

    /**
     * JWT 토큰에서 인증(Authentication) 객체를 생성합니다.
     *
     * @param token JWT 토큰
     * @return 인증(Authentication) 객체
     * @throws IllegalArgumentException 인증 실패 시
     */
    public Authentication getAuthentication(String token) {
        Assert.hasText(token, "JWT token must not be empty");
        try {
            UserId userId = jwtTokenProvider.getUserId(token);
            User user = loadUserPort.loadUserByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // 기본 권한 설정
            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
            log.debug("Authentication created for userId={}", userId);

            return new UsernamePasswordAuthenticationToken(
                    new SecurityUserDetails(user, null, Collections.singletonList("ROLE_USER")),
                    null,
                    authorities);
        } catch (Exception ex) {
            log.warn("Failed to create authentication from JWT: {}", ex.getMessage());
            throw new IllegalArgumentException("Invalid JWT authentication: " + ex.getMessage(), ex);
        }
    }
}
