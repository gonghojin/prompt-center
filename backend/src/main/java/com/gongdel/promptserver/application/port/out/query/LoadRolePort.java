package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.role.Role;

import java.util.Optional;

/**
 * 역할 단건 조회 포트입니다.
 */
public interface LoadRolePort {
    /**
     * PK로 역할을 조회합니다.
     *
     * @param id 역할 PK
     * @return Role Optional
     */
    Optional<Role> loadRoleById(Long id);

    /**
     * 이름으로 역할을 조회합니다.
     *
     * @param name 역할 이름
     * @return Role Optional
     */
    Optional<Role> loadRoleByName(String name);
}
