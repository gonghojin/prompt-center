package com.gongdel.promptserver.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryTest {

    @DisplayName("카테고리 생성 테스트")
    @Nested
    class CategoryCreationTest {

        @DisplayName("기본 생성자로 카테고리를 생성할 수 있다")
        @Test
        void createCategoryWithDefaultConstructor() {
            // given
            String name = "programming";
            String displayName = "프로그래밍";
            String description = "프로그래밍 관련 프롬프트";

            // when
            Category category = new Category(name, displayName, description);

            // then
            assertThat(category.getId()).isNull();
            assertThat(category.getName()).isEqualTo(name);
            assertThat(category.getDisplayName()).isEqualTo(displayName);
            assertThat(category.getDescription()).isEqualTo(description);
            assertThat(category.getParentCategory()).isNull();
            assertThat(category.isSystem()).isFalse();
            assertThat(category.getCreatedAt()).isNotNull();
            assertThat(category.getUpdatedAt()).isNotNull();
        }

        @DisplayName("시스템 카테고리로 생성할 수 있다")
        @Test
        void createSystemCategory() {
            // given
            String name = "system-category";
            String displayName = "시스템 카테고리";
            String description = "시스템에서 사용하는 카테고리";
            boolean isSystem = true;

            // when
            Category category = new Category(name, displayName, description, isSystem);

            // then
            assertThat(category.getId()).isNull();
            assertThat(category.getName()).isEqualTo(name);
            assertThat(category.getDisplayName()).isEqualTo(displayName);
            assertThat(category.getDescription()).isEqualTo(description);
            assertThat(category.getParentCategory()).isNull();
            assertThat(category.isSystem()).isTrue();
        }

        @DisplayName("하위 카테고리로 생성할 수 있다")
        @Test
        void createSubCategory() {
            // given
            Category parentCategory = new Category("parent", "상위 카테고리", "상위 카테고리 설명");
            String name = "sub-category";
            String displayName = "하위 카테고리";
            String description = "하위 카테고리 설명";

            // when
            Category subCategory = new Category(name, displayName, description, parentCategory);

            // then
            assertThat(subCategory.getId()).isNull();
            assertThat(subCategory.getName()).isEqualTo(name);
            assertThat(subCategory.getDisplayName()).isEqualTo(displayName);
            assertThat(subCategory.getDescription()).isEqualTo(description);
            assertThat(subCategory.getParentCategory()).isEqualTo(parentCategory);
            assertThat(subCategory.isSystem()).isFalse();
        }

        @DisplayName("전체 생성자로 카테고리를 생성할 수 있다")
        @Test
        void createCategoryWithFullConstructor() {
            // given
            Long id = 1L;
            String name = "full-category";
            String displayName = "전체 생성자 카테고리";
            String description = "전체 생성자로 생성된 카테고리";
            Category parentCategory = new Category("parent", "상위 카테고리", "상위 카테고리 설명");
            boolean isSystem = true;

            // when
            Category category = new Category(id, name, displayName, description, parentCategory, isSystem,
                LocalDateTime.now(), LocalDateTime.now());

            // then
            assertThat(category.getId()).isEqualTo(id);
            assertThat(category.getName()).isEqualTo(name);
            assertThat(category.getDisplayName()).isEqualTo(displayName);
            assertThat(category.getDescription()).isEqualTo(description);
            assertThat(category.getParentCategory()).isEqualTo(parentCategory);
            assertThat(category.isSystem()).isEqualTo(isSystem);
            assertThat(category.getCreatedAt()).isNotNull();
            assertThat(category.getUpdatedAt()).isNotNull();
        }
    }

    @DisplayName("카테고리 유효성 검사 테스트")
    @Nested
    class CategoryValidationTest {

        @DisplayName("이름이 null이거나 비어있으면 예외가 발생한다")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  "})
        void throwExceptionWhenNameIsNullOrEmpty(String invalidName) {
            // given
            String displayName = "유효한 표시 이름";

            // when & then
            assertThatThrownBy(() -> new Category(invalidName, displayName, "설명"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("카테고리 이름은 필수입니다.");
        }

        @DisplayName("표시 이름이 null이거나 비어있으면 예외가 발생한다")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  "})
        void throwExceptionWhenDisplayNameIsNullOrEmpty(String invalidDisplayName) {
            // given
            String name = "valid-name";

            // when & then
            assertThatThrownBy(() -> new Category(name, invalidDisplayName, "설명"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("카테고리 표시 이름은 필수입니다.");
        }
    }

    @DisplayName("카테고리 업데이트 테스트")
    @Nested
    class CategoryUpdateTest {

        @DisplayName("카테고리 정보를 업데이트할 수 있다")
        @Test
        void updateCategory() {
            // given
            Category category = new Category("original", "원본 카테고리", "원본 설명");
            Category parentCategory = new Category("parent", "상위 카테고리", "상위 카테고리 설명");
            String newDisplayName = "업데이트된 카테고리";
            String newDescription = "업데이트된 설명";
            LocalDateTime originalUpdatedAt = category.getUpdatedAt();

            // 시간 차이를 확인하기 위해 약간의 딜레이
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // when
            category.update(newDisplayName, newDescription, parentCategory);

            // then
            assertThat(category.getName()).isEqualTo("original"); // 이름은 변경되지 않음
            assertThat(category.getDisplayName()).isEqualTo(newDisplayName);
            assertThat(category.getDescription()).isEqualTo(newDescription);
            assertThat(category.getParentCategory()).isEqualTo(parentCategory);
            assertThat(category.getUpdatedAt()).isAfter(originalUpdatedAt);
        }

        @DisplayName("카테고리 업데이트 시 표시 이름이 유효하지 않으면 예외가 발생한다")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  "})
        void throwExceptionWhenUpdateWithInvalidDisplayName(String invalidDisplayName) {
            // given
            Category category = new Category("original", "원본 카테고리", "원본 설명");

            // when & then
            assertThatThrownBy(() -> category.update(invalidDisplayName, "새 설명", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("카테고리 표시 이름은 필수입니다.");
        }
    }

    @DisplayName("카테고리 동등성 테스트")
    @Nested
    class CategoryEqualityTest {

        @DisplayName("ID가 동일한 카테고리는 동등하다")
        @Test
        void categoriesWithSameIdAreEqual() {
            // given
            Long id = 1L;
            Category category1 = new Category(id, "name1", "카테고리1", "설명1", null, false, LocalDateTime.now(),
                LocalDateTime.now());
            Category category2 = new Category(id, "name2", "카테고리2", "설명2", null, true, LocalDateTime.now(),
                LocalDateTime.now());

            // when & then
            assertThat(category1).isEqualTo(category2);
            assertThat(category1.hashCode()).isEqualTo(category2.hashCode());
        }

        @DisplayName("ID가 다른 카테고리는 동등하지 않다")
        @Test
        void categoriesWithDifferentIdAreNotEqual() {
            // given
            Category category1 = new Category(1L, "name", "카테고리", "설명", null, false, LocalDateTime.now(),
                LocalDateTime.now());
            Category category2 = new Category(2L, "name", "카테고리", "설명", null, false, LocalDateTime.now(),
                LocalDateTime.now());

            // when & then
            assertThat(category1).isNotEqualTo(category2);
        }

        @DisplayName("하나의 카테고리가 ID가 null이면 동등하지 않다")
        @Test
        void categoriesWithOneNullIdAreNotEqual() {
            // given
            Category category1 = new Category(1L, "name", "카테고리", "설명", null, false, LocalDateTime.now(),
                LocalDateTime.now());
            Category category2 = new Category(null, "name", "카테고리", "설명", null, false, LocalDateTime.now(),
                LocalDateTime.now());

            // when & then
            assertThat(category1).isNotEqualTo(category2);
            assertThat(category2).isNotEqualTo(category1);
        }
    }

    @DisplayName("카테고리 문자열 표현 테스트")
    @Nested
    class CategoryToStringTest {

        @DisplayName("toString은 displayName을 반환한다")
        @Test
        void toStringReturnsDisplayName() {
            // given
            String displayName = "카테고리 표시 이름";
            Category category = new Category("name", displayName, "설명");

            // when & then
            assertThat(category.toString()).isEqualTo(displayName);
        }
    }
}
