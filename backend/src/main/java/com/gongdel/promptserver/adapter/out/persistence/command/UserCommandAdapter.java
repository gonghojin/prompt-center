package com.gongdel.promptserver.adapter.out.persistence.command;

import com.gongdel.promptserver.adapter.out.persistence.entity.UserEntity;
import com.gongdel.promptserver.adapter.out.persistence.mapper.UserMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.UserJpaRepository;
import com.gongdel.promptserver.application.port.out.command.DeleteUserPort;
import com.gongdel.promptserver.application.port.out.command.SaveUserPort;
import com.gongdel.promptserver.application.port.out.command.UpdateUserPort;
import com.gongdel.promptserver.domain.exception.UserOperationException;
import com.gongdel.promptserver.domain.user.User;
import com.gongdel.promptserver.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 사용자 저장/수정/삭제를 담당하는 Command 어댑터입니다.
 *
 * <p>
 * 이 어댑터는 User 도메인 객체의 영속성 처리를 담당하며, JPA 및 매퍼를 통해 DB와 연동합니다.
 * 주요 기능:
 * - 사용자 정보 저장
 * - 사용자 정보 수정
 * - 사용자 정보 삭제
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class UserCommandAdapter implements SaveUserPort, UpdateUserPort, DeleteUserPort {
    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    /**
     * 사용자를 저장합니다.
     *
     * @param user 저장할 사용자 도메인 객체
     * @return 저장된 사용자 도메인 객체
     * @throws UserOperationException 저장 실패 시 발생
     */
    @Override
    public User saveUser(User user) {
        Assert.notNull(user, "User must not be null");
        log.debug("Starting user save operation for email: {}", user.getEmail());

        try {
            UserEntity entity = userMapper.toEntity(user);
            UserEntity saved = userJpaRepository.save(entity);
            log.info("User saved successfully - email: {}, id: {}", user.getEmail(), saved.getId());
            return userMapper.toDomain(saved);
        } catch (DataAccessException e) {
            String errorMessage = String.format("Failed to save user - email: %s, cause: %s",
                user.getEmail(), e.getMessage());
            log.error(errorMessage, e);
            throw new UserOperationException(errorMessage, e);
        }
    }

    /**
     * 사용자 정보를 수정합니다.
     *
     * @param user 수정할 사용자 도메인 객체
     * @return 수정된 사용자 도메인 객체
     * @throws UserOperationException 수정 실패 시 발생
     */
    @Override
    public User updateUser(User user) {
        Assert.notNull(user, "User must not be null");
        log.debug("Starting user update operation for email: {}", user.getEmail());

        try {
            UserEntity entity = userMapper.toEntity(user);
            UserEntity updated = userJpaRepository.save(entity);
            log.info("User updated successfully - email: {}, id: {}", user.getEmail(), updated.getId());
            return userMapper.toDomain(updated);
        } catch (DataAccessException e) {
            String errorMessage = String.format("Failed to update user - email: %s, cause: %s",
                user.getEmail(), e.getMessage());
            log.error(errorMessage, e);
            throw new UserOperationException(errorMessage, e);
        }
    }

    /**
     * 사용자를 삭제합니다.
     *
     * @param userId 삭제할 사용자 식별자
     * @throws UserOperationException 삭제 실패 시 발생
     */
    @Override
    public void deleteUser(UserId userId) {
        Assert.notNull(userId, "User ID must not be null");
        log.debug("Starting user deletion operation for id: {}", userId.getValue());

        try {
            userJpaRepository.deleteByUuid(userId.getValue());
            log.info("User deleted successfully - id: {}", userId.getValue());
        } catch (DataAccessException e) {
            String errorMessage = String.format("Failed to delete user - id: %s, cause: %s",
                userId.getValue(), e.getMessage());
            log.error(errorMessage, e);
            throw new UserOperationException(errorMessage, e);
        }
    }
}
