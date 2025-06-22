package com.gongdel.promptserver.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 리프레시 토큰 JPA 엔티티
 * <p>
 * refresh_token 테이블과 매핑되며, JWT 리프레시 토큰을 관리합니다.
 * </p>
 */
@Entity
@Table(name = "refresh_token", indexes = {
    @Index(name = "idx_refresh_token_user_id", columnList = "userId")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshTokenEntity extends BaseJpaEntity {
    /**
     * 토큰 고유 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자 ID(UUID)
     */
    @Column(nullable = false)
    private String userId;

    /**
     * 리프레시 토큰 문자열
     */
    @Column(nullable = false, length = 512)
    private String token;

    /**
     * 토큰 만료 일시
     */
    @Column(nullable = false)
    private LocalDateTime expiresAt;
}
