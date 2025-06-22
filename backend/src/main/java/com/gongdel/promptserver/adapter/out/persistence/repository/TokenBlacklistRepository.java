package com.gongdel.promptserver.adapter.out.persistence.repository;

import com.gongdel.promptserver.adapter.out.persistence.entity.TokenBlacklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 토큰 블랙리스트 JPA 리포지토리
 * <p>
 * token_blacklist 테이블에 대한 CRUD 및 토큰별 조회 기능을 제공합니다.
 * </p>
 */
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklistEntity, Long> {
    /**
     * 토큰 ID(jti)로 블랙리스트 엔티티를 조회합니다.
     *
     * @param tokenId JWT 토큰 ID(jti)
     * @return Optional<TokenBlacklistEntity>
     */
    Optional<TokenBlacklistEntity> findByTokenId(String tokenId);

    /**
     * 해당 토큰 ID(jti)가 블랙리스트에 존재하는지 확인합니다.
     *
     * @param tokenId JWT 토큰 ID(jti)
     * @return 존재 여부
     */
    boolean existsByTokenId(String tokenId);
}
