package com.gongdel.promptserver.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Visibility 열거형에 대한 단위 테스트
 */
class VisibilityTest {

    @Test
    @DisplayName("PUBLIC 상수는 '공개'라는 표시 이름을 가진다")
    void publicShouldHaveCorrectDisplayName() {
        // when
        String displayName = Visibility.PUBLIC.getDisplayName();

        // then
        assertThat(displayName).isEqualTo("공개");
    }

    @Test
    @DisplayName("PRIVATE 상수는 '비공개'라는 표시 이름을 가진다")
    void privateShouldHaveCorrectDisplayName() {
        // when
        String displayName = Visibility.PRIVATE.getDisplayName();

        // then
        assertThat(displayName).isEqualTo("비공개");
    }

    @Test
    @DisplayName("toString 메소드는 표시 이름을 반환한다")
    void toStringShouldReturnDisplayName() {
        // when & then
        assertThat(Visibility.PUBLIC.toString()).isEqualTo("공개");
        assertThat(Visibility.PRIVATE.toString()).isEqualTo("비공개");
    }

    @Test
    @DisplayName("isPublic 메소드는 PUBLIC 상수에 대해 true를 반환한다")
    void isPublicShouldReturnTrueForPublic() {
        // when & then
        assertThat(Visibility.PUBLIC.isPublic()).isTrue();
        assertThat(Visibility.PRIVATE.isPublic()).isFalse();
    }

    @Test
    @DisplayName("isPrivate 메소드는 PRIVATE 상수에 대해 true를 반환한다")
    void isPrivateShouldReturnTrueForPrivate() {
        // when & then
        assertThat(Visibility.PRIVATE.isPrivate()).isTrue();
        assertThat(Visibility.PUBLIC.isPrivate()).isFalse();
    }

    @Test
    @DisplayName("values 메소드는 모든 공개 범위를 포함한다")
    void valuesShouldContainAllVisibilities() {
        // when
        Visibility[] visibilities = Visibility.values();

        // then
        assertThat(visibilities).hasSize(3);
        assertThat(visibilities).contains(Visibility.PUBLIC, Visibility.PRIVATE, Visibility.TEAM);
    }

    @Test
    @DisplayName("valueOf 메소드는 이름으로 공개 범위를 조회할 수 있다")
    void valueOfShouldRetrieveVisibilityByName() {
        // when & then
        assertThat(Visibility.valueOf("PUBLIC")).isEqualTo(Visibility.PUBLIC);
        assertThat(Visibility.valueOf("PRIVATE")).isEqualTo(Visibility.PRIVATE);
    }
}
