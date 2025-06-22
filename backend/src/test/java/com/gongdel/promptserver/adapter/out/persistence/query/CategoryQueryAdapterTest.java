package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.entity.CategoryEntity;
import com.gongdel.promptserver.adapter.out.persistence.repository.JpaCategoryRepository;
import com.gongdel.promptserver.domain.exception.CategoryOperationException;
import com.gongdel.promptserver.domain.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryQueryAdapterTest {

    @Mock
    private JpaCategoryRepository jpaCategoryRepository;

    @InjectMocks
    private CategoryQueryAdapter categoryQueryAdapter;

    private CategoryEntity categoryEntity;
    private CategoryEntity parentCategoryEntity;
    private Category category;
    private Category parentCategory;
    private Long categoryId;
    private String categoryName;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        categoryId = 1L;
        categoryName = "test-category";

        // 부모 카테고리 엔티티 생성
        parentCategoryEntity = new CategoryEntity();
        parentCategoryEntity.setId(2L);
        parentCategoryEntity.setName("parent-category");
        parentCategoryEntity.setDisplayName("부모 카테고리");
        parentCategoryEntity.setDescription("부모 카테고리 설명");
        parentCategoryEntity.setSystem(true);
        parentCategoryEntity.setCreatedAt(LocalDateTime.now());
        parentCategoryEntity.setUpdatedAt(LocalDateTime.now());

        // 카테고리 엔티티 생성
        categoryEntity = new CategoryEntity();
        categoryEntity.setId(categoryId);
        categoryEntity.setName(categoryName);
        categoryEntity.setDisplayName("테스트 카테고리");
        categoryEntity.setDescription("테스트 카테고리 설명");
        categoryEntity.setSystem(false);
        categoryEntity.setParentCategory(parentCategoryEntity);
        categoryEntity.setCreatedAt(LocalDateTime.now());
        categoryEntity.setUpdatedAt(LocalDateTime.now());

        // 도메인 모델 객체 생성
        parentCategory = new Category(
            parentCategoryEntity.getId(),
            parentCategoryEntity.getName(),
            parentCategoryEntity.getDisplayName(),
            parentCategoryEntity.getDescription(),
            null,
            parentCategoryEntity.isSystem(),
            parentCategoryEntity.getCreatedAt(),
            parentCategoryEntity.getUpdatedAt());

        category = new Category(
            categoryEntity.getId(),
            categoryEntity.getName(),
            categoryEntity.getDisplayName(),
            categoryEntity.getDescription(),
            parentCategory,
            categoryEntity.isSystem(),
            categoryEntity.getCreatedAt(),
            categoryEntity.getUpdatedAt());
    }

    @Test
    @DisplayName("ID로 카테고리를 조회한다")
    void loadCategoryById_ShouldReturnCategory() {
        // given
        when(jpaCategoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));

        // when
        Optional<Category> result = categoryQueryAdapter.loadCategoryById(categoryId);

        // then
        assertThat(result).isPresent();
        Category resultCategory = result.get();
        assertThat(resultCategory.getId()).isEqualTo(categoryId);
        assertThat(resultCategory.getName()).isEqualTo(categoryName);
        assertThat(resultCategory.getParentCategory()).isNotNull();
        assertThat(resultCategory.getParentCategory().getId()).isEqualTo(parentCategoryEntity.getId());

        // verify
        verify(jpaCategoryRepository).findById(categoryId);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 카테고리 조회 시 빈 Optional을 반환한다")
    void loadCategoryById_WithNonExistingId_ShouldReturnEmptyOptional() {
        // given
        Long nonExistingId = 999L;
        when(jpaCategoryRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // when
        Optional<Category> result = categoryQueryAdapter.loadCategoryById(nonExistingId);

        // then
        assertThat(result).isEmpty();

        // verify
        verify(jpaCategoryRepository).findById(nonExistingId);
    }

    @Test
    @DisplayName("이름으로 카테고리를 조회한다")
    void loadCategoryByName_ShouldReturnCategory() {
        // given
        when(jpaCategoryRepository.findByName(categoryName)).thenReturn(Optional.of(categoryEntity));

        // when
        Optional<Category> result = categoryQueryAdapter.loadCategoryByName(categoryName);

        // then
        assertThat(result).isPresent();
        Category resultCategory = result.get();
        assertThat(resultCategory.getId()).isEqualTo(categoryId);
        assertThat(resultCategory.getName()).isEqualTo(categoryName);

        // verify
        verify(jpaCategoryRepository).findByName(categoryName);
    }

    @Test
    @DisplayName("존재하지 않는 이름으로 카테고리 조회 시 빈 Optional을 반환한다")
    void loadCategoryByName_WithNonExistingName_ShouldReturnEmptyOptional() {
        // given
        String nonExistingName = "non-existing-category";
        when(jpaCategoryRepository.findByName(nonExistingName)).thenReturn(Optional.empty());

        // when
        Optional<Category> result = categoryQueryAdapter.loadCategoryByName(nonExistingName);

        // then
        assertThat(result).isEmpty();

        // verify
        verify(jpaCategoryRepository).findByName(nonExistingName);
    }

    @Test
    @DisplayName("모든 카테고리 목록을 조회한다")
    void findAllCategories_ShouldReturnAllCategories() {
        // given
        List<CategoryEntity> entities = Arrays.asList(categoryEntity, parentCategoryEntity);
        when(jpaCategoryRepository.findAll()).thenReturn(entities);

        // when
        List<Category> result = categoryQueryAdapter.findAllCategories();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        // verify
        verify(jpaCategoryRepository).findAll();
    }

    @Test
    @DisplayName("시스템 카테고리 여부로 카테고리 목록을 조회한다")
    void findCategoriesByIsSystem_ShouldReturnFilteredCategories() {
        // given
        boolean isSystem = true;
        List<CategoryEntity> systemEntities = List.of(parentCategoryEntity);
        when(jpaCategoryRepository.findByIsSystem(isSystem)).thenReturn(systemEntities);

        // when
        List<Category> result = categoryQueryAdapter.findCategoriesByIsSystem(isSystem);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(parentCategoryEntity.getId());
        assertThat(result.get(0).isSystem()).isTrue();

        // verify
        verify(jpaCategoryRepository).findByIsSystem(isSystem);
    }

    @Test
    @DisplayName("최상위 카테고리 목록을 조회한다")
    void findRootCategories_ShouldReturnRootCategories() {
        // given
        CategoryEntity rootCategory = new CategoryEntity();
        rootCategory.setId(3L);
        rootCategory.setName("root-category");
        rootCategory.setDisplayName("루트 카테고리");
        rootCategory.setDescription("루트 카테고리 설명");
        rootCategory.setSystem(false);
        rootCategory.setParentCategory(null);

        List<CategoryEntity> rootEntities = List.of(rootCategory);
        when(jpaCategoryRepository.findByParentCategoryIsNull()).thenReturn(rootEntities);

        // when
        List<Category> result = categoryQueryAdapter.findRootCategories();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(rootCategory.getId());
        assertThat(result.get(0).getParentCategory()).isNull();

        // verify
        verify(jpaCategoryRepository).findByParentCategoryIsNull();
    }

    @Test
    @DisplayName("상위 카테고리 ID로 하위 카테고리 목록을 조회한다")
    void findCategoriesByParentId_ShouldReturnChildCategories() {
        // given
        Long parentId = parentCategoryEntity.getId();
        List<CategoryEntity> childEntities = List.of(categoryEntity);
        when(jpaCategoryRepository.findByParentCategoryId(parentId)).thenReturn(childEntities);

        // when
        List<Category> result = categoryQueryAdapter.findCategoriesByParentId(parentId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(categoryEntity.getId());
        assertThat(result.get(0).getParentCategory()).isNotNull();
        assertThat(result.get(0).getParentCategory().getId()).isEqualTo(parentId);

        // verify
        verify(jpaCategoryRepository).findByParentCategoryId(parentId);
    }

    // 추가 테스트 케이스: null 파라미터 처리 테스트

    @Test
    @DisplayName("ID가 null일 때 IllegalArgumentException이 발생한다")
    void loadCategoryById_WithNullId_ShouldThrowException() {
        // when, then
        assertThrows(IllegalArgumentException.class, () -> categoryQueryAdapter.loadCategoryById(null));

        // verify
        verify(jpaCategoryRepository, never()).findById(any());
    }

    @Test
    @DisplayName("이름이 null일 때 IllegalArgumentException이 발생한다")
    void loadCategoryByName_WithNullName_ShouldThrowException() {
        // when, then
        assertThrows(IllegalArgumentException.class, () -> categoryQueryAdapter.loadCategoryByName(null));

        // verify
        verify(jpaCategoryRepository, never()).findByName(any());
    }

    @Test
    @DisplayName("부모 ID가 null일 때 IllegalArgumentException이 발생한다")
    void findCategoriesByParentId_WithNullParentId_ShouldThrowException() {
        // when, then
        assertThrows(IllegalArgumentException.class, () -> categoryQueryAdapter.findCategoriesByParentId(null));

        // verify
        verify(jpaCategoryRepository, never()).findByParentCategoryId(any());
    }

    // 추가 테스트 케이스: 빈 리스트 반환 테스트

    @Test
    @DisplayName("카테고리가 없을 때 빈 리스트를 반환한다")
    void findAllCategories_WithNoCategories_ShouldReturnEmptyList() {
        // given
        when(jpaCategoryRepository.findAll()).thenReturn(Collections.emptyList());

        // when
        List<Category> result = categoryQueryAdapter.findAllCategories();

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        // verify
        verify(jpaCategoryRepository).findAll();
    }

    @Test
    @DisplayName("시스템 카테고리가 없을 때 빈 리스트를 반환한다")
    void findCategoriesByIsSystem_WithNoMatchingCategories_ShouldReturnEmptyList() {
        // given
        boolean isSystem = true;
        when(jpaCategoryRepository.findByIsSystem(isSystem)).thenReturn(Collections.emptyList());

        // when
        List<Category> result = categoryQueryAdapter.findCategoriesByIsSystem(isSystem);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        // verify
        verify(jpaCategoryRepository).findByIsSystem(isSystem);
    }

    @Test
    @DisplayName("루트 카테고리가 없을 때 빈 리스트를 반환한다")
    void findRootCategories_WithNoRootCategories_ShouldReturnEmptyList() {
        // given
        when(jpaCategoryRepository.findByParentCategoryIsNull()).thenReturn(Collections.emptyList());

        // when
        List<Category> result = categoryQueryAdapter.findRootCategories();

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        // verify
        verify(jpaCategoryRepository).findByParentCategoryIsNull();
    }

    @Test
    @DisplayName("하위 카테고리가 없을 때 빈 리스트를 반환한다")
    void findCategoriesByParentId_WithNoChildCategories_ShouldReturnEmptyList() {
        // given
        Long parentId = parentCategoryEntity.getId();
        when(jpaCategoryRepository.findByParentCategoryId(parentId)).thenReturn(Collections.emptyList());

        // when
        List<Category> result = categoryQueryAdapter.findCategoriesByParentId(parentId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        // verify
        verify(jpaCategoryRepository).findByParentCategoryId(parentId);
    }

    // 추가 테스트 케이스: 예외 발생 상황 테스트

    @Test
    @DisplayName("ID로 카테고리 조회 중 예외 발생 시 CategoryOperationException이 발생한다")
    void loadCategoryById_WhenExceptionOccurs_ShouldThrowCategoryOperationException() {
        // given
        when(jpaCategoryRepository.findById(categoryId)).thenThrow(new RuntimeException("Database error"));

        // when, then
        assertThatThrownBy(() -> categoryQueryAdapter.loadCategoryById(categoryId))
            .isInstanceOf(CategoryOperationException.class)
            .hasMessageContaining("Failed to load category with id: " + categoryId);

        // verify
        verify(jpaCategoryRepository).findById(categoryId);
    }

    @Test
    @DisplayName("이름으로 카테고리 조회 중 예외 발생 시 CategoryOperationException이 발생한다")
    void loadCategoryByName_WhenExceptionOccurs_ShouldThrowCategoryOperationException() {
        // given
        when(jpaCategoryRepository.findByName(categoryName)).thenThrow(new RuntimeException("Database error"));

        // when, then
        assertThatThrownBy(() -> categoryQueryAdapter.loadCategoryByName(categoryName))
            .isInstanceOf(CategoryOperationException.class)
            .hasMessageContaining("Failed to load category with name: " + categoryName);

        // verify
        verify(jpaCategoryRepository).findByName(categoryName);
    }

    @Test
    @DisplayName("전체 카테고리 조회 중 예외 발생 시 CategoryOperationException이 발생한다")
    void findAllCategories_WhenExceptionOccurs_ShouldThrowCategoryOperationException() {
        // given
        when(jpaCategoryRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // when, then
        assertThatThrownBy(() -> categoryQueryAdapter.findAllCategories())
            .isInstanceOf(CategoryOperationException.class)
            .hasMessageContaining("Failed to find all categories");

        // verify
        verify(jpaCategoryRepository).findAll();
    }

    @Test
    @DisplayName("시스템 카테고리 조회 중 예외 발생 시 CategoryOperationException이 발생한다")
    void findCategoriesByIsSystem_WhenExceptionOccurs_ShouldThrowCategoryOperationException() {
        // given
        boolean isSystem = true;
        when(jpaCategoryRepository.findByIsSystem(isSystem)).thenThrow(new RuntimeException("Database error"));

        // when, then
        assertThatThrownBy(() -> categoryQueryAdapter.findCategoriesByIsSystem(isSystem))
            .isInstanceOf(CategoryOperationException.class)
            .hasMessageContaining("Failed to find categories with isSystem: " + isSystem);

        // verify
        verify(jpaCategoryRepository).findByIsSystem(isSystem);
    }

    @Test
    @DisplayName("루트 카테고리 조회 중 예외 발생 시 CategoryOperationException이 발생한다")
    void findRootCategories_WhenExceptionOccurs_ShouldThrowCategoryOperationException() {
        // given
        when(jpaCategoryRepository.findByParentCategoryIsNull()).thenThrow(new RuntimeException("Database error"));

        // when, then
        assertThatThrownBy(() -> categoryQueryAdapter.findRootCategories())
            .isInstanceOf(CategoryOperationException.class)
            .hasMessageContaining("Failed to find root categories");

        // verify
        verify(jpaCategoryRepository).findByParentCategoryIsNull();
    }

    @Test
    @DisplayName("부모 ID로 하위 카테고리 조회 중 예외 발생 시 CategoryOperationException이 발생한다")
    void findCategoriesByParentId_WhenExceptionOccurs_ShouldThrowCategoryOperationException() {
        // given
        Long parentId = parentCategoryEntity.getId();
        when(jpaCategoryRepository.findByParentCategoryId(parentId)).thenThrow(new RuntimeException("Database error"));

        // when, then
        assertThatThrownBy(() -> categoryQueryAdapter.findCategoriesByParentId(parentId))
            .isInstanceOf(CategoryOperationException.class)
            .hasMessageContaining("Failed to find categories with parent id: " + parentId);

        // verify
        verify(jpaCategoryRepository).findByParentCategoryId(parentId);
    }
}
