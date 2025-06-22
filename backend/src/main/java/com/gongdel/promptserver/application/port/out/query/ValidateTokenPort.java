package com.gongdel.promptserver.application.port.out.query;

/**
 * JWT 토큰 유효성 검증 및 사용자 ID 추출을 위한 아웃바운드 포트 인터페이스입니다.
 */
public interface ValidateTokenPort {
    /**
     * JWT 토큰의 유효성을 검증합니다.
     *
     * @param token 검증할 JWT 토큰
     * @return 유효하면 true, 아니면 false
     */
    boolean validateToken(String token);

    /**
     * JWT 토큰에서 사용자 ID를 추출합니다.
     *
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    String getUserIdFromToken(String token);
}
