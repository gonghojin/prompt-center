package com.gongdel.promptserver.application.port.out.command;

import com.gongdel.promptserver.domain.userauth.UserAuthentication;

/**
 * 사용자 인증 정보를 저장하는 포트 인터페이스입니다.
 * 이 인터페이스는 헥사고날 아키텍처의 출력 포트로, 사용자 인증 정보의 영속성을 담당합니다.
 */
public interface SaveUserAuthenticationPort {
    /**
     * 사용자 인증 정보를 저장하고 저장된 인증 정보를 반환합니다.
     *
     * @param userAuthentication 저장할 사용자 인증 정보
     * @return 저장된 사용자 인증 정보
     */
    UserAuthentication saveUserAuthentication(UserAuthentication userAuthentication);
}
