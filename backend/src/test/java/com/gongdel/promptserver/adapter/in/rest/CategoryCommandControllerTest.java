package com.gongdel.promptserver.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gongdel.promptserver.adapter.in.rest.request.CreateCategoryRequest;
import com.gongdel.promptserver.adapter.in.rest.request.UpdateCategoryRequest;
import com.gongdel.promptserver.application.exception.CategoryDuplicateNameException;
import com.gongdel.promptserver.application.exception.CategoryNotFoundException;
import com.gongdel.promptserver.application.port.in.CategoryCommandUseCase;
import com.gongdel.promptserver.domain.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryCommandController.class)
class CategoryCommandControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryCommandUseCase categoryCommandUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("카테고리 생성 성공")
    void createCategory_success() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("test", "테스트", "설명", null, false);
        Category category = new Category(1L, "test", "테스트", "설명", null, false, null, null);
        Mockito.when(categoryCommandUseCase.createCategory(any())).thenReturn(category);

        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("test"));
    }

    @Test
    @DisplayName("카테고리 생성 실패 - 중복")
    void createCategory_duplicateName() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("dup", "중복", "설명", null, false);
        Mockito.when(categoryCommandUseCase.createCategory(any())).thenThrow(new CategoryDuplicateNameException("dup"));

        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("카테고리 수정 성공")
    void updateCategory_success() throws Exception {
        UpdateCategoryRequest request = new UpdateCategoryRequest("수정", "수정설명", null);
        Category category = new Category(1L, "test", "수정", "수정설명", null, false, null, null);
        Mockito.when(categoryCommandUseCase.updateCategory(any())).thenReturn(category);

        mockMvc.perform(put("/api/v1/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.displayName").value("수정"));
    }

    @Test
    @DisplayName("카테고리 수정 실패 - 존재하지 않음")
    void updateCategory_notFound() throws Exception {
        UpdateCategoryRequest request = new UpdateCategoryRequest("수정", "수정설명", null);
        Mockito.when(categoryCommandUseCase.updateCategory(any())).thenThrow(new CategoryNotFoundException(1L));

        mockMvc.perform(put("/api/v1/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("카테고리 삭제 성공")
    void deleteCategory_success() throws Exception {
        Mockito.doNothing().when(categoryCommandUseCase).deleteCategory(1L);

        mockMvc.perform(delete("/api/v1/categories/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("카테고리 삭제 실패 - 존재하지 않음")
    void deleteCategory_notFound() throws Exception {
        Mockito.doThrow(new CategoryNotFoundException(1L)).when(categoryCommandUseCase).deleteCategory(1L);

        mockMvc.perform(delete("/api/v1/categories/1"))
            .andExpect(status().isNotFound());
    }
}
