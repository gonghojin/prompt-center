package com.gongdel.promptserver.application.usecase.command;

import com.gongdel.promptserver.application.exception.CategoryDuplicateNameException;
import com.gongdel.promptserver.application.exception.CategoryNotFoundException;
import com.gongdel.promptserver.application.exception.CategoryOperationFailedException;
import com.gongdel.promptserver.application.port.in.command.CreateCategoryCommand;
import com.gongdel.promptserver.application.port.in.command.UpdateCategoryCommand;
import com.gongdel.promptserver.application.port.out.command.DeleteCategoryPort;
import com.gongdel.promptserver.application.port.out.command.SaveCategoryPort;
import com.gongdel.promptserver.application.port.out.command.UpdateCategoryPort;
import com.gongdel.promptserver.application.port.out.query.LoadCategoryPort;
import com.gongdel.promptserver.domain.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryCommandServiceTest {

    @Mock
    private SaveCategoryPort saveCategoryPort;

    @Mock
    private UpdateCategoryPort updateCategoryPort;

    @Mock
    private DeleteCategoryPort deleteCategoryPort;

    @Mock
    private LoadCategoryPort loadCategoryPort;

    @InjectMocks
    private CategoryCommandService categoryCommandService;

    @Nested
    @DisplayName("카테고리 생성 테스트")
    class CreateCategoryTest {

        @Test
        @DisplayName("유효한 명령으로 카테고리를 생성할 수 있다")
        void createCategoryWithValidCommand() {
            // given
            CreateCategoryCommand command = CreateCategoryCommand.create(
                "programming",
                "프로그래밍",
                "프로그래밍 관련 카테고리");

            Category expected = new Category(
                1L,
                "programming",
                "프로그래밍",
                "프로그래밍 관련 카테고리",
                null,
                false,
                null,
                null);

            given(loadCategoryPort.loadCategoryByName(anyString())).willReturn(Optional.empty());
            given(saveCategoryPort.saveCategory(any(Category.class))).willReturn(expected);

            // when
            Category result = categoryCommandService.createCategory(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("programming");
            assertThat(result.getDisplayName()).isEqualTo("프로그래밍");
            verify(loadCategoryPort).loadCategoryByName("programming");
            verify(saveCategoryPort).saveCategory(any(Category.class));
        }

        @Test
        @DisplayName("시스템 카테고리를 생성할 수 있다")
        void createSystemCategory() {
            // given
            CreateCategoryCommand command = CreateCategoryCommand.createSystemCategory(
                "system",
                "시스템",
                "시스템 카테고리");

            Category expected = new Category(
                1L,
                "system",
                "시스템",
                "시스템 카테고리",
                null,
                true,
                null,
                null);

            given(loadCategoryPort.loadCategoryByName(anyString())).willReturn(Optional.empty());
            given(saveCategoryPort.saveCategory(any(Category.class))).willReturn(expected);

            // when
            Category result = categoryCommandService.createCategory(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.isSystem()).isTrue();
        }

        @Test
        @DisplayName("상위 카테고리를 가진 카테고리를 생성할 수 있다")
        void createSubCategory() {
            // given
            Category parentCategory = new Category(
                1L,
                "parent",
                "상위 카테고리",
                "상위 카테고리 설명",
                null,
                false,
                null,
                null);

            CreateCategoryCommand command = CreateCategoryCommand.createSubCategory(
                "sub",
                "하위 카테고리",
                "하위 카테고리 설명",
                1L);

            Category expected = new Category(
                2L,
                "sub",
                "하위 카테고리",
                "하위 카테고리 설명",
                parentCategory,
                false,
                null,
                null);

            given(loadCategoryPort.loadCategoryByName(anyString())).willReturn(Optional.empty());
            given(loadCategoryPort.loadCategoryById(1L)).willReturn(Optional.of(parentCategory));
            given(saveCategoryPort.saveCategory(any(Category.class))).willReturn(expected);

            // when
            Category result = categoryCommandService.createCategory(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(2L);
            assertThat(result.getParentCategory()).isEqualTo(parentCategory);
        }

        @Test
        @DisplayName("중복된 이름으로 카테고리 생성 시 예외가 발생한다")
        void throwExceptionWhenNameDuplicated() {
            // given
            CreateCategoryCommand command = CreateCategoryCommand.create(
                "duplicate",
                "중복 카테고리",
                "중복 테스트");

            Category existing = new Category(
                1L,
                "duplicate",
                "이미 존재하는 카테고리",
                "기존 카테고리",
                null,
                false,
                null,
                null);

            given(loadCategoryPort.loadCategoryByName("duplicate")).willReturn(Optional.of(existing));

            // when & then
            assertThatThrownBy(() -> categoryCommandService.createCategory(command))
                .isInstanceOf(CategoryDuplicateNameException.class)
                .hasMessageContaining("duplicate");
        }

        @Test
        @DisplayName("상위 카테고리가 존재하지 않을 경우 예외가 발생한다")
        void throwExceptionWhenParentCategoryNotFound() {
            // given
            CreateCategoryCommand command = CreateCategoryCommand.createSubCategory(
                "sub",
                "하위 카테고리",
                "하위 카테고리 설명",
                999L);

            given(loadCategoryPort.loadCategoryByName(anyString())).willReturn(Optional.empty());
            given(loadCategoryPort.loadCategoryById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> categoryCommandService.createCategory(command))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("이름이 'sub'인 카테고리를 찾을 수 없습니다");

            verify(loadCategoryPort).loadCategoryByName("sub");
            verify(loadCategoryPort).loadCategoryById(999L);
        }
    }

    @Nested
    @DisplayName("카테고리 업데이트 테스트")
    class UpdateCategoryTest {

        @Test
        @DisplayName("유효한 명령으로 카테고리를 업데이트할 수 있다")
        void updateCategoryWithValidCommand() {
            // given
            Category existingCategory = new Category(
                1L,
                "existing",
                "기존 카테고리",
                "기존 설명",
                null,
                false,
                null,
                null);

            UpdateCategoryCommand command = UpdateCategoryCommand.create(
                1L,
                "업데이트된 카테고리",
                "업데이트된 설명");

            Category updatedCategory = new Category(
                1L,
                "existing",
                "업데이트된 카테고리",
                "업데이트된 설명",
                null,
                false,
                null,
                null);

            given(loadCategoryPort.loadCategoryById(1L)).willReturn(Optional.of(existingCategory));
            given(updateCategoryPort.updateCategory(any(Category.class))).willReturn(updatedCategory);

            // when
            Category result = categoryCommandService.updateCategory(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("existing");
            assertThat(result.getDisplayName()).isEqualTo("업데이트된 카테고리");
            assertThat(result.getDescription()).isEqualTo("업데이트된 설명");
            verify(updateCategoryPort).updateCategory(any(Category.class));
        }

        @Test
        @DisplayName("존재하지 않는 카테고리 업데이트 시 예외가 발생한다")
        void throwExceptionWhenCategoryNotFound() {
            // given
            UpdateCategoryCommand command = UpdateCategoryCommand.create(
                999L,
                "없는 카테고리",
                "없는 설명");

            given(loadCategoryPort.loadCategoryById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> categoryCommandService.updateCategory(command))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("ID가 999");
        }

        @Test
        @DisplayName("상위 카테고리를 변경할 수 있다")
        void updateCategoryWithParent() {
            // given
            Category existingCategory = new Category(
                1L,
                "existing",
                "기존 카테고리",
                "기존 설명",
                null,
                false,
                null,
                null);

            Category parentCategory = new Category(
                2L,
                "parent",
                "상위 카테고리",
                "상위 설명",
                null,
                false,
                null,
                null);

            UpdateCategoryCommand command = UpdateCategoryCommand.createWithParent(
                1L,
                "업데이트된 카테고리",
                "업데이트된 설명",
                2L);

            Category updatedCategory = new Category(
                1L,
                "existing",
                "업데이트된 카테고리",
                "업데이트된 설명",
                parentCategory,
                false,
                null,
                null);

            given(loadCategoryPort.loadCategoryById(1L)).willReturn(Optional.of(existingCategory));
            given(loadCategoryPort.loadCategoryById(2L)).willReturn(Optional.of(parentCategory));
            given(updateCategoryPort.updateCategory(any(Category.class))).willReturn(updatedCategory);

            // when
            Category result = categoryCommandService.updateCategory(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getParentCategory()).isEqualTo(parentCategory);
        }
    }

    @Nested
    @DisplayName("카테고리 삭제 테스트")
    class DeleteCategoryTest {

        @Test
        @DisplayName("유효한 ID로 카테고리를 삭제할 수 있다")
        void deleteCategoryWithValidId() {
            // given
            Long categoryId = 1L;
            Category category = new Category(
                categoryId,
                "test",
                "테스트",
                "테스트 설명",
                null,
                false,
                null,
                null);

            given(loadCategoryPort.loadCategoryById(categoryId)).willReturn(Optional.of(category));

            // when
            categoryCommandService.deleteCategory(categoryId);

            // then
            verify(deleteCategoryPort).deleteCategory(categoryId);
        }

        @Test
        @DisplayName("존재하지 않는 카테고리 삭제 시 예외가 발생한다")
        void throwExceptionWhenDeleteNonExistingCategory() {
            // given
            Long categoryId = 999L;
            given(loadCategoryPort.loadCategoryById(categoryId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> categoryCommandService.deleteCategory(categoryId))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("ID가 999");
        }

        @Test
        @DisplayName("카테고리 삭제 중 오류 발생 시 예외가 발생한다")
        void throwExceptionWhenErrorOccursDuringDeletion() {
            // given
            Long categoryId = 1L;
            Category category = new Category(
                categoryId,
                "test",
                "테스트",
                "테스트 설명",
                null,
                false,
                null,
                null);

            given(loadCategoryPort.loadCategoryById(categoryId)).willReturn(Optional.of(category));
            doThrow(new RuntimeException("DB 에러")).when(deleteCategoryPort).deleteCategory(anyLong());

            // when & then
            assertThatThrownBy(() -> categoryCommandService.deleteCategory(categoryId))
                .isInstanceOf(CategoryOperationFailedException.class)
                .hasMessageContaining("Error occurred during category operation");
        }
    }
}
