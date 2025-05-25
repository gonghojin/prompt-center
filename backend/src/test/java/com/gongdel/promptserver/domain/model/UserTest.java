package com.gongdel.promptserver.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
                .id(id)
                .email(email)
                .name(name)
                .password(password)
                .role(role)
                .build();

        // then
        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getRole()).isEqualTo(role);
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("role이 null이면 기본값으로 ROLE_USER가 설정된다")
    void createUserWithNullRole() {
        // given
        UUID id = UUID.randomUUID();
        String email = "test@example.com";
        String name = "테스트 사용자";
        String password = "password1234";

        // when
        User user = User.builder()
                .id(id)
                .email(email)
                .name(name)
                .password(password)
                .role(null)
                .build();

        // then
        assertThat(user.getRole()).isEqualTo(UserRole.ROLE_USER);
    }

    @Test
    @DisplayName("사용자 정보(이름, 비밀번호)를 업데이트할 수 있다")
    void updateUser() {
        // given
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .name("원래 이름")
                .password("원래 비밀번호")
                .role(UserRole.ROLE_USER)
                .build();

        LocalDateTime originalUpdatedAt = user.getUpdatedAt();

        // 약간의 시간 지연을 위해 잠시 대기
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // 무시
        }

        // when
        String newName = "새로운 이름";
        String newPassword = "새로운 비밀번호";
        user.update(newName, newPassword);

        // then
        assertThat(user.getName()).isEqualTo(newName);
        assertThat(user.getPassword()).isEqualTo(newPassword);
        assertThat(user.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    @DisplayName("사용자 역할을 업데이트할 수 있다")
    void updateUserRole() {
        // given
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .name("테스트 사용자")
                .password("password1234")
                .role(UserRole.ROLE_USER)
                .build();

        LocalDateTime originalUpdatedAt = user.getUpdatedAt();

        // 약간의 시간 지연을 위해 잠시 대기
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // 무시
        }

        // when
        user.updateRole(UserRole.ROLE_ADMIN);

        // then
        assertThat(user.getRole()).isEqualTo(UserRole.ROLE_ADMIN);
        assertThat(user.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    @DisplayName("toString 메서드는 비밀번호를 제외한 사용자 정보를 반환한다")
    void toStringExcludePassword() {
        // given
        UUID id = UUID.randomUUID();
        String email = "test@example.com";
        String name = "테스트 사용자";
        String password = "password1234";
        User user = User.builder()
                .id(id)
                .email(email)
                .name(name)
                .password(password)
                .role(UserRole.ROLE_USER)
                .build();

        // when
        String userString = user.toString();

        // then
        assertThat(userString).contains(id.toString());
        assertThat(userString).contains(email);
        assertThat(userString).contains(name);
        assertThat(userString).doesNotContain(password);
    }
}
