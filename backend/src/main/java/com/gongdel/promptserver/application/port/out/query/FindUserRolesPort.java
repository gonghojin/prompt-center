package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.user.UserRole;

import java.util.List;

/**
 * 사용자-역할 매핑 목록(필터) 조회 포트입니다.
 */
public interface FindUserRolesPort {
    /**
     * 특정 사용자에 할당된 모든 역할 매핑을 조회합니다.
     *
     * @param userId 사용자 PK
     * @return UserRole 리스트
     */
    List<UserRole> findUserRolesByUserId(Long userId);

    /**
     * 특정 역할에 할당된 모든 사용자 매핑을 조회합니다.
     *
     * @param roleId 역할 PK
     * @return UserRole 리스트
     */
    List<UserRole> findUserRolesByRoleId(Long roleId);
}
