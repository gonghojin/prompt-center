package com.gongdel.promptserver.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * 팀 정보를 저장하는 JPA 엔티티입니다.
 */
@Entity
@Table(name = "team", indexes = {
    @Index(name = "idx_team_uuid", columnList = "uuid", unique = true),
    @Index(name = "idx_team_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TeamEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamStatus status;

    /**
     * 팀 상태를 정의하는 열거형
     */
    public enum TeamStatus {
        ACTIVE, INACTIVE, DELETED
    }
}
