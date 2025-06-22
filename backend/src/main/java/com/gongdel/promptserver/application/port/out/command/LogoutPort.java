package com.gongdel.promptserver.application.port.out.command;

import com.gongdel.promptserver.domain.logout.LogoutToken;

/**
 * 로그아웃 처리를 위한 포트입니다.
 * 토큰을 블랙리스트에 등록하고 관련 리소스를 정리합니다.
 */
public interface LogoutPort {
    /**
     * 로그아웃 처리를 수행합니다.
     * 토큰을 블랙리스트에 등록하고 관련 리소스를 정리합니다.
     *
     * @param logoutToken 로그아웃할 토큰 정보
     */
    void logout(LogoutToken logoutToken);
}
