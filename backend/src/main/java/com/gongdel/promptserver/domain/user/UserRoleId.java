package com.gongdel.promptserver.domain.user;

import java.util.Objects;
import java.util.UUID;

/**
 * 사용자-역할 매핑 식별자 값 객체입니다. UUID 기반으로 불변입니다.
 */
public final class UserRoleId {
    private final UUID value;

    public UserRoleId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserRoleId는 null일 수 없습니다.");
        }
        this.value = value;
    }

    public static UserRoleId randomId() {
        return new UserRoleId(UUID.randomUUID());
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
        UserRoleId userRoleId = (UserRoleId) o;
        return value.equals(userRoleId.value);
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
