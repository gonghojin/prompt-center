package com.gongdel.promptserver.domain.team;

import com.gongdel.promptserver.domain.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 팀 정보를 나타내는 도메인 모델 클래스입니다. 사용자 그룹을 관리합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {

    private Long id;
    private TeamId uuid;
    private String name;
    private String description;
    private Set<User> members = new HashSet<>();
    private TeamStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 팀을 생성합니다.
     *
     * @param id          팀 ID
     * @param uuid        팀 UUID
     * @param name        팀 이름
     * @param description 팀 설명
     * @param status      팀 상태
     */
    public Team(Long id, TeamId uuid, String name, String description, TeamStatus status) {
        this.id = id;
        this.uuid = uuid != null ? uuid : TeamId.randomId();
        this.name = Objects.requireNonNull(name, "Team name cannot be null");
        this.description = description;
        this.status = status != null ? status : TeamStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 팀에 멤버를 추가합니다.
     *
     * @param user 추가할 사용자
     * @return 추가 성공 여부
     */
    public boolean addMember(User user) {
        if (user == null) {
            return false;
        }
        return members.add(user);
    }

    /**
     * 팀에서 멤버를 제거합니다.
     *
     * @param user 제거할 사용자
     * @return 제거 성공 여부
     */
    public boolean removeMember(User user) {
        return members.remove(user);
    }

    /**
     * 팀 정보를 업데이트합니다.
     *
     * @param name        변경할 팀 이름
     * @param description 변경할 팀 설명
     * @param status      변경할 팀 상태
     */
    public void update(String name, String description, TeamStatus status) {
        this.name = Objects.requireNonNull(name, "Team name cannot be null");
        this.description = description;
        this.status = status != null ? status : this.status;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 팀의 활성화 상태를 변경합니다.
     *
     * @param active 활성화 여부
     */
    public void setActive(boolean active) {
        this.status = active ? TeamStatus.ACTIVE : TeamStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 팀 상태를 나타내는 열거형
     */
    public enum TeamStatus {
        ACTIVE, INACTIVE
    }
}
