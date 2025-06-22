package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.mapper.UserRoleMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.UserRoleRepository;
import com.gongdel.promptserver.application.port.out.query.FindUserRolesPort;
import com.gongdel.promptserver.application.port.out.query.LoadUserRolePort;
import com.gongdel.promptserver.domain.user.UserRole;
import com.gongdel.promptserver.domain.user.UserRoleDomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 사용자-역할 매핑 조회를 위한 CQRS 어댑터입니다.
 * 사용자와 역할 간의 관계를 조회하는 기능을 제공합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRoleQueryAdapter implements LoadUserRolePort, FindUserRolesPort {
    private static final String ERROR_LOAD_USER_ROLE = "Failed to load user role for userId: %d and roleId: %d";
    private static final String ERROR_UNEXPECTED_LOAD = "Unexpected error while loading user role for userId: %d and roleId: %d";
    private static final String ERROR_FIND_ROLES = "Failed to find roles for userId: %d";
    private static final String ERROR_UNEXPECTED_FIND_ROLES = "Unexpected error while finding roles for userId: %d";
    private static final String ERROR_FIND_USERS = "Failed to find users for roleId: %d";
    private static final String ERROR_UNEXPECTED_FIND_USERS = "Unexpected error while finding users for roleId: %d";

    private final UserRoleRepository userRoleJpaRepository;
    private final UserRoleMapper userRoleMapper;

    /**
     * 특정 사용자와 역할에 대한 매핑 정보를 조회합니다.
     *
     * @param userId 조회할 사용자 ID
     * @param roleId 조회할 역할 ID
     * @return 사용자-역할 매핑 정보 (Optional)
     * @throws UserRoleDomainException  사용자-역할 조회 중 도메인 예외가 발생한 경우
     * @throws IllegalArgumentException userId 또는 roleId가 null이거나 유효하지 않은 경우
     */
    @Override
    public Optional<UserRole> loadUserRoleByUserIdAndRoleId(Long userId, Long roleId) {
        log.debug("Attempting to load user role for userId: {} and roleId: {}", userId, roleId);
        Assert.notNull(userId, "userId must not be null");
        Assert.notNull(roleId, "roleId must not be null");

        try {
            return userRoleJpaRepository.findByUserIdAndRoleId(userId, roleId)
                .map(userRoleMapper::toDomain);
        } catch (DataAccessException e) {
            String errorMessage = String.format(ERROR_LOAD_USER_ROLE, userId, roleId);
            log.error(errorMessage, e);
            throw new UserRoleDomainException(errorMessage, e);
        } catch (Exception e) {
            String errorMessage = String.format(ERROR_UNEXPECTED_LOAD, userId, roleId);
            log.error(errorMessage, e);
            throw new UserRoleDomainException(errorMessage, e);
        }
    }

    /**
     * 특정 사용자에게 할당된 모든 역할을 조회합니다.
     *
     * @param userId 조회할 사용자 ID
     * @return 사용자에게 할당된 역할 목록
     * @throws UserRoleDomainException  사용자-역할 조회 중 도메인 예외가 발생한 경우
     * @throws IllegalArgumentException userId가 null이거나 유효하지 않은 경우
     */
    @Override
    public List<UserRole> findUserRolesByUserId(Long userId) {
        log.debug("Attempting to find all roles for userId: {}", userId);
        Assert.notNull(userId, "userId must not be null");

        try {
            return userRoleJpaRepository.findByUserId(userId)
                .stream()
                .map(userRoleMapper::toDomain)
                .collect(Collectors.toList());
        } catch (DataAccessException e) {
            String errorMessage = String.format(ERROR_FIND_ROLES, userId);
            log.error(errorMessage, e);
            throw new UserRoleDomainException(errorMessage, e);
        } catch (Exception e) {
            String errorMessage = String.format(ERROR_UNEXPECTED_FIND_ROLES, userId);
            log.error(errorMessage, e);
            throw new UserRoleDomainException(errorMessage, e);
        }
    }

    /**
     * 특정 역할이 할당된 모든 사용자를 조회합니다.
     *
     * @param roleId 조회할 역할 ID
     * @return 해당 역할이 할당된 사용자-역할 매핑 목록
     * @throws UserRoleDomainException  사용자-역할 조회 중 도메인 예외가 발생한 경우
     * @throws IllegalArgumentException roleId가 null이거나 유효하지 않은 경우
     */
    @Override
    public List<UserRole> findUserRolesByRoleId(Long roleId) {
        log.debug("Attempting to find all users with roleId: {}", roleId);
        Assert.notNull(roleId, "roleId must not be null");

        try {
            return userRoleJpaRepository.findByRoleId(roleId)
                .stream()
                .map(userRoleMapper::toDomain)
                .collect(Collectors.toList());
        } catch (DataAccessException e) {
            String errorMessage = String.format(ERROR_FIND_USERS, roleId);
            log.error(errorMessage, e);
            throw new UserRoleDomainException(errorMessage, e);
        } catch (Exception e) {
            String errorMessage = String.format(ERROR_UNEXPECTED_FIND_USERS, roleId);
            log.error(errorMessage, e);
            throw new UserRoleDomainException(errorMessage, e);
        }
    }
}
