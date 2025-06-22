package com.gongdel.promptserver.adapter.out.persistence.repository;

import com.gongdel.promptserver.adapter.out.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 리프레시 토큰 JPA 리포지토리
 * <p>
 * refresh_token 테이블에 대한 CRUD 및 사용자/토큰별 조회 기능을 제공합니다.
 * </p>
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    /**
     * 사용자 ID로 리프레시 토큰을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return Optional<RefreshTokenEntity>
     */
    Optional<RefreshTokenEntity> findByUserId(String userId);

    /**
     * 토큰 문자열로 리프레시 토큰을 조회합니다.
     *
     * @param token 리프레시 토큰 문자열
     * @return Optional<RefreshTokenEntity>
     */
    Optional<RefreshTokenEntity> findByToken(String token);

    /**
     * 사용자 ID로 리프레시 토큰을 삭제합니다.
     *
     * @param userId 사용자 ID
     */
    void deleteByUserId(String userId);

    /**
     * 토큰 값으로 리프레시 토큰을 삭제합니다.
     *
     * @param token 토큰 값
     */
    void deleteByToken(String token);
}
