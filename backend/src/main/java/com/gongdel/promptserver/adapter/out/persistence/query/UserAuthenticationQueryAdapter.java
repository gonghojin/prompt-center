package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.mapper.UserAuthenticationMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.UserAuthenticationRepository;
import com.gongdel.promptserver.application.port.out.query.LoadUserAuthenticationPort;
import com.gongdel.promptserver.domain.exception.UserOperationException;
import com.gongdel.promptserver.domain.userauth.UserAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * 사용자 인증 정보를 조회하는 Query 어댑터입니다.
 * 이 어댑터는 사용자 인증 정보를 조회하는 기능을 제공하며,
 * 영속성 계층과 도메인 계층 사이의 데이터 변환을 담당합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAuthenticationQueryAdapter implements LoadUserAuthenticationPort {
    private final UserAuthenticationRepository userAuthJpaRepository;
    private final UserAuthenticationMapper userAuthMapper;

    /**
     * 사용자 ID를 기반으로 사용자 인증 정보를 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 조회된 사용자 인증 정보 (Optional)
     * @throws UserOperationException   조회 실패 시 발생
     * @throws IllegalArgumentException userId가 null인 경우 발생
     */
    @Override
    public Optional<UserAuthentication> loadUserAuthenticationByUserId(Long userId) {
        Assert.notNull(userId, "userId must not be null");
        try {
            return userAuthJpaRepository.findByUserId(userId)
                .map(userAuthMapper::toDomain);
        } catch (DataAccessException e) {
            log.error("Failed to load user authentication by userId: {}", userId, e);
            throw new UserOperationException("Failed to load user authentication by userId: " + userId, e);
        } catch (Exception e) {
            log.error("Unexpected error while loading user authentication by userId: {}", userId, e);
            throw new UserOperationException("Unexpected error while loading user authentication by userId: " + userId,
                e);
        }
    }
}
