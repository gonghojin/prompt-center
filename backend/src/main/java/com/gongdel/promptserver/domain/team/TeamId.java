package com.gongdel.promptserver.domain.team;

import java.util.Objects;
import java.util.UUID;

/**
 * 팀 식별자 값 객체입니다. UUID 기반으로 불변입니다.
 */
public final class TeamId {
    private final UUID value;

    public TeamId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("TeamId는 null일 수 없습니다.");
        }
        this.value = value;
    }

    public static TeamId randomId() {
        return new TeamId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TeamId teamId = (TeamId) o;
        return value.equals(teamId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
