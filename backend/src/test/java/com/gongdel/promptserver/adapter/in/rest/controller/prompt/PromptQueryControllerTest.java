package com.gongdel.promptserver.adapter.in.rest.controller.prompt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gongdel.promptserver.adapter.in.rest.BaseControllerTest;
import com.gongdel.promptserver.application.port.in.PromptsQueryUseCase;
import com.gongdel.promptserver.domain.model.PromptDetail;
import com.gongdel.promptserver.domain.model.PromptSearchResult;
import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.model.Visibility;
import com.gongdel.promptserver.domain.user.Email;
import com.gongdel.promptserver.domain.user.User;
import com.gongdel.promptserver.domain.user.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PromptQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
class PromptQueryControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PromptsQueryUseCase promptsQueryUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("프롬프트 단건 조회 API")
    class GetPrompt {

        @Test
        @DisplayName("존재하는 프롬프트 ID로 조회 시 200 OK와 상세 정보 반환")
        void getPrompt_success() throws Exception {
            UUID promptId = UUID.randomUUID();
            PromptDetail detail = PromptDetail.builder()
                .id(promptId)
                .author(User.builder()
                    .uuid(new UserId(UUID.randomUUID()))
                    .name("홍길동")
                    .email(new Email("test@naver.com"))
                    .build())
                .title("테스트 프롬프트")
                .build();
            when(promptsQueryUseCase.loadPromptDetailByUuid(any()))
                .thenReturn(Optional.of(detail));

            mockMvc.perform(get("/api/v1/prompts/{id}", promptId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(promptId.toString()))
                .andExpect(jsonPath("$.title").value("테스트 프롬프트"));
        }

        @Test
        @DisplayName("존재하지 않는 프롬프트 ID로 조회 시 404 Not Found 반환")
        void getPrompt_notFound() throws Exception {
            UUID promptId = UUID.randomUUID();
            when(promptsQueryUseCase.loadPromptDetailByUuid(any()))
                .thenReturn(Optional.empty());

            mockMvc.perform(get("/api/v1/prompts/{id}", promptId))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("프롬프트 복합 검색 API")
    class SearchPromptsAdvanced {

        @Test
        @DisplayName("파라미터 없이 호출 시 200 OK와 결과 반환")
        void searchPromptsAdvanced_default() throws Exception {
            PromptSearchResult result = PromptSearchResult.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .title("테스트 프롬프트")
                .description("설명")
                .currentVersionId(1L)
                .categoryId(1L)
                .categoryName("카테고리")
                .createdById(1L)
                .createdByName("홍길동")
                .tags(Collections.singletonList("태그"))
                .visibility(Visibility.PUBLIC)
                .status(PromptStatus.PUBLISHED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            Pageable pageable = PageRequest.of(0, 20);
            when(promptsQueryUseCase.searchPrompts(any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(result), pageable, 1));

            mockMvc.perform(get("/api/v1/prompts/advanced-search")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("테스트 프롬프트"));
        }

        @Test
        @DisplayName("status 값이 잘못된 경우 기본값(PUBLISHED)로 동작")
        void searchPromptsAdvanced_invalidStatus() throws Exception {
            PromptSearchResult result = PromptSearchResult.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .title("테스트 프롬프트")
                .description("설명")
                .currentVersionId(1L)
                .categoryId(1L)
                .categoryName("카테고리")
                .createdById(1L)
                .createdByName("홍길동")
                .tags(Collections.singletonList("태그"))
                .visibility(Visibility.PUBLIC)
                .status(PromptStatus.PUBLISHED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            Pageable pageable = PageRequest.of(0, 20);
            when(promptsQueryUseCase.searchPrompts(any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(result), pageable, 1));

            mockMvc.perform(get("/api/v1/prompts/advanced-search")
                    .param("status", "INVALID_STATUS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("테스트 프롬프트"));
        }

        @Test
        @DisplayName("여러 파라미터 조합으로 호출 시 200 OK와 결과 반환")
        void searchPromptsAdvanced_withParams() throws Exception {
            PromptSearchResult result = PromptSearchResult.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .title("테스트 프롬프트")
                .description("설명")
                .currentVersionId(1L)
                .categoryId(1L)
                .categoryName("카테고리")
                .createdById(1L)
                .createdByName("홍길동")
                .tags(Collections.singletonList("태그"))
                .visibility(Visibility.PUBLIC)
                .status(PromptStatus.PUBLISHED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            Pageable pageable = PageRequest.of(0, 20);
            when(promptsQueryUseCase.searchPrompts(any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(result), pageable, 1));

            mockMvc.perform(get("/api/v1/prompts/advanced-search")
                    .param("title", "테스트 프롬프트")
                    .param("description", "추천")
                    .param("tag", "stable-diffusion")
                    .param("categoryId", "1")
                    .param("status", "PUBLISHED")
                    .param("sortType", "TITLE")
                    .param("page", "0")
                    .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("테스트 프롬프트"));
        }
    }
}
