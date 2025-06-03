package com.gongdel.promptserver.common.security;

import com.gongdel.promptserver.application.port.out.query.CheckTokenBlacklistPort;
import com.gongdel.promptserver.application.usecase.query.TokenValidationService;
import com.gongdel.promptserver.domain.exception.InvalidJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * JWT 인증을 처리하는 필터입니다.
 * <p>
 * 인증 성공/실패에 대해 로깅만 처리합니다.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final TokenValidationService tokenValidationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = getJwtFromRequest(request);

            // 토큰이 있는 경우에만 인증 처리
            if (StringUtils.hasText(token)) {
                // 1. 토큰 검증 (형식, 서명, 블랙리스트)
                if (tokenValidationService.validateToken(token)) {
                    // 2. 인증 처리
                    Authentication authentication = jwtAuthenticationProvider.getAuthentication(token);
                    if (authentication instanceof UsernamePasswordAuthenticationToken) {
                        ((UsernamePasswordAuthenticationToken) authentication)
                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    }
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("JWT authentication successful for userId={}", jwtTokenProvider.getUserId(token));
                }
            }
        } catch (Exception ex) {
            // 인증 실패/예외 발생 시 SecurityContext clear 및 WARN 로그
            SecurityContextHolder.clearContext();
            log.warn("JWT authentication failed: {}", ex.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
