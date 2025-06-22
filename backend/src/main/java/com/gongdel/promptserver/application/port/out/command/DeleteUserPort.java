package com.gongdel.promptserver.application.port.out.command;

import com.gongdel.promptserver.domain.user.UserId;

/**
 * 사용자 삭제 포트 인터페이스입니다.
 *
 * <p>
 * 사용자 도메인 객체를 영속화 계층에서 삭제하기 위한 포트입니다.
 * </p>
 */
public interface DeleteUserPort {
    /**
     * 사용자를 삭제합니다.
     *
     * @param userId 삭제할 사용자 식별자
     */
    void deleteUser(UserId userId);
}
