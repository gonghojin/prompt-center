package com.gongdel.promptserver.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 시스템 사용자를 나타내는 도메인 모델입니다. 사용자 식별 정보와 인증 정보, 역할을 포함합니다.
 */
@Getter
@ToString(exclude = "password")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    private UUID id;
    private String email;
    private String name;
    private String password;
    private UserRole role;
    private Team team;
    private UserStatus status;
    private LocalDateTime lastLoginAt;

    /**
     * 사용자 객체를 생성합니다.
     *
     * @param id          사용자 고유 식별자
     * @param email       사용자 이메일 주소
     * @param name        사용자 이름
     * @param password    사용자 비밀번호 (암호화된 상태로 저장되어야 함)
     * @param role        사용자 역할
     * @param team        사용자 소속 팀
     * @param status      사용자 상태
     * @param lastLoginAt 마지막 로그인 일시
     * @param createdAt   생성 일시
     * @param updatedAt   수정 일시
     */
    @Builder
    public User(
        UUID id,
        String email,
        String name,
        String password,
        UserRole role,
        Team team,
        UserStatus status,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id != null ? id : UUID.randomUUID();
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role != null ? role : UserRole.ROLE_USER;
        this.team = team;
        this.status = status != null ? status : UserStatus.ACTIVE;
        this.lastLoginAt = lastLoginAt;
    }

    /**
     * 사용자 정보를 업데이트합니다. 이름과 비밀번호만 변경 가능하며, 업데이트 시간도 자동으로 갱신됩니다.
     *
     * @param name     변경할 이름
     * @param password 변경할 비밀번호
     */
    public void update(String name, String password) {
        this.name = name;
        this.password = password;
        updateModifiedTime();
    }

    /**
     * 사용자 역할을 변경합니다.
     *
     * @param role 변경할 역할
     */
    public void updateRole(UserRole role) {
        this.role = role;
        updateModifiedTime();
    }

    /**
     * 사용자 팀을 변경합니다.
     *
     * @param team 변경할 팀
     */
    public void updateTeam(Team team) {
        this.team = team;
        updateModifiedTime();
    }

    /**
     * 사용자 상태를 변경합니다.
     *
     * @param status 변경할 상태
     */
    public void updateStatus(UserStatus status) {
        this.status = status;
        updateModifiedTime();
    }

    /**
     * 마지막 로그인 시간을 현재 시간으로 업데이트합니다.
     */
    public void updateLastLoginTime() {
        this.lastLoginAt = LocalDateTime.now();
        updateModifiedTime();
    }

    /**
     * 사용자 상태를 나타내는 열거형
     */
    public enum UserStatus {
        ACTIVE, INACTIVE
    }
}
