package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.port.out.query.FindCategoriesPort;
import com.gongdel.promptserver.application.port.out.query.LoadCategoryPort;
import com.gongdel.promptserver.domain.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryQueryServiceTest {

    @Mock
    private LoadCategoryPort loadCategoryPort;

    @Mock
    private FindCategoriesPort findCategoriesPort;

    @InjectMocks
    private CategoryQueryService categoryQueryService;

    @Nested
    @DisplayName("카테고리 단일 조회 테스트")
    class SingleCategoryQueryTest {

        @Test
        @DisplayName("ID로 카테고리를 조회할 수 있다")
        void getCategoryById() {
            // given
            Long categoryId = 1L;
            Category category = new Category(
                categoryId,
                "test",
                "테스트 카테고리",
                "테스트 설명",
                null,
                false,
                null,
                null);

            given(loadCategoryPort.loadCategoryById(categoryId)).willReturn(Optional.of(category));

            // when
            Optional<Category> result = categoryQueryService.getCategoryById(categoryId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(categoryId);
            assertThat(result.get().getName()).isEqualTo("test");
            verify(loadCategoryPort).loadCategoryById(categoryId);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환한다")
        void getCategoryByNonExistingId() {
            // given
            Long nonExistingId = 999L;
            given(loadCategoryPort.loadCategoryById(nonExistingId)).willReturn(Optional.empty());

            // when
            Optional<Category> result = categoryQueryService.getCategoryById(nonExistingId);

            // then
            assertThat(result).isEmpty();
            verify(loadCategoryPort).loadCategoryById(nonExistingId);
        }

        @Test
        @DisplayName("이름으로 카테고리를 조회할 수 있다")
        void getCategoryByName() {
            // given
            String categoryName = "test-category";
            Category category = new Category(
                1L,
                categoryName,
                "테스트 카테고리",
                "테스트 설명",
                null,
                false,
                null,
                null);

            given(loadCategoryPort.loadCategoryByName(categoryName)).willReturn(Optional.of(category));

            // when
            Optional<Category> result = categoryQueryService.getCategoryByName(categoryName);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo(categoryName);
            verify(loadCategoryPort).loadCategoryByName(categoryName);
        }
    }

    @Nested
    @DisplayName("카테고리 목록 조회 테스트")
    class CategoryListQueryTest {

        @Test
        @DisplayName("모든 카테고리를 조회할 수 있다")
        void getAllCategories() {
            // given
            List<Category> categories = Arrays.asList(
                new Category(1L, "cat1", "카테고리1", "설명1", null, false, null, null),
                new Category(2L, "cat2", "카테고리2", "설명2", null, false, null, null));

            given(findCategoriesPort.findAllCategories()).willReturn(categories);

            // when
            List<Category> result = categoryQueryService.getAllCategories();

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("cat1");
            assertThat(result.get(1).getName()).isEqualTo("cat2");
            verify(findCategoriesPort).findAllCategories();
        }

        @Test
        @DisplayName("시스템 카테고리 여부로 카테고리를 조회할 수 있다")
        void getCategoriesBySystemFlag() {
            // given
            List<Category> systemCategories = Arrays.asList(
                new Category(1L, "sys1", "시스템1", "설명1", null, true, null, null),
                new Category(2L, "sys2", "시스템2", "설명2", null, true, null, null));

            given(findCategoriesPort.findCategoriesByIsSystem(true)).willReturn(systemCategories);

            // when
            List<Category> result = categoryQueryService.getCategoriesBySystemFlag(true);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).isSystem()).isTrue();
            assertThat(result.get(1).isSystem()).isTrue();
            verify(findCategoriesPort).findCategoriesByIsSystem(true);
        }

        @Test
        @DisplayName("최상위 카테고리를 조회할 수 있다")
        void getRootCategories() {
            // given
            List<Category> rootCategories = Arrays.asList(
                new Category(1L, "root1", "루트1", "설명1", null, false, null, null),
                new Category(2L, "root2", "루트2", "설명2", null, false, null, null));

            given(findCategoriesPort.findRootCategories()).willReturn(rootCategories);

            // when
            List<Category> result = categoryQueryService.getRootCategories();

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getParentCategory()).isNull();
            assertThat(result.get(1).getParentCategory()).isNull();
            verify(findCategoriesPort).findRootCategories();
        }

        @Test
        @DisplayName("특정 카테고리의 하위 카테고리를 조회할 수 있다")
        void getSubCategories() {
            // given
            Long parentId = 1L;
            Category parent = new Category(parentId, "parent", "상위", "상위 설명", null, false, null, null);

            List<Category> subCategories = Arrays.asList(
                new Category(2L, "sub1", "하위1", "설명1", parent, false, null, null),
                new Category(3L, "sub2", "하위2", "설명2", parent, false, null, null));

            given(loadCategoryPort.loadCategoryById(parentId)).willReturn(Optional.of(mock(Category.class)));
            given(findCategoriesPort.findCategoriesByParentId(parentId)).willReturn(subCategories);

            // when
            List<Category> result = categoryQueryService.getSubCategories(parentId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getParentCategory()).isEqualTo(parent);
            assertThat(result.get(1).getParentCategory()).isEqualTo(parent);
            verify(findCategoriesPort).findCategoriesByParentId(parentId);
        }
    }
}
