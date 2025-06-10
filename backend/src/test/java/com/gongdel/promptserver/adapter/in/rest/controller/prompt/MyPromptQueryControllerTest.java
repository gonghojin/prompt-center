package com.gongdel.promptserver.adapter.in.rest.controller.prompt;

import com.gongdel.promptserver.adapter.in.rest.BaseControllerTest;
import com.gongdel.promptserver.application.port.in.MyPromptsQueryUseCase;
import com.gongdel.promptserver.application.port.in.query.FavoriteQueryUseCase;
import com.gongdel.promptserver.domain.model.PromptStats;
import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.model.favorite.FavoritePromptResult;
import com.gongdel.promptserver.domain.model.favorite.FavoriteSearchCondition;
import com.gongdel.promptserver.domain.model.statistics.PromptStatisticsResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MyPromptQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
class MyPromptQueryControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MyPromptsQueryUseCase myPromptsQueryUseCase;

    @MockBean
    private FavoriteQueryUseCase favoriteQueryUseCase;

    @Nested
    @DisplayName("getMyPrompts()")
    class GetMyPrompts {

        @Test
        @DisplayName("정상 요청 시 내 프롬프트 목록을 반환한다")
        void givenValidRequest_whenGetMyPrompts_thenReturnsPromptList() throws Exception {
            // Given
            java.util.UUID uuid = java.util.UUID.randomUUID();
            com.gongdel.promptserver.domain.model.PromptSearchResult searchResult = com.gongdel.promptserver.domain.model.PromptSearchResult
                .builder()
                .id(1L)
                .uuid(uuid)
                .title("test")
                .description("desc")
                .currentVersionId(1L)
                .categoryId(1L)
                .categoryName("cat")
                .createdById(1L)
                .createdByName("user")
                .tags(Collections.emptyList())
                .visibility(com.gongdel.promptserver.domain.model.Visibility.PUBLIC)
                .status(com.gongdel.promptserver.domain.model.PromptStatus.PUBLISHED)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();
            org.springframework.data.domain.PageImpl<com.gongdel.promptserver.domain.model.PromptSearchResult> page = new org.springframework.data.domain.PageImpl<>(
                Collections.singletonList(searchResult), PageRequest.of(0, 20), 1);
            when(myPromptsQueryUseCase.findMyPrompts(any())).thenReturn(page);

            // When & Then
            mockMvc.perform(get("/api/v1/prompts/my")
                    .param("page", "0")
                    .param("size", "20")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(uuid.toString()))
                .andExpect(jsonPath("$.content[0].title").value("test"));
        }

        @Test
        @DisplayName("page가 음수면 400 Bad Request를 반환한다")
        void givenNegativePage_whenGetMyPrompts_thenBadRequest() throws Exception {
            mockMvc.perform(get("/api/v1/prompts/my")
                    .param("page", "-1")
                    .param("size", "20")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("size가 0이면 400 Bad Request를 반환한다")
        void givenZeroSize_whenGetMyPrompts_thenBadRequest() throws Exception {
            mockMvc.perform(get("/api/v1/prompts/my")
                    .param("page", "0")
                    .param("size", "0")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("getMyPromptStatistics()")
    class GetMyPromptStatistics {

        @Test
        @DisplayName("정상 요청 시 내 프롬프트 통계를 반환한다")
        void givenValidRequest_whenGetMyPromptStatistics_thenReturnsStatistics() throws Exception {
            // Given
            PromptStatisticsResult result = PromptStatisticsResult.builder()
                .totalCount(10)
                .draftCount(2)
                .publishedCount(6)
                .archivedCount(2)
                .build();
            when(myPromptsQueryUseCase.getMyPromptStatistics(1L)).thenReturn(result);

            // When & Then
            mockMvc.perform(get("/api/v1/prompts/my/statistics")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(10))
                .andExpect(jsonPath("$.draftCount").value(2))
                .andExpect(jsonPath("$.publishedCount").value(6))
                .andExpect(jsonPath("$.archivedCount").value(2));
        }
    }

    @Nested
    @DisplayName("getMyPrompts() - 파라미터/예외/인증 케이스")
    class GetMyPromptsAdvanced {
        @Test
        @DisplayName("statusFilters, visibilityFilters, searchKeyword, sortType 조합 정상 반환")
        void givenAllParams_whenGetMyPrompts_thenReturnsPromptList() throws Exception {
            java.util.UUID uuid = java.util.UUID.randomUUID();
            com.gongdel.promptserver.domain.model.PromptSearchResult searchResult = com.gongdel.promptserver.domain.model.PromptSearchResult
                .builder()
                .id(1L)
                .uuid(uuid)
                .title("test")
                .description("desc")
                .currentVersionId(1L)
                .categoryId(1L)
                .categoryName("cat")
                .createdById(1L)
                .createdByName("user")
                .tags(Collections.emptyList())
                .visibility(com.gongdel.promptserver.domain.model.Visibility.PUBLIC)
                .status(com.gongdel.promptserver.domain.model.PromptStatus.PUBLISHED)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();
            org.springframework.data.domain.PageImpl<com.gongdel.promptserver.domain.model.PromptSearchResult> page = new org.springframework.data.domain.PageImpl<>(
                Collections.singletonList(searchResult), PageRequest.of(0, 20), 1);

            when(myPromptsQueryUseCase.findMyPrompts(any())).thenReturn(page);

            mockMvc.perform(get("/api/v1/prompts/my")
                    .param("page", "0")
                    .param("size", "20")
                    .param("statusFilters", "PUBLISHED")
                    .param("visibilityFilters", "PUBLIC")
                    .param("searchKeyword", "test")
                    .param("sortType", "LATEST_MODIFIED")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(uuid.toString()))
                .andExpect(jsonPath("$.content[0].title").value("test"));
        }

        @Test
        @DisplayName("서비스 예외 발생 시 500 반환")
        void givenServiceException_whenGetMyPrompts_thenInternalServerError() throws Exception {
            when(myPromptsQueryUseCase.findMyPrompts(any())).thenThrow(new RuntimeException("DB error"));
            mockMvc.perform(get("/api/v1/prompts/my")
                    .param("page", "0")
                    .param("size", "20"))
                .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("getMyPromptStatistics() - 예외/인증 케이스")
    class GetMyPromptStatisticsAdvanced {
        @Test
        @DisplayName("서비스 예외 발생 시 500 반환")
        void givenServiceException_whenGetMyPromptStatistics_thenInternalServerError() throws Exception {
            when(currentUserProvider.getCurrentUserId()).thenReturn(1L);
            when(myPromptsQueryUseCase.getMyPromptStatistics(1L)).thenThrow(new RuntimeException("error"));
            mockMvc.perform(get("/api/v1/prompts/my/statistics")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("getMyFavoritePrompts()")
    class GetMyFavoritePrompts {
        @Test
        @DisplayName("정상 요청 시 즐겨찾기 목록 반환")
        void givenValidRequest_whenGetMyFavoritePrompts_thenReturnsFavoriteList() throws Exception {
            FavoritePromptResult result = FavoritePromptResult.builder()
                .favoriteId(1L)
                .promptId(1L)
                .promptUuid(UUID.randomUUID())
                .title("favorite")
                .description("desc")
                .createdById(1L)
                .categoryName("category")
                .visibility(com.gongdel.promptserver.domain.model.Visibility.PUBLIC)
                .status(PromptStatus.PUBLISHED)
                .promptCreatedAt(java.time.LocalDateTime.now())
                .promptUpdatedAt(java.time.LocalDateTime.now())
                .favoriteCreatedAt(java.time.LocalDateTime.now())
                .stats(new PromptStats(100, 50))
                .build();
            PageImpl<FavoritePromptResult> page = new PageImpl<>(Collections.singletonList(result),
                PageRequest.of(0, 20), 1);
            when(favoriteQueryUseCase.searchFavorites(any(FavoriteSearchCondition.class))).thenReturn(page);
            mockMvc.perform(get("/api/v1/prompts/my/favorites")
                    .param("page", "0")
                    .param("size", "20")
                    .param("sort", "createdAt")
                    .param("order", "desc")
                    .param("searchKeyword", "fav")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("favorite"));
        }

        @Test
        @DisplayName("page가 음수면 400 Bad Request를 반환한다")
        void givenNegativePage_whenGetMyFavoritePrompts_thenBadRequest() throws Exception {
            when(currentUserProvider.getCurrentUserId()).thenReturn(1L);
            mockMvc.perform(get("/api/v1/prompts/my/favorites")
                    .param("page", "-1")
                    .param("size", "20"))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("size가 0이면 400 Bad Request를 반환한다")
        void givenZeroSize_whenGetMyFavoritePrompts_thenBadRequest() throws Exception {
            when(currentUserProvider.getCurrentUserId()).thenReturn(1L);
            mockMvc.perform(get("/api/v1/prompts/my/favorites")
                    .param("page", "0")
                    .param("size", "0"))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("서비스 예외 발생 시 500 반환")
        void givenServiceException_whenGetMyFavoritePrompts_thenInternalServerError() throws Exception {
            when(currentUserProvider.getCurrentUserId()).thenReturn(1L);
            when(favoriteQueryUseCase.searchFavorites(any(FavoriteSearchCondition.class)))
                .thenThrow(new RuntimeException("error"));
            mockMvc.perform(get("/api/v1/prompts/my/favorites")
                    .param("page", "0")
                    .param("size", "20"))
                .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("getMyFavoriteCount()")
    class GetMyFavoriteCount {
        @Test
        @DisplayName("정상 요청 시 즐겨찾기 개수 반환")
        void givenValidRequest_whenGetMyFavoriteCount_thenReturnsCount() throws Exception {
            when(currentUserProvider.getCurrentUserId()).thenReturn(1L);
            when(favoriteQueryUseCase.countByUser(1L)).thenReturn(5L);
            mockMvc.perform(get("/api/v1/prompts/my/favorites/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(5));
        }

        @Test
        @DisplayName("서비스 예외 발생 시 500 반환")
        void givenServiceException_whenGetMyFavoriteCount_thenInternalServerError() throws Exception {
            when(currentUserProvider.getCurrentUserId()).thenReturn(1L);
            when(favoriteQueryUseCase.countByUser(1L)).thenThrow(new RuntimeException("error"));
            mockMvc.perform(get("/api/v1/prompts/my/favorites/count"))
                .andExpect(status().isInternalServerError());
        }
    }
}
