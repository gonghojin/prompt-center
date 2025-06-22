package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.mapper.RoleMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.RoleRepository;
import com.gongdel.promptserver.application.port.out.query.FindRolesPort;
import com.gongdel.promptserver.application.port.out.query.LoadRolePort;
import com.gongdel.promptserver.domain.role.Role;
import com.gongdel.promptserver.domain.role.RoleDomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

/**
 * 역할(Role) 엔티티의 조회를 담당하는 CQRS 어댑터입니다.
 * 역할 정보를 조회하는 모든 쿼리 작업을 처리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleQueryAdapter implements LoadRolePort, FindRolesPort {
    private final RoleRepository roleJpaRepository;
    private final RoleMapper roleMapper;

    /**
     * ID를 기반으로 역할을 조회합니다.
     *
     * @param id 조회할 역할의 ID
     * @return 조회된 역할 정보를 담은 Optional 객체
     * @throws RoleDomainException 데이터베이스 조회 중 오류 발생 시
     */
    @Override
    public Optional<Role> loadRoleById(Long id) {
        try {
            Assert.notNull(id, "Role ID must not be null");
            log.debug("Loading role by ID: {}", id);
            return roleJpaRepository.findById(id)
                .map(roleMapper::toDomain);
        } catch (DataAccessException e) {
            log.error("Database error while loading role with ID: {}", id, e);
            throw new RoleDomainException("Database error while loading role", e);
        } catch (Exception e) {
            log.error("Failed to load role by ID: {}", id, e);
            throw new RoleDomainException("Failed to load role by ID: " + id, e);
        }
    }

    /**
     * 이름을 기반으로 역할을 조회합니다.
     *
     * @param name 조회할 역할의 이름
     * @return 조회된 역할 정보를 담은 Optional 객체
     * @throws RoleDomainException 데이터베이스 조회 중 오류 발생 시
     */
    @Override
    public Optional<Role> loadRoleByName(String name) {
        try {
            Assert.hasText(name, "Role name must not be empty");
            log.debug("Loading role by name: {}", name);

            return roleJpaRepository.findByName(name)
                .map(roleMapper::toDomain);
        } catch (DataAccessException e) {
            log.error("Database error while loading role with name: {}", name, e);
            throw new RoleDomainException("Database error while loading role", e);
        } catch (Exception e) {
            log.error("Failed to load role by name: {}", name, e);
            throw new RoleDomainException("Failed to load role by name: " + name, e);
        }
    }

    /**
     * 모든 역할 목록을 조회합니다.
     *
     * @return 전체 역할 목록
     * @throws RoleDomainException 데이터베이스 조회 중 오류 발생 시
     */
    @Override
    public List<Role> findAllRoles() {
        log.debug("Finding all roles");

        try {
            List<Role> roles = roleJpaRepository.findAll()
                .stream()
                .map(roleMapper::toDomain)
                .toList();

            log.debug("Found {} roles", roles.size());
            return roles;
        } catch (DataAccessException e) {
            log.error("Database error while finding all roles", e);
            throw new RoleDomainException("Database error while finding roles", e);
        } catch (Exception e) {
            log.error("Failed to find all roles", e);
            throw new RoleDomainException("Failed to find all roles", e);
        }
    }
}
