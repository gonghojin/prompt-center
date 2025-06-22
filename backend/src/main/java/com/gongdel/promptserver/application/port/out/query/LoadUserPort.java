package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.user.Email;
import com.gongdel.promptserver.domain.user.User;
import com.gongdel.promptserver.domain.user.UserId;

import java.util.Optional;

/**
 * 사용자 정보를 조회하는 포트입니다.
 * 이 포트는 사용자 정보를 다양한 식별자(UUID, 이메일, ID)로 조회하는 기능을 제공합니다.
 */
public interface LoadUserPort {
    /**
     * UUID로 사용자를 조회합니다.
     *
     * @param userId 사용자 UUID
     * @return 조회된 사용자 도메인 객체 (Optional)
     * @throws IllegalArgumentException userId가 null인 경우 발생
     */
    Optional<User> loadUserByUserId(UserId userId);

    /**
     * 이메일로 사용자를 조회합니다.
     *
     * @param email 사용자 이메일 값 객체
     * @return 조회된 사용자 도메인 객체 (Optional)
     * @throws IllegalArgumentException email이 null인 경우 발생
     */
    Optional<User> loadUserByEmail(Email email);

    /**
     * ID로 사용자를 조회합니다.
     *
     * @param id 사용자 ID
     * @return 조회된 사용자 도메인 객체 (Optional)
     * @throws IllegalArgumentException id가 null인 경우 발생
     */
    Optional<User> loadUserById(Long id);
}
