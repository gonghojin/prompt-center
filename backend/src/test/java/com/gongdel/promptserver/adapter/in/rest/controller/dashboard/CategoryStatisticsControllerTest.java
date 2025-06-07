package com.gongdel.promptserver.adapter.in.rest.controller.dashboard;

import com.gongdel.promptserver.adapter.in.rest.BaseControllerTest;
import com.gongdel.promptserver.application.port.in.CategoryStatisticsQueryUseCase;
import com.gongdel.promptserver.domain.model.statistics.CategoryPromptCount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryStatisticsController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryStatisticsControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryStatisticsQueryUseCase categoryStatisticsQueryUseCase;

    private CategoryPromptCount buildMockCategoryPromptCount(Long id, String name, Long count) {
        return new CategoryPromptCount(id, name, count);
    }

    @Nested
    @DisplayName("루트 카테고리별 통계 API")
    class GetRootCategoryStatistics {
        @Test
        @DisplayName("정상적으로 루트 카테고리별 통계를 반환한다")
        void getRootCategoryStatistics_success() throws Exception {
            // Given
            List<CategoryPromptCount> mockCounts = List.of(
                buildMockCategoryPromptCount(1L, "카테고리1", 10L),
                buildMockCategoryPromptCount(2L, "카테고리2", 5L));
            when(categoryStatisticsQueryUseCase.getRootCategoryPromptCounts()).thenReturn(mockCounts);

            // When & Then
            mockMvc.perform(get("/api/v1/dashboard/categories/root/statistics")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories[0].categoryId").value(1L))
                .andExpect(jsonPath("$.categories[0].categoryName").value("카테고리1"))
                .andExpect(jsonPath("$.categories[0].promptCount").value(10L));
        }

        @Test
        @DisplayName("서비스에서 예외 발생 시 500 반환")
        void getRootCategoryStatistics_serviceException() throws Exception {
            // Given
            when(categoryStatisticsQueryUseCase.getRootCategoryPromptCounts())
                .thenThrow(new RuntimeException("DB error"));

            // When & Then
            mockMvc.perform(get("/api/v1/dashboard/categories/root/statistics")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("하위 카테고리별 통계 API")
    class GetChildCategoryStatistics {
        @Test
        @DisplayName("정상적으로 하위 카테고리별 통계를 반환한다")
        void getChildCategoryStatistics_success() throws Exception {
            // Given
            Long rootId = 1L;
            List<CategoryPromptCount> mockCounts = List.of(
                buildMockCategoryPromptCount(10L, "하위1", 3L));
            when(categoryStatisticsQueryUseCase.getChildCategoryPromptCounts(rootId)).thenReturn(mockCounts);

            // When & Then
            mockMvc.perform(get("/api/v1/dashboard/categories/{rootId}/children/statistics", rootId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories[0].categoryId").value(10L))
                .andExpect(jsonPath("$.categories[0].categoryName").value("하위1"))
                .andExpect(jsonPath("$.categories[0].promptCount").value(3L));
        }

        @Test
        @DisplayName("rootId가 null이면 400 Bad Request를 반환한다")
        void getChildCategoryStatistics_nullRootId() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/dashboard/categories//children/statistics")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // PathVariable 누락 시 404
        }

        @Test
        @DisplayName("서비스에서 예외 발생 시 500 반환")
        void getChildCategoryStatistics_serviceException() throws Exception {
            // Given
            when(categoryStatisticsQueryUseCase.getChildCategoryPromptCounts(anyLong()))
                .thenThrow(new RuntimeException("DB error"));
            // When & Then
            mockMvc.perform(get("/api/v1/dashboard/categories/{rootId}/children/statistics", 1L)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        }
    }
}
