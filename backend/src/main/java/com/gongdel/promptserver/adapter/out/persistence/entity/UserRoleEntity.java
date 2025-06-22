package com.gongdel.promptserver.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * 사용자-역할 매핑 JPA 엔티티
 * <p>
 * user_role 테이블과 매핑되며, 사용자와 역할 간의 다대다 관계를 관리합니다.
 * </p>
 */
@Entity
@Table(name = "user_role", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_role", columnNames = {"userId", "roleId"})
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserRoleEntity extends BaseJpaEntity {
    /**
     * 매핑 고유 식별자 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 매핑 UUID (글로벌 유일)
     */
    @Column(nullable = false, unique = true)
    private UUID uuid;

    /**
     * 사용자 ID (User Long FK)
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 역할 ID (Role Long FK)
     */
    @Column(nullable = false)
    private Long roleId;

    /**
     * 매퍼/테스트용 ID 기반 생성자
     */
    public UserRoleEntity(Long id) {
        this.id = id;
    }
}
