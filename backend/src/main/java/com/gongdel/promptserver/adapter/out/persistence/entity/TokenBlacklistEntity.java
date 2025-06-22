package com.gongdel.promptserver.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 토큰 블랙리스트 JPA 엔티티
 * <p>
 * token_blacklist 테이블과 매핑되며, 로그아웃된 JWT 토큰을 관리합니다.
 * </p>
 */
@Entity
@Table(name = "token_blacklist", indexes = @Index(name = "idx_token_blacklist_token_id", columnList = "tokenId"))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TokenBlacklistEntity extends BaseJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String tokenId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private LocalDateTime expiresAt;
}
