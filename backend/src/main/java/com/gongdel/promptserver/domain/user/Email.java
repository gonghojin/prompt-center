package com.gongdel.promptserver.domain.user;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 이메일 값 객체입니다. 불변이며, 생성 시 유효성 검사를 수행합니다.
 */
public final class Email {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private final String value;

    public Email(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다: " + value);
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Email email = (Email) o;
        return value.equalsIgnoreCase(email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value.toLowerCase());
    }

    @Override
    public String toString() {
        return value;
    }
}
