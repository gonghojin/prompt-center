package com.gongdel.promptserver.domain.user;

import java.util.Objects;
import java.util.UUID;

/**
 * 사용자 식별자 값 객체입니다. UUID 기반으로 불변입니다.
 */
public final class UserId {
    private final UUID value;

    public UserId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId는 null일 수 없습니다.");
        }
        this.value = value;
    }

    public static UserId randomId() {
        return new UserId(UUID.randomUUID());
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
        UserId userId = (UserId) o;
        return value.equals(userId.value);
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
