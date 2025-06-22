package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.user.UserRole;

import java.util.List;
import java.util.Optional;

/**
 * 사용자-역할 매핑 정보를 조회하는 포트입니다.
 *
 * @author AI
 */
public interface UserRoleQueryPort {
    /**
     * 특정 사용자에 할당된 모든 역할 매핑을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return UserRole 리스트
     */
    List<UserRole> findUserRolesByUserId(Long userId);

    /**
     * 특정 역할에 할당된 모든 사용자 매핑을 조회합니다.
     *
     * @param roleId 역할 ID
     * @return UserRole 리스트
     */
    List<UserRole> findUserRolesByRoleId(Long roleId);

    /**
     * 특정 사용자와 역할의 매핑을 조회합니다.
     *
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     * @return UserRole Optional
     */
    Optional<UserRole> loadUserRoleByUserIdAndRoleId(Long userId, Long roleId);
}
