package com.gongdel.promptserver.application.port.out.command;

import com.gongdel.promptserver.domain.user.User;

/**
 * 사용자 저장 포트 인터페이스입니다.
 *
 * <p>
 * 사용자 도메인 객체를 영속화 계층에 저장하기 위한 포트입니다.
 * </p>
 */
public interface SaveUserPort {
    /**
     * 사용자를 저장합니다.
     *
     * @param user 저장할 사용자 도메인 객체
     * @return 저장된 사용자 도메인 객체
     */
    User saveUser(User user);
}
