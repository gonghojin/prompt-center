package com.gongdel.promptserver.adapter.out.persistence.repository;

import com.gongdel.promptserver.adapter.out.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String email);

    void deleteByUuid(UUID uuid);

    Optional<UserEntity> findByUuid(UUID uuid);

    /**
     * 특정 상태의 유저 수를 조회합니다.
     *
     * @param status 유저 상태
     * @return 해당 상태의 유저 수
     */
    long countByStatus(UserEntity.UserStatus status);

    /**
     * 특정 상태이며 특정 기간에 생성된 유저 수를 조회합니다.
     *
     * @param status    유저 상태
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜
     * @return 해당 조건의 유저 수
     */
    long countByStatusAndCreatedAtBetween(UserEntity.UserStatus status, LocalDateTime startDate, LocalDateTime endDate);
}
