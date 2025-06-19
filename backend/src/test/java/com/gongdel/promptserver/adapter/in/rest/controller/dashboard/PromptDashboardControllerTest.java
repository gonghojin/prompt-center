package com.gongdel.promptserver.adapter.in.rest.controller.dashboard;

import com.gongdel.promptserver.adapter.in.rest.BaseControllerTest;
import com.gongdel.promptserver.application.port.in.PromptDashboardQueryUseCase;
import com.gongdel.promptserver.domain.model.PromptSearchResult;
import com.gongdel.promptserver.domain.model.PromptStats;
import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.model.Visibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PromptDashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class PromptDashboardControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PromptDashboardQueryUseCase promptDashboardQueryUseCase;

    private PromptSearchResult buildMockPrompt(Long id) {
        return PromptSearchResult.builder()
            .id(id)
            .uuid(UUID.randomUUID())
            .title("title" + id)
            .description("desc" + id)
            .currentVersionId(1L)
            .categoryId(1L)
            .categoryName("cat")
            .createdById(1L)
            .createdByName("user")
            .tags(Collections.emptyList())
            .visibility(Visibility.PUBLIC)
            .status(PromptStatus.PUBLISHED)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .stats(new PromptStats())
            .isFavorite(false)
            .isLiked(false)
            .build();
    }

    @Nested
    @DisplayName("최근 프롬프트 목록 API")
    class GetRecentPrompts {
        @Test
        @DisplayName("정상적으로 최근 프롬프트 목록을 반환한다")
        void getRecentPrompts_success() throws Exception {
            // Given
            int pageSize = 3;
            List<PromptSearchResult> mockResults = List.of(
                buildMockPrompt(1L), buildMockPrompt(2L), buildMockPrompt(3L));
            when(promptDashboardQueryUseCase.getRecentPrompts(pageSize)).thenReturn(mockResults);

            // When & Then
            mockMvc.perform(get("/api/v1/dashboard/prompts/recent")
                    .param("pageSize", String.valueOf(pageSize))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryId").value(1L))
                .andExpect(jsonPath("$[0].title").value("title1"));
        }

        @Test
        @DisplayName("pageSize가 0 이하이면 기본값 4로 동작한다")
        void getRecentPrompts_zeroOrNegativePageSize() throws Exception {
            // Given
            int defaultPageSize = 4;
            List<PromptSearchResult> mockResults = List.of(
                buildMockPrompt(1L), buildMockPrompt(2L), buildMockPrompt(3L), buildMockPrompt(4L));
            when(promptDashboardQueryUseCase.getRecentPrompts(defaultPageSize)).thenReturn(mockResults);

            // When & Then
            mockMvc.perform(get("/api/v1/dashboard/prompts/recent")
                    .param("pageSize", "0")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryId").value(1L))
                .andExpect(jsonPath("$[0].title").value("title1"));
        }

        @Test
        @DisplayName("서비스에서 예외 발생 시 500 반환")
        void getRecentPrompts_serviceException() throws Exception {
            // Given
            int pageSize = 2;
            when(promptDashboardQueryUseCase.getRecentPrompts(anyInt()))
                .thenThrow(new RuntimeException("DB error"));

            // When & Then
            mockMvc.perform(get("/api/v1/dashboard/prompts/recent")
                    .param("pageSize", String.valueOf(pageSize))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        }
    }
}
