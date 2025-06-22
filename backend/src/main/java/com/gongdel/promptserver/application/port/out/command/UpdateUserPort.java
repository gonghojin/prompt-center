package com.gongdel.promptserver.application.port.out.command;

import com.gongdel.promptserver.domain.user.User;

/**
 * 사용자 정보 업데이트 포트 인터페이스입니다.
 *
 * <p>
 * 사용자 도메인 객체의 정보를 영속화 계층에서 수정하기 위한 포트입니다.
 * </p>
 */
public interface UpdateUserPort {
    /**
     * 사용자 정보를 수정합니다.
     *
     * @param user 수정할 사용자 도메인 객체
     * @return 수정된 사용자 도메인 객체
     */
    User updateUser(User user);
}
