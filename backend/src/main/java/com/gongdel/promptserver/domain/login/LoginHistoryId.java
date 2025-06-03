package com.gongdel.promptserver.domain.login;

import lombok.Value;
import java.util.UUID;

/**
 * 로그인 이력의 식별자를 나타내는 값 객체입니다.
 */
@Value
public class LoginHistoryId {
    UUID value;

    public LoginHistoryId(UUID value) {
        this.value = value;
    }

    public static LoginHistoryId randomId() {
        return new LoginHistoryId(UUID.randomUUID());
    }
}
