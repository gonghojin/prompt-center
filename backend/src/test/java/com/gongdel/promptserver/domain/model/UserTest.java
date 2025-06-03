package com.gongdel.promptserver.domain.model;

import com.gongdel.promptserver.domain.user.Email;
import com.gongdel.promptserver.domain.user.User;
import com.gongdel.promptserver.domain.user.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User 도메인 모델 테스트
 */
class UserTest {

    @Test
    @DisplayName("Builder를 사용하여 User 객체를 생성할 수 있다")
    void createUserWithBuilder() {
        // given
        UUID id = UUID.randomUUID();
        String email = "test@example.com";
        String name = "테스트 사용자";
        String password = "password1234";
        UserRole role = UserRole.ROLE_USER;

        // when
        User user = User.builder()
            .uuid(new UserId(id))
            .email(new Email(email))
            .name(name)
            .build();

        // then
        assertThat(user.getUuid().getValue()).isEqualTo(id);
        assertThat(user.getEmail().getValue()).isEqualTo(email);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
    }
}
