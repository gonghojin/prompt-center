package com.gongdel.promptserver.adapter.out.persistence.repository;

import com.gongdel.promptserver.adapter.out.persistence.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 사용자-역할 매핑 JPA 리포지토리
 * <p>
 * user_role 테이블에 대한 CRUD 및 사용자/역할별 조회 기능을 제공합니다.
 * </p>
 */
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
    /**
     * 특정 사용자에 할당된 모든 역할 매핑을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return UserRoleEntity 리스트
     */
    List<UserRoleEntity> findByUserId(Long userId);

    /**
     * 특정 역할에 할당된 모든 사용자 매핑을 조회합니다.
     *
     * @param roleId 역할 ID
     * @return UserRoleEntity 리스트
     */
    List<UserRoleEntity> findByRoleId(Long roleId);

    /**
     * 특정 사용자와 역할의 매핑을 조회합니다.
     *
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     * @return UserRoleEntity Optional
     */
    Optional<UserRoleEntity> findByUserIdAndRoleId(Long userId, Long roleId);
}
