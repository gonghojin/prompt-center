package com.gongdel.promptserver.application.port.out.query;

/**
 * 토큰 블랙리스트 조회를 위한 포트입니다.
 */
public interface CheckTokenBlacklistPort {
    /**
     * 해당 토큰이 블랙리스트에 등록되어 있는지 확인합니다.
     *
     * @param tokenId JWT 토큰 ID(jti)
     * @return 블랙리스트 등록 여부
     */
    boolean isBlacklisted(String tokenId);
}
