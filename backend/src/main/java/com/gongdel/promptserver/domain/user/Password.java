package com.gongdel.promptserver.domain.user;

import java.util.Objects;

/**
 * 비밀번호 값 객체입니다. 불변이며, 생성 시 유효성 검사를 수행합니다.
 */
public class Password {
    private final String value;

    public Password(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        this.value = value;
    }

    public String toRaw() {
        return value;
    }

    public String toString() {
        return "********";
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Password password = (Password) o;
        return value.equals(password.value);
    }

    public int hashCode() {
        return Objects.hash(value);
    }
}
