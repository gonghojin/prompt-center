package com.gongdel.promptserver.domain.user;

import java.util.List;
import java.util.Optional;
import com.gongdel.promptserver.domain.role.RoleId;

/**
 * 사용자-역할 매핑 도메인 리포지토리
 * <p>
 * 도메인 계층에서 사용자-역할 매핑을 조회/저장하는 기능을 제공합니다.
 * </p>
 */
public interface UserRoleRepository {
    /**
     * 특정 사용자에 할당된 모든 역할 매핑을 조회합니다.
     *
     * @param userId 사용자 UserId VO
     * @return UserRole 리스트
     */
    List<UserRole> findByUserId(UserId userId);

    /**
     * 특정 역할에 할당된 모든 사용자 매핑을 조회합니다.
     *
     * @param roleId 역할 RoleId VO
     * @return UserRole 리스트
     */
    List<UserRole> findByRoleId(RoleId roleId);

    /**
     * 특정 사용자와 역할의 매핑을 조회합니다.
     *
     * @param userId 사용자 UserId VO
     * @param roleId 역할 RoleId VO
     * @return UserRole Optional
     */
    Optional<UserRole> findByUserIdAndRoleId(UserId userId, RoleId roleId);

    /**
     * 사용자-역할 매핑을 저장합니다.
     *
     * @param userRole UserRole 도메인 객체
     * @return 저장된 UserRole
     */
    UserRole save(UserRole userRole);
}
