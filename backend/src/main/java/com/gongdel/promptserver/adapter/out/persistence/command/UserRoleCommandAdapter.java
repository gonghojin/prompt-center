package com.gongdel.promptserver.adapter.out.persistence.command;

import com.gongdel.promptserver.adapter.out.persistence.entity.UserRoleEntity;
import com.gongdel.promptserver.adapter.out.persistence.mapper.UserRoleMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.UserRoleRepository;
import com.gongdel.promptserver.application.port.out.command.SaveUserRolePort;
import com.gongdel.promptserver.domain.user.UserRole;
import com.gongdel.promptserver.domain.user.UserRoleDomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 사용자 역할 정보를 저장하는 어댑터 클래스입니다.
 * 영속성 계층과 도메인 계층 간의 데이터 변환을 담당합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class UserRoleCommandAdapter implements SaveUserRolePort {
    private static final String ERROR_INVALID_DATA = "Invalid user role data: %s";
    private static final String ERROR_DATABASE = "Failed to save user role due to database error";
    private static final String ERROR_UNEXPECTED = "Unexpected error occurred while saving user role";

    private final UserRoleRepository userRoleJpaRepository;
    private final UserRoleMapper userRoleMapper;

    /**
     * 사용자 역할 정보를 저장합니다.
     *
     * @param userRole 저장할 사용자 역할 정보
     * @return 저장된 사용자 역할 정보
     * @throws UserRoleDomainException 사용자 역할 정보가 유효하지 않거나 저장 중 오류가 발생한 경우
     */
    @Override
    public UserRole saveUserRole(UserRole userRole) {
        try {
            validateUserRole(userRole);
            log.debug("Attempting to save user role: {}", userRole);
            return saveAndMapUserRole(userRole);
        } catch (IllegalArgumentException e) {
            log.error(String.format(ERROR_INVALID_DATA, e.getMessage()));
            throw new UserRoleDomainException(String.format(ERROR_INVALID_DATA, e.getMessage()), e);
        } catch (DataAccessException e) {
            log.error(ERROR_DATABASE, e);
            throw new UserRoleDomainException(ERROR_DATABASE, e);
        } catch (Exception e) {
            log.error(ERROR_UNEXPECTED, e);
            throw new UserRoleDomainException(ERROR_UNEXPECTED, e);
        }
    }

    /**
     * 사용자 역할 정보의 유효성을 검증합니다.
     *
     * @param userRole 검증할 사용자 역할 정보
     * @throws IllegalArgumentException 유효성 검증 실패 시
     */
    private void validateUserRole(UserRole userRole) {
        Assert.notNull(userRole, "User role must not be null");
    }

    /**
     * 사용자 역할 정보를 저장하고 도메인 객체로 변환합니다.
     *
     * @param userRole 저장할 사용자 역할 정보
     * @return 저장된 사용자 역할 정보
     */
    private UserRole saveAndMapUserRole(UserRole userRole) {
        UserRoleEntity entity = userRoleMapper.toEntity(userRole);
        log.debug("Converted to entity: {}", entity);

        UserRoleEntity saved = userRoleJpaRepository.save(entity);
        log.debug("Successfully saved entity: {}", saved);

        UserRole savedUserRole = userRoleMapper.toDomain(saved);
        log.info("User role saved successfully for user: {}", savedUserRole.getUserId());

        return savedUserRole;
    }
}
