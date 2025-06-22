package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.role.Role;

import java.util.List;

/**
 * 역할 목록(전체) 조회 포트입니다.
 */
public interface FindRolesPort {
    /**
     * 전체 역할 목록을 조회합니다.
     *
     * @return Role 리스트
     */
    List<Role> findAllRoles();
}
