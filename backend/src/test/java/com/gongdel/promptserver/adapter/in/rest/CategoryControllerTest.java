package com.gongdel.promptserver.adapter.in.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gongdel.promptserver.adapter.in.rest.request.CreateCategoryRequest;
import com.gongdel.promptserver.adapter.in.rest.request.UpdateCategoryRequest;
import com.gongdel.promptserver.application.exception.CategoryDuplicateNameException;
import com.gongdel.promptserver.application.exception.CategoryNotFoundException;
import com.gongdel.promptserver.application.port.in.CategoryCommandUseCase;
import com.gongdel.promptserver.application.port.in.CategoryQueryUseCase;
import com.gongdel.promptserver.domain.model.Category;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryCommandUseCase categoryCommandUseCase;

    @MockBean
    private CategoryQueryUseCase categoryQueryUseCase;

    private final LocalDateTime now = LocalDateTime.now();

    @Nested
    @DisplayName("카테고리 생성 API 테스트")
    class CreateCategoryTest {

        @Test
        @DisplayName("유효한 요청으로 카테고리를 생성할 수 있다")
        void createCategory_WithValidRequest_ShouldReturnCreatedCategory() throws Exception {
            // given
            CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("test-category")
                .displayName("테스트 카테고리")
                .description("테스트 카테고리 설명")
                .isSystem(false)
                .build();

            Category createdCategory = new Category(
                1L,
                "test-category",
                "테스트 카테고리",
                "테스트 카테고리 설명",
                null,
                false,
                now,
                now);

            given(categoryCommandUseCase.createCategory(any())).willReturn(createdCategory);

            // when & then
            mockMvc.perform(post("/api/v1/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("test-category")))
                .andExpect(jsonPath("$.displayName", is("테스트 카테고리")))
                .andExpect(jsonPath("$.description", is("테스트 카테고리 설명")))
                .andExpect(jsonPath("$.isSystem", is(false)));

            verify(categoryCommandUseCase).createCategory(any());
        }

        @Test
        @DisplayName("중복된 이름으로 카테고리 생성 시 409 응답을 반환한다")
        void createCategory_WithDuplicateName_ShouldReturnConflict() throws Exception {
            // given
            CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("duplicate-name")
                .displayName("중복 이름 카테고리")
                .description("중복 테스트")
                .build();

            given(categoryCommandUseCase.createCategory(any()))
                .willThrow(new CategoryDuplicateNameException("duplicate-name"));

            // when & then
            mockMvc.perform(post("/api/v1/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

            verify(categoryCommandUseCase).createCategory(any());
        }

        @Test
        @DisplayName("유효하지 않은 요청으로 카테고리 생성 시 400 응답을 반환한다")
        void createCategory_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
            // given
            CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name("") // 이름 없음
                .displayName("") // 표시 이름 없음
                .description("테스트 설명")
                .build();

            // when & then
            mockMvc.perform(post("/api/v1/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

            verify(categoryCommandUseCase, never()).createCategory(any());
        }
    }

    @Nested
    @DisplayName("카테고리 조회 API 테스트")
    class GetCategoryTest {

        @Test
        @DisplayName("ID로 카테고리를 조회할 수 있다")
        void getCategoryById_WithExistingId_ShouldReturnCategory() throws Exception {
            // given
            Long categoryId = 1L;
            Category category = new Category(
                categoryId,
                "test-category",
                "테스트 카테고리",
                "테스트 카테고리 설명",
                null,
                false,
                now,
                now);

            given(categoryQueryUseCase.getCategoryById(categoryId)).willReturn(Optional.of(category));

            // when & then
            mockMvc.perform(get("/api/v1/categories/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("test-category")))
                .andExpect(jsonPath("$.displayName", is("테스트 카테고리")));

            verify(categoryQueryUseCase).getCategoryById(categoryId);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 카테고리 조회 시 404 응답을 반환한다")
        void getCategoryById_WithNonExistingId_ShouldReturnNotFound() throws Exception {
            // given
            Long nonExistingId = 999L;
            given(categoryQueryUseCase.getCategoryById(nonExistingId)).willReturn(Optional.empty());

            // when & then
            mockMvc.perform(get("/api/v1/categories/{id}", nonExistingId))
                .andExpect(status().isNotFound());

            verify(categoryQueryUseCase).getCategoryById(nonExistingId);
        }

        @Test
        @DisplayName("모든 카테고리를 조회할 수 있다")
        void getCategories_ShouldReturnAllCategories() throws Exception {
            // given
            List<Category> categories = Arrays.asList(
                new Category(1L, "cat1", "카테고리1", "설명1", null, false, now, now),
                new Category(2L, "cat2", "카테고리2", "설명2", null, true, now, now));

            given(categoryQueryUseCase.getAllCategories()).willReturn(categories);

            // when & then
            mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("cat1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("cat2")));

            verify(categoryQueryUseCase).getAllCategories();
        }

        @Test
        @DisplayName("이름으로 카테고리를 조회할 수 있다")
        void getCategories_WithName_ShouldReturnMatchingCategory() throws Exception {
            // given
            String categoryName = "specific-cat";
            Category category = new Category(
                1L,
                categoryName,
                "특정 카테고리",
                "특정 카테고리 설명",
                null,
                false,
                now,
                now);

            given(categoryQueryUseCase.getCategoryByName(categoryName)).willReturn(Optional.of(category));

            // when & then
            mockMvc.perform(get("/api/v1/categories")
                    .param("name", categoryName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is(categoryName)));

            verify(categoryQueryUseCase).getCategoryByName(categoryName);
        }

        @Test
        @DisplayName("이름으로 카테고리 조회 시 결과가 없으면 빈 목록을 반환한다")
        void getCategories_WithNonExistingName_ShouldReturnEmptyList() throws Exception {
            // given
            String nonExistingName = "non-existing";
            given(categoryQueryUseCase.getCategoryByName(nonExistingName)).willReturn(Optional.empty());

            // when & then
            mockMvc.perform(get("/api/v1/categories")
                    .param("name", nonExistingName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

            verify(categoryQueryUseCase).getCategoryByName(nonExistingName);
        }

        @Test
        @DisplayName("시스템 카테고리 여부로 카테고리를 필터링할 수 있다")
        void getCategories_WithSystemFlag_ShouldReturnFilteredCategories() throws Exception {
            // given
            List<Category> systemCategories = Arrays.asList(
                new Category(1L, "sys1", "시스템1", "설명1", null, true, now, now),
                new Category(2L, "sys2", "시스템2", "설명2", null, true, now, now));

            given(categoryQueryUseCase.getCategoriesBySystemFlag(true)).willReturn(systemCategories);

            // when & then
            mockMvc.perform(get("/api/v1/categories")
                    .param("isSystem", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].isSystem", is(true)))
                .andExpect(jsonPath("$[1].isSystem", is(true)));

            verify(categoryQueryUseCase).getCategoriesBySystemFlag(true);
        }

        @Test
        @DisplayName("최상위 카테고리를 조회할 수 있다")
        void getRootCategories_ShouldReturnRootCategories() throws Exception {
            // given
            List<Category> rootCategories = Arrays.asList(
                new Category(1L, "root1", "루트1", "설명1", null, false, now, now),
                new Category(2L, "root2", "루트2", "설명2", null, false, now, now));

            given(categoryQueryUseCase.getRootCategories()).willReturn(rootCategories);

            // when & then
            mockMvc.perform(get("/api/v1/categories/roots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("root1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("root2")));

            verify(categoryQueryUseCase).getRootCategories();
        }

        @Test
        @DisplayName("특정 카테고리의 하위 카테고리를 조회할 수 있다")
        void getSubCategories_WithExistingParentId_ShouldReturnSubCategories() throws Exception {
            // given
            Long parentId = 1L;
            Category parentCategory = new Category(
                parentId,
                "parent",
                "상위 카테고리",
                "상위 카테고리 설명",
                null,
                false,
                now,
                now);

            List<Category> subCategories = Arrays.asList(
                new Category(2L, "sub1", "하위1", "설명1", parentCategory, false, now, now),
                new Category(3L, "sub2", "하위2", "설명2", parentCategory, false, now, now));

            given(categoryQueryUseCase.getSubCategories(parentId)).willReturn(subCategories);

            // when & then
            mockMvc.perform(get("/api/v1/categories/{parentId}/subcategories", parentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].name", is("sub1")))
                .andExpect(jsonPath("$[0].parentCategoryId", is(1)))
                .andExpect(jsonPath("$[1].id", is(3)))
                .andExpect(jsonPath("$[1].name", is("sub2")))
                .andExpect(jsonPath("$[1].parentCategoryId", is(1)));

            verify(categoryQueryUseCase).getSubCategories(parentId);
        }

        @Test
        @DisplayName("존재하지 않는 상위 카테고리의 하위 카테고리 조회 시 404 응답을 반환한다")
        void getSubCategories_WithNonExistingParentId_ShouldReturnNotFound() throws Exception {
            // given
            Long nonExistingParentId = 999L;
            given(categoryQueryUseCase.getSubCategories(nonExistingParentId))
                .willThrow(new CategoryNotFoundException(nonExistingParentId));

            // when & then
            mockMvc.perform(get("/api/v1/categories/{parentId}/subcategories", nonExistingParentId))
                .andExpect(status().isNotFound());

            verify(categoryQueryUseCase).getSubCategories(nonExistingParentId);
        }
    }

    @Nested
    @DisplayName("카테고리 업데이트 API 테스트")
    class UpdateCategoryTest {

        @Test
        @DisplayName("유효한 요청으로 카테고리를 업데이트할 수 있다")
        void updateCategory_WithValidRequest_ShouldReturnUpdatedCategory() throws Exception {
            // given
            Long categoryId = 1L;
            UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .displayName("업데이트된 카테고리")
                .description("업데이트된 설명")
                .build();

            Category updatedCategory = new Category(
                categoryId,
                "original-name",
                "업데이트된 카테고리",
                "업데이트된 설명",
                null,
                false,
                now,
                now);

            given(categoryCommandUseCase.updateCategory(any())).willReturn(updatedCategory);

            // when & then
            mockMvc.perform(put("/api/v1/categories/{id}", categoryId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.displayName", is("업데이트된 카테고리")))
                .andExpect(jsonPath("$.description", is("업데이트된 설명")));

            verify(categoryCommandUseCase).updateCategory(any());
        }

        @Test
        @DisplayName("존재하지 않는 카테고리 업데이트 시 404 응답을 반환한다")
        void updateCategory_WithNonExistingId_ShouldReturnNotFound() throws Exception {
            // given
            Long nonExistingId = 999L;
            UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .displayName("업데이트된 카테고리")
                .description("업데이트된 설명")
                .build();

            given(categoryCommandUseCase.updateCategory(any()))
                .willThrow(new CategoryNotFoundException(nonExistingId));

            // when & then
            mockMvc.perform(put("/api/v1/categories/{id}", nonExistingId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

            verify(categoryCommandUseCase).updateCategory(any());
        }

        @Test
        @DisplayName("유효하지 않은 요청으로 카테고리 업데이트 시 400 응답을 반환한다")
        void updateCategory_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
            // given
            Long categoryId = 1L;
            UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .displayName("") // 표시 이름 없음
                .description("업데이트된 설명")
                .build();

            // when & then
            mockMvc.perform(put("/api/v1/categories/{id}", categoryId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

            verify(categoryCommandUseCase, never()).updateCategory(any());
        }
    }

    @Nested
    @DisplayName("카테고리 삭제 API 테스트")
    class DeleteCategoryTest {

        @Test
        @DisplayName("카테고리를 삭제할 수 있다")
        void deleteCategory_WithExistingId_ShouldReturnNoContent() throws Exception {
            // given
            Long categoryId = 1L;
            doNothing().when(categoryCommandUseCase).deleteCategory(categoryId);

            // when & then
            mockMvc.perform(delete("/api/v1/categories/{id}", categoryId))
                .andExpect(status().isNoContent());

            verify(categoryCommandUseCase).deleteCategory(categoryId);
        }

        @Test
        @DisplayName("존재하지 않는 카테고리 삭제 시 404 응답을 반환한다")
        void deleteCategory_WithNonExistingId_ShouldReturnNotFound() throws Exception {
            // given
            Long nonExistingId = 999L;
            doThrow(new CategoryNotFoundException(nonExistingId))
                .when(categoryCommandUseCase).deleteCategory(nonExistingId);

            // when & then
            mockMvc.perform(delete("/api/v1/categories/{id}", nonExistingId))
                .andExpect(status().isNotFound());

            verify(categoryCommandUseCase).deleteCategory(nonExistingId);
        }
    }
}
