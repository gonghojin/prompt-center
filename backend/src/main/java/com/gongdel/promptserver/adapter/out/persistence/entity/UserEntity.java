package com.gongdel.promptserver.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * 사용자 정보를 저장하는 JPA 엔티티입니다.
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email", unique = true),
    @Index(name = "idx_user_team", columnList = "team_id"),
    @Index(name = "idx_user_uuid", columnList = "uuid", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
public class UserEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(name = "team_id")
    private Long teamId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    /**
     * 매퍼/테스트용 ID 기반 생성자
     */
    public UserEntity(Long id) {
        this.id = id;
    }

    /**
     * 사용자 상태를 정의하는 열거형
     */
    public enum UserStatus {
        ACTIVE, INACTIVE, DELETED
    }
}
