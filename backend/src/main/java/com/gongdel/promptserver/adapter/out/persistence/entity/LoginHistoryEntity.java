package com.gongdel.promptserver.adapter.out.persistence.entity;

import com.gongdel.promptserver.domain.login.LoginHistory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 사용자의 로그인 이력을 저장하는 JPA 엔티티입니다.
 */
@Entity
@Table(name = "login_history", indexes = {
    @Index(name = "idx_login_history_user", columnList = "user_id"),
    @Index(name = "idx_login_history_login_at", columnList = "login_at"),
    @Index(name = "idx_login_history_uuid", columnList = "uuid", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
public class LoginHistoryEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "login_at", nullable = false)
    private LocalDateTime loginAt;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginHistory.LoginStatus status;

    /**
     * 매퍼/테스트용 ID 기반 생성자
     */
    public LoginHistoryEntity(Long id) {
        this.id = id;
    }
}
