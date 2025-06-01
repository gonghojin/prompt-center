package com.gongdel.promptserver.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gongdel.promptserver.application.exception.CategoryNotFoundException;
import com.gongdel.promptserver.application.port.in.CategoryQueryUseCase;
import com.gongdel.promptserver.domain.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryQueryController.class)
class CategoryQueryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryQueryUseCase categoryQueryUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("단일 카테고리 조회 성공")
    void getCategoryById_success() throws Exception {
        Category category = new Category(1L, "test", "테스트", "설명", null, false, null, null);
        Mockito.when(categoryQueryUseCase.getCategoryById(1L)).thenReturn(Optional.of(category));

        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("test"));
    }

    @Test
    @DisplayName("단일 카테고리 조회 실패 - 존재하지 않음")
    void getCategoryById_notFound() throws Exception {
        Mockito.when(categoryQueryUseCase.getCategoryById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("전체 카테고리 목록 조회")
    void getAllCategories() throws Exception {
        Category c1 = new Category(1L, "test1", "테스트1", "설명1", null, false, null, null);
        Category c2 = new Category(2L, "test2", "테스트2", "설명2", null, false, null, null);
        Mockito.when(categoryQueryUseCase.getAllCategories()).thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @DisplayName("이름으로 카테고리 조회 성공")
    void getCategoryByName_success() throws Exception {
        Category category = new Category(1L, "test", "테스트", "설명", null, false, null, null);
        Mockito.when(categoryQueryUseCase.getCategoryByName("test")).thenReturn(Optional.of(category));

        mockMvc.perform(get("/api/v1/categories?name=test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("이름으로 카테고리 조회 실패 - 존재하지 않음")
    void getCategoryByName_notFound() throws Exception {
        Mockito.when(categoryQueryUseCase.getCategoryByName("notfound")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/categories?name=notfound"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("시스템 카테고리 목록 조회")
    void getCategoriesBySystemFlag() throws Exception {
        Category c1 = new Category(1L, "sys1", "시스템1", "설명1", null, true, null, null);
        Mockito.when(categoryQueryUseCase.getCategoriesBySystemFlag(true)).thenReturn(List.of(c1));

        mockMvc.perform(get("/api/v1/categories?isSystem=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isSystem").value(true));
    }

    @Test
    @DisplayName("루트 카테고리 목록 조회")
    void getRootCategories() throws Exception {
        Category c1 = new Category(1L, "root1", "루트1", "설명1", null, false, null, null);
        Mockito.when(categoryQueryUseCase.getRootCategories()).thenReturn(List.of(c1));

        mockMvc.perform(get("/api/v1/categories/roots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("하위 카테고리 목록 조회 성공")
    void getSubCategories_success() throws Exception {
        Category c1 = new Category(2L, "sub1", "서브1", "설명1", null, false, null, null);
        Mockito.when(categoryQueryUseCase.getSubCategories(1L)).thenReturn(List.of(c1));

        mockMvc.perform(get("/api/v1/categories/1/subcategories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L));
    }

    @Test
    @DisplayName("하위 카테고리 목록 조회 실패 - 상위 카테고리 없음")
    void getSubCategories_notFound() throws Exception {
        Mockito.when(categoryQueryUseCase.getSubCategories(1L)).thenThrow(new CategoryNotFoundException(1L));

        mockMvc.perform(get("/api/v1/categories/1/subcategories"))
                .andExpect(status().isNotFound());
    }
}
