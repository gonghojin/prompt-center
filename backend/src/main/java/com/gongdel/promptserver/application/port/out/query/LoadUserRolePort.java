package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.user.UserRole;

import java.util.Optional;

/**
 * 사용자-역할 매핑 단건 조회 포트입니다.
 */
public interface LoadUserRolePort {
    /**
     * 특정 사용자와 역할의 매핑을 조회합니다.
     *
     * @param userId 사용자 PK
     * @param roleId 역할 PK
     * @return UserRole Optional
     */
    Optional<UserRole> loadUserRoleByUserIdAndRoleId(Long userId, Long roleId);
}
