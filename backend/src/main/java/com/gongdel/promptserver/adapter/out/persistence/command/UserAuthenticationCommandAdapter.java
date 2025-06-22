package com.gongdel.promptserver.adapter.out.persistence.command;

import com.gongdel.promptserver.adapter.out.persistence.mapper.UserAuthenticationMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.UserAuthenticationRepository;
import com.gongdel.promptserver.application.port.out.command.SaveUserAuthenticationPort;
import com.gongdel.promptserver.domain.userauth.UserAuthentication;
import com.gongdel.promptserver.domain.userauth.UserAuthenticationDomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 사용자 인증 정보를 영속성 계층에 저장하는 어댑터입니다.
 * 이 클래스는 SaveUserAuthenticationPort 인터페이스의 구현체로,
 * 도메인 객체를 영속성 계층의 엔티티로 변환하여 저장합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserAuthenticationCommandAdapter implements SaveUserAuthenticationPort {
    private final UserAuthenticationRepository userAuthJpaRepository;
    private final UserAuthenticationMapper userAuthMapper;

    /**
     * 사용자 인증 정보를 저장하고 저장된 정보를 반환합니다.
     * 이 메서드는 도메인 객체를 엔티티로 변환하여 저장한 후,
     * 다시 도메인 객체로 변환하여 반환합니다.
     *
     * @param userAuthentication 저장할 사용자 인증 정보
     * @return 저장된 사용자 인증 정보
     * @throws IllegalArgumentException          userAuthentication이 null이거나 userId가
     *                                           null인 경우
     * @throws UserAuthenticationDomainException 데이터베이스 작업 중 오류가 발생한 경우
     */
    @Override
    @Transactional
    public UserAuthentication saveUserAuthentication(UserAuthentication userAuthentication) {
        Assert.notNull(userAuthentication, "UserAuthentication must not be null");
        final Long userId = userAuthentication.getUserId();

        try {
            return userAuthMapper.toDomain(
                userAuthJpaRepository.save(
                    userAuthMapper.toEntity(userAuthentication)));
        } catch (DataAccessException e) {
            log.error("Failed to save user authentication for user: {}. Error: {}", userId, e.getMessage(), e);
            throw new UserAuthenticationDomainException(
                String.format("Failed to save user authentication for user: %d", userId),
                e);
        } catch (Exception e) {
            log.error("Unexpected error while saving user authentication for user: {}. Error: {}", userId,
                e.getMessage(), e);
            throw new UserAuthenticationDomainException(
                String.format("Unexpected error while saving user authentication for user: %d", userId),
                e);
        }
    }
}
