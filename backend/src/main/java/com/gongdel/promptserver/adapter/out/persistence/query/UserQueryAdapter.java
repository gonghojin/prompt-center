package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.mapper.UserMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.UserJpaRepository;
import com.gongdel.promptserver.application.port.out.query.LoadUserPort;
import com.gongdel.promptserver.domain.exception.UserOperationException;
import com.gongdel.promptserver.domain.user.Email;
import com.gongdel.promptserver.domain.user.User;
import com.gongdel.promptserver.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * 사용자 조회를 담당하는 Query 어댑터입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryAdapter implements LoadUserPort {
    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    /**
     * UUID로 사용자를 조회합니다.
     *
     * @param userId 사용자 UUID
     * @return 조회된 사용자 도메인 객체 (Optional)
     * @throws UserOperationException   조회 실패 시 발생
     * @throws IllegalArgumentException userId가 null인 경우 발생
     */
    @Override
    public Optional<User> loadUserByUserId(UserId userId) {
        Assert.notNull(userId, "userId must not be null");
        try {
            return userJpaRepository.findByUuid(userId.getValue())
                .map(userMapper::toDomain);
        } catch (DataAccessException e) {
            log.error("Failed to load user by uuid: {}", userId.getValue(), e);
            throw new UserOperationException("Failed to load user by uuid: " + userId.getValue(), e);
        } catch (Exception e) {
            log.error("Unexpected error while loading user by uuid: {}", userId.getValue(), e);
            throw new UserOperationException("Unexpected error while loading user by uuid: " + userId.getValue(), e);
        }
    }

    /**
     * 이메일로 사용자를 조회합니다.
     *
     * @param email 사용자 이메일 값 객체
     * @return 조회된 사용자 도메인 객체 (Optional)
     * @throws UserOperationException   조회 실패 시 발생
     * @throws IllegalArgumentException email이 null인 경우 발생
     */
    @Override
    public Optional<User> loadUserByEmail(Email email) {
        Assert.notNull(email, "email must not be null");
        try {
            return userJpaRepository.findByEmail(email.getValue())
                .map(userMapper::toDomain);
        } catch (DataAccessException e) {
            log.error("Failed to load user by email: {}", email.getValue(), e);
            throw new UserOperationException("Failed to load user by email: " + email.getValue(), e);
        } catch (Exception e) {
            log.error("Unexpected error while loading user by email: {}", email.getValue(), e);
            throw new UserOperationException("Unexpected error while loading user by email: " + email.getValue(), e);
        }
    }

    /**
     * ID로 사용자를 조회합니다.
     *
     * @param id 사용자 ID
     * @return 조회된 사용자 도메인 객체 (Optional)
     * @throws UserOperationException   조회 실패 시 발생
     * @throws IllegalArgumentException id가 null인 경우 발생
     */
    @Override
    public Optional<User> loadUserById(Long id) {
        Assert.notNull(id, "id must not be null");
        try {
            return userJpaRepository.findById(id)
                .map(userMapper::toDomain);
        } catch (DataAccessException e) {
            log.error("Failed to load user by id: {}", id, e);
            throw new UserOperationException("Failed to load user by id: " + id, e);
        } catch (Exception e) {
            log.error("Unexpected error while loading user by id: {}", id, e);
            throw new UserOperationException("Unexpected error while loading user by id: " + id, e);
        }
    }
}
