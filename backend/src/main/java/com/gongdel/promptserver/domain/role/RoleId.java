package com.gongdel.promptserver.domain.role;

import java.util.Objects;
import java.util.UUID;

/**
 * 역할 식별자 값 객체입니다. UUID 기반으로 불변입니다.
 */
public final class RoleId {
    private final UUID value;

    public RoleId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("RoleId는 null일 수 없습니다.");
        }
        this.value = value;
    }

    public static RoleId randomId() {
        return new RoleId(UUID.randomUUID());
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
        RoleId roleId = (RoleId) o;
        return value.equals(roleId.value);
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
