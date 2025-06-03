package com.gongdel.promptserver.domain.user;

import com.gongdel.promptserver.domain.model.BaseTimeEntity;
import com.gongdel.promptserver.domain.team.Team;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 시스템 사용자를 나타내는 도메인 모델입니다. 사용자 식별 정보와 인증 정보, 역할을 포함합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    /**
     * DB PK (Long, JPA 식별자)
     */
    private Long id;
    /**
     * 비즈니스 식별자 (UUID)
     */
    private UserId uuid;
    private Email email;
    private String name;
    private Team team;
    private UserStatus status;

    /**
     * 사용자 객체를 생성합니다.
     *
     * @param id        DB PK(Long)
     * @param uuid      사용자 고유 식별자(UUID)
     * @param email     사용자 이메일 주소
     * @param name      사용자 이름
     * @param team      사용자 소속 팀
     * @param status    사용자 상태
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     */
    @Builder
    public User(
            Long id,
            UserId uuid,
            Email email,
            String name,
            Team team,
            UserStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.uuid = uuid != null ? uuid : UserId.randomId();
        this.email = email;
        this.name = name;
        this.team = team;
        this.status = status != null ? status : UserStatus.ACTIVE;
    }

    public void update(String name) {
        this.name = name;
        updateModifiedTime();
    }

    public void updateTeam(Team team) {
        this.team = team;
        updateModifiedTime();
    }

    public void updateStatus(UserStatus status) {
        this.status = status;
        updateModifiedTime();
    }

    public Long getId() {
        return id;
    }

    public UserId getUuid() {
        return uuid;
    }

    /**
     * User 최초 등록을 위한 정적 팩토리 메서드
     */
    public static User register(Email email, String name, Team team, UserStatus status, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new User(
                null, // id (DB에서 생성)
                null, // uuid (내부에서 생성)
                email,
                name,
                team,
                status,
                createdAt,
                updatedAt);
    }

    // Getter, equals, hashCode, toString 등 필요시 추가 구현
}
