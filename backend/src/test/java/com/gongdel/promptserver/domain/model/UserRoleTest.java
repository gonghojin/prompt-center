package com.gongdel.promptserver.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserRole 열거형 테스트
 */
class UserRoleTest {

    @Test
    @DisplayName("ROLE_USER는 '사용자'라는 표시 이름을 가진다")
    void roleUserShouldHaveCorrectDisplayName() {
        // when
        String displayName = UserRole.ROLE_USER.getDisplayName();

        // then
        assertThat(displayName).isEqualTo("사용자");
    }

    @Test
    @DisplayName("ROLE_ADMIN은 '관리자'라는 표시 이름을 가진다")
    void roleAdminShouldHaveCorrectDisplayName() {
        // when
        String displayName = UserRole.ROLE_ADMIN.getDisplayName();

        // then
        assertThat(displayName).isEqualTo("관리자");
    }

    @Test
    @DisplayName("toString()은 displayName을 반환한다")
    void toStringShouldReturnDisplayName() {
        // when & then
        assertThat(UserRole.ROLE_USER.toString()).isEqualTo("사용자");
        assertThat(UserRole.ROLE_ADMIN.toString()).isEqualTo("관리자");
    }

    @Test
    @DisplayName("values()는 모든 역할을 포함한다")
    void valuesShouldContainAllRoles() {
        // when
        UserRole[] roles = UserRole.values();

        // then
        assertThat(roles).hasSize(5);
        assertThat(roles).contains(UserRole.ROLE_USER, UserRole.ROLE_ADMIN, UserRole.ROLE_DESIGNER, UserRole.ROLE_DEVELOPER, UserRole.ROLE_DATA_SCIENTIST);
    }

    @Test
    @DisplayName("valueOf()는 이름으로 열거형 상수를 조회할 수 있다")
    void valueOfShouldRetrieveEnumConstant() {
        // when & then
        assertThat(UserRole.valueOf("ROLE_USER")).isEqualTo(UserRole.ROLE_USER);
        assertThat(UserRole.valueOf("ROLE_ADMIN")).isEqualTo(UserRole.ROLE_ADMIN);
    }
}
