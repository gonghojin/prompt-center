package com.gongdel.promptserver.adapter.out.persistence.command;

import com.gongdel.promptserver.adapter.out.persistence.entity.CategoryEntity;
import com.gongdel.promptserver.adapter.out.persistence.repository.JpaCategoryRepository;
import com.gongdel.promptserver.domain.exception.CategoryErrorType;
import com.gongdel.promptserver.domain.exception.CategoryNotFoundDomainException;
import com.gongdel.promptserver.domain.exception.CategoryOperationException;
import com.gongdel.promptserver.domain.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryCommandAdapterTest {

    @Mock
    private JpaCategoryRepository jpaCategoryRepository;

    @InjectMocks
    private CategoryCommandAdapter categoryCommandAdapter;

    @Captor
    private ArgumentCaptor<CategoryEntity> categoryEntityCaptor;

    private Category category;
    private Category parentCategory;
    private CategoryEntity categoryEntity;
    private CategoryEntity parentCategoryEntity;
    private Long categoryId;
    private String categoryName;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        categoryId = 1L;
        categoryName = "test-category";

        // 부모 카테고리 생성
        parentCategory = new Category(
            2L,
            "parent-category",
            "부모 카테고리",
            "부모 카테고리 설명",
            null,
            true,
            LocalDateTime.now(),
            LocalDateTime.now());

        // 테스트용 카테고리 생성
        category = new Category(
            categoryId,
            categoryName,
            "테스트 카테고리",
            "테스트 카테고리 설명",
            parentCategory,
            false,
            LocalDateTime.now(),
            LocalDateTime.now());

        // 부모 카테고리 엔티티 생성
        parentCategoryEntity = new CategoryEntity();
        parentCategoryEntity.setId(2L);
        parentCategoryEntity.setName("parent-category");
        parentCategoryEntity.setDisplayName("부모 카테고리");
        parentCategoryEntity.setDescription("부모 카테고리 설명");
        parentCategoryEntity.setSystem(true);
        parentCategoryEntity.setCreatedAt(parentCategory.getCreatedAt());
        parentCategoryEntity.setUpdatedAt(parentCategory.getUpdatedAt());

        // 카테고리 엔티티 생성
        categoryEntity = new CategoryEntity();
        categoryEntity.setId(categoryId);
        categoryEntity.setName(categoryName);
        categoryEntity.setDisplayName("테스트 카테고리");
        categoryEntity.setDescription("테스트 카테고리 설명");
        categoryEntity.setSystem(false);
        categoryEntity.setParentCategory(parentCategoryEntity);
        categoryEntity.setCreatedAt(category.getCreatedAt());
        categoryEntity.setUpdatedAt(category.getUpdatedAt());
    }

    @Nested
    @DisplayName("카테고리 저장 테스트")
    class SaveCategoryTest {
        @Test
        @DisplayName("카테고리를 저장한다")
        void saveCategory_ShouldSaveAndReturnCategory() {
            // given
            // 저장용 카테고리는 ID가 null이어야 함
            Category categoryForSave = new Category(
                null, // ID는 null
                categoryName,
                "테스트 카테고리",
                "테스트 카테고리 설명",
                parentCategory,
                false,
                LocalDateTime.now(),
                LocalDateTime.now());

            when(jpaCategoryRepository.save(any(CategoryEntity.class))).thenReturn(categoryEntity);

            // when
            Category savedCategory = categoryCommandAdapter.saveCategory(categoryForSave);

            // then
            verify(jpaCategoryRepository).save(categoryEntityCaptor.capture());
            CategoryEntity capturedEntity = categoryEntityCaptor.getValue();

            // 변환된 엔티티 검증
            assertThat(capturedEntity.getName()).isEqualTo(categoryName);
            assertThat(capturedEntity.getDisplayName()).isEqualTo("테스트 카테고리");
            assertThat(capturedEntity.getDescription()).isEqualTo("테스트 카테고리 설명");
            assertThat(capturedEntity.isSystem()).isFalse();
            assertThat(capturedEntity.getParentCategory()).isNotNull();
            assertThat(capturedEntity.getParentCategory().getId()).isEqualTo(parentCategory.getId());

            // 반환된 도메인 모델 검증
            assertThat(savedCategory).isNotNull();
            assertThat(savedCategory.getId()).isEqualTo(categoryId);
            assertThat(savedCategory.getName()).isEqualTo(categoryName);
            assertThat(savedCategory.getDisplayName()).isEqualTo("테스트 카테고리");
            assertThat(savedCategory.getDescription()).isEqualTo("테스트 카테고리 설명");
            assertThat(savedCategory.isSystem()).isFalse();
            assertThat(savedCategory.getParentCategory()).isNotNull();
            assertThat(savedCategory.getParentCategory().getId()).isEqualTo(parentCategory.getId());
        }

        @Test
        @DisplayName("신규 카테고리를 생성한다")
        void saveCategory_WithNewCategory_ShouldCreateAndReturnCategory() {
            // given
            Category newCategory = new Category(
                "new-category",
                "새 카테고리",
                "새 카테고리 설명");

            CategoryEntity savedEntity = new CategoryEntity();
            savedEntity.setId(3L);
            savedEntity.setName("new-category");
            savedEntity.setDisplayName("새 카테고리");
            savedEntity.setDescription("새 카테고리 설명");
            savedEntity.setSystem(false);
            savedEntity.setParentCategory(null);
            savedEntity.setCreatedAt(LocalDateTime.now());
            savedEntity.setUpdatedAt(LocalDateTime.now());

            when(jpaCategoryRepository.save(any(CategoryEntity.class))).thenReturn(savedEntity);

            // when
            Category savedCategory = categoryCommandAdapter.saveCategory(newCategory);

            // then
            verify(jpaCategoryRepository).save(categoryEntityCaptor.capture());
            CategoryEntity capturedEntity = categoryEntityCaptor.getValue();

            // 변환된 엔티티 검증
            assertThat(capturedEntity.getName()).isEqualTo("new-category");
            assertThat(capturedEntity.getDisplayName()).isEqualTo("새 카테고리");
            assertThat(capturedEntity.getDescription()).isEqualTo("새 카테고리 설명");
            assertThat(capturedEntity.isSystem()).isFalse();
            assertThat(capturedEntity.getParentCategory()).isNull();

            // 반환된 도메인 모델 검증
            assertThat(savedCategory).isNotNull();
            assertThat(savedCategory.getId()).isEqualTo(3L);
            assertThat(savedCategory.getName()).isEqualTo("new-category");
            assertThat(savedCategory.getDisplayName()).isEqualTo("새 카테고리");
            assertThat(savedCategory.getDescription()).isEqualTo("새 카테고리 설명");
            assertThat(savedCategory.isSystem()).isFalse();
            assertThat(savedCategory.getParentCategory()).isNull();
        }

        @Test
        @DisplayName("null 카테고리를 저장하면 예외가 발생한다")
        void saveCategory_WithNullCategory_ShouldThrowException() {
            // given
            Category nullCategory = null;

            // when, then
            assertThatThrownBy(() -> categoryCommandAdapter.saveCategory(nullCategory))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Category must not be null");
        }

        @Test
        @DisplayName("같은 이름의 카테고리가 존재하면 예외가 발생한다")
        void saveCategory_WithDuplicateName_ShouldThrowException() {
            // given
            Category newCategory = new Category(
                "existing-name",
                "이미 존재하는 카테고리",
                "이미 존재하는 카테고리 설명");

            when(jpaCategoryRepository.findByName("existing-name")).thenReturn(Optional.of(new CategoryEntity()));

            // when, then
            assertThatThrownBy(() -> categoryCommandAdapter.saveCategory(newCategory))
                .isInstanceOf(CategoryOperationException.class)
                .extracting("errorType")
                .isEqualTo(CategoryErrorType.DUPLICATE_NAME);
        }

        @Test
        @DisplayName("데이터 무결성 위반 시 예외가 발생한다")
        void saveCategory_WhenDataIntegrityViolation_ShouldThrowException() {
            // given
            Category newCategory = new Category(
                "new-category",
                "새 카테고리",
                "새 카테고리 설명");

            when(jpaCategoryRepository.findByName(anyString())).thenReturn(Optional.empty());
            when(jpaCategoryRepository.save(any(CategoryEntity.class)))
                .thenThrow(new DataIntegrityViolationException("Constraint violation"));

            // when, then
            assertThatThrownBy(() -> categoryCommandAdapter.saveCategory(newCategory))
                .isInstanceOf(CategoryOperationException.class)
                .extracting("errorType")
                .isEqualTo(CategoryErrorType.INVALID_CATEGORY);
        }
    }

    @Nested
    @DisplayName("카테고리 업데이트 테스트")
    class UpdateCategoryTest {
        @Test
        @DisplayName("카테고리를 업데이트한다")
        void updateCategory_ShouldUpdateAndReturnCategory() {
            // given
            Category updatedCategory = new Category(
                categoryId,
                categoryName,
                "업데이트된 카테고리",
                "업데이트된 설명",
                null, // 부모 카테고리 제거
                true, // 시스템 카테고리로 변경
                category.getCreatedAt(),
                LocalDateTime.now());

            CategoryEntity updatedEntity = new CategoryEntity();
            updatedEntity.setId(categoryId);
            updatedEntity.setName(categoryName);
            updatedEntity.setDisplayName("업데이트된 카테고리");
            updatedEntity.setDescription("업데이트된 설명");
            updatedEntity.setSystem(true);
            updatedEntity.setParentCategory(null);
            updatedEntity.setCreatedAt(category.getCreatedAt());
            updatedEntity.setUpdatedAt(LocalDateTime.now());

            when(jpaCategoryRepository.existsById(categoryId)).thenReturn(true);
            when(jpaCategoryRepository.save(any(CategoryEntity.class))).thenReturn(updatedEntity);

            // when
            Category result = categoryCommandAdapter.updateCategory(updatedCategory);

            // then
            verify(jpaCategoryRepository).save(categoryEntityCaptor.capture());
            CategoryEntity capturedEntity = categoryEntityCaptor.getValue();

            // 변환된 엔티티 검증
            assertThat(capturedEntity.getId()).isEqualTo(categoryId);
            assertThat(capturedEntity.getName()).isEqualTo(categoryName);
            assertThat(capturedEntity.getDisplayName()).isEqualTo("업데이트된 카테고리");
            assertThat(capturedEntity.getDescription()).isEqualTo("업데이트된 설명");
            assertThat(capturedEntity.isSystem()).isTrue();
            assertThat(capturedEntity.getParentCategory()).isNull();

            // 반환된 도메인 모델 검증
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(categoryId);
            assertThat(result.getName()).isEqualTo(categoryName);
            assertThat(result.getDisplayName()).isEqualTo("업데이트된 카테고리");
            assertThat(result.getDescription()).isEqualTo("업데이트된 설명");
            assertThat(result.isSystem()).isTrue();
            assertThat(result.getParentCategory()).isNull();
        }

        @Test
        @DisplayName("존재하지 않는 카테고리를 업데이트하면 예외가 발생한다")
        void updateCategory_WithNonExistingCategory_ShouldThrowException() {
            // given
            when(jpaCategoryRepository.existsById(categoryId)).thenReturn(false);

            // when, then
            assertThatThrownBy(() -> categoryCommandAdapter.updateCategory(category))
                .isInstanceOf(CategoryNotFoundDomainException.class)
                .extracting("errorType")
                .isEqualTo(CategoryErrorType.NOT_FOUND);
        }

        @Test
        @DisplayName("null 카테고리를 업데이트하면 예외가 발생한다")
        void updateCategory_WithNullCategory_ShouldThrowException() {
            // given
            Category nullCategory = null;

            // when, then
            assertThatThrownBy(() -> categoryCommandAdapter.updateCategory(nullCategory))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Category must not be null");
        }

        @Test
        @DisplayName("ID가 없는 카테고리를 업데이트하면 예외가 발생한다")
        void updateCategory_WithNoId_ShouldThrowException() {
            // given
            Category noIdCategory = new Category(
                null,
                "no-id",
                "ID 없는 카테고리",
                "ID 없는 카테고리 설명",
                null,
                false,
                LocalDateTime.now(),
                LocalDateTime.now());

            // when, then
            assertThatThrownBy(() -> categoryCommandAdapter.updateCategory(noIdCategory))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Category id must not be null for update");
        }

        @Test
        @DisplayName("같은 이름의 다른 카테고리가 존재하면 예외가 발생한다")
        void updateCategory_WithDuplicateName_ShouldThrowException() {
            // given
            String duplicateName = "duplicate-name";
            Long differentId = 999L;

            Category updatedCategory = new Category(
                categoryId,
                duplicateName,
                "중복 이름 카테고리",
                "중복 이름 카테고리 설명",
                null,
                false,
                LocalDateTime.now(),
                LocalDateTime.now());

            CategoryEntity existingEntity = new CategoryEntity();
            existingEntity.setId(differentId);
            existingEntity.setName(duplicateName);

            // 이름으로 이미 존재하는 다른 카테고리를 찾음 - 중복 이름 검사에서 사용됨
            when(jpaCategoryRepository.findByName(duplicateName)).thenReturn(Optional.of(existingEntity));

            // when, then
            assertThatThrownBy(() -> categoryCommandAdapter.updateCategory(updatedCategory))
                .isInstanceOf(CategoryOperationException.class)
                .hasFieldOrPropertyWithValue("errorType", CategoryErrorType.DUPLICATE_NAME);

            // 호출 확인 - 중복 이름이 발견되면 existsById는 호출되지 않음
            verify(jpaCategoryRepository).findByName(duplicateName);
            verify(jpaCategoryRepository, never()).existsById(anyLong());
            verify(jpaCategoryRepository, never()).save(any(CategoryEntity.class));
        }

        @Test
        @DisplayName("데이터 무결성 위반 시 예외가 발생한다")
        void updateCategory_WhenDataIntegrityViolation_ShouldThrowException() {
            // given
            when(jpaCategoryRepository.existsById(categoryId)).thenReturn(true);
            when(jpaCategoryRepository.findByName(anyString())).thenReturn(Optional.empty());
            when(jpaCategoryRepository.save(any(CategoryEntity.class)))
                .thenThrow(new DataIntegrityViolationException("Constraint violation"));

            // when, then
            assertThatThrownBy(() -> categoryCommandAdapter.updateCategory(category))
                .isInstanceOf(CategoryOperationException.class)
                .extracting("errorType")
                .isEqualTo(CategoryErrorType.INVALID_CATEGORY);
        }
    }

    @Nested
    @DisplayName("카테고리 삭제 테스트")
    class DeleteCategoryTest {
        @Test
        @DisplayName("카테고리를 삭제한다")
        void deleteCategory_ShouldDeleteCategoryById() {
            // given
            Long categoryIdToDelete = 1L;
            doNothing().when(jpaCategoryRepository).deleteById(categoryIdToDelete);

            // when
            categoryCommandAdapter.deleteCategory(categoryIdToDelete);

            // then
            verify(jpaCategoryRepository).deleteById(categoryIdToDelete);
        }

        @Test
        @DisplayName("존재하지 않는 카테고리를 삭제하면 예외가 발생한다")
        void deleteCategory_WithNonExistingCategory_ShouldThrowException() {
            // given
            Long nonExistingId = 999L;
            doThrow(new EmptyResultDataAccessException(1)).when(jpaCategoryRepository).deleteById(nonExistingId);

            // when, then
            assertThatThrownBy(() -> categoryCommandAdapter.deleteCategory(nonExistingId))
                .isInstanceOf(CategoryNotFoundDomainException.class)
                .extracting("errorType")
                .isEqualTo(CategoryErrorType.NOT_FOUND);
        }

        @Test
        @DisplayName("null ID로 삭제하면 예외가 발생한다")
        void deleteCategory_WithNullId_ShouldThrowException() {
            // given
            Long nullId = null;

            // when, then
            assertThatThrownBy(() -> categoryCommandAdapter.deleteCategory(nullId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Category id must not be null");
        }
    }
}
