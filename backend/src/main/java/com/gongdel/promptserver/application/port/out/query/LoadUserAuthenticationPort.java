package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.userauth.UserAuthentication;

import java.util.Optional;

/**
 * 사용자 인증 정보를 userId로 조회하는 포트입니다.
 *
 * @author AI
 */
public interface LoadUserAuthenticationPort {
    /**
     * userId로 사용자 인증 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return Optional로 감싼 UserAuthentication 도메인 객체
     */
    Optional<UserAuthentication> loadUserAuthenticationByUserId(Long userId);
}
