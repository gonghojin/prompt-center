package com.gongdel.promptserver.adapter.in.rest.response;

import com.gongdel.promptserver.domain.user.Email;
import com.gongdel.promptserver.domain.user.User;
import com.gongdel.promptserver.domain.user.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserResponse DTO 클래스에 대한 단위 테스트
 */
class UserResponseTest {

    @Test
    @DisplayName("User 도메인 객체로부터 UserResponse DTO를 생성할 수 있다")
    void fromShouldCreateDtoFromDomain() {
        // given
        UUID id = UUID.randomUUID();
        String email = "test@example.com";
        String name = "테스트 사용자";

        User user = User.builder()
            .uuid(new UserId(id))
            .email(new Email(email))
            .name(name)
            .build();

        // when
        UserResponse userResponse = UserResponse.from(user);

        // then
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getId()).isEqualTo(id);
        assertThat(userResponse.getEmail()).isEqualTo(email);
        assertThat(userResponse.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("UserResponse 빌더를 사용하여 DTO 객체를 생성할 수 있다")
    void builderShouldCreateDto() {
        // given
        UUID id = UUID.randomUUID();
        String email = "builder@example.com";
        String name = "빌더 테스트";

        // when
        UserResponse userResponse = UserResponse.builder()
            .id(id)
            .email(email)
            .name(name)
            .build();

        // then
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getId()).isEqualTo(id);
        assertThat(userResponse.getEmail()).isEqualTo(email);
        assertThat(userResponse.getName()).isEqualTo(name);
    }
}
