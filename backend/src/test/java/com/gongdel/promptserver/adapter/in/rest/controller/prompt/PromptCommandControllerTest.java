package com.gongdel.promptserver.adapter.in.rest.controller.prompt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gongdel.promptserver.adapter.in.rest.BaseControllerTest;
import com.gongdel.promptserver.adapter.in.rest.request.prompt.CreatePromptRequest;
import com.gongdel.promptserver.adapter.in.rest.request.prompt.InputVariableDto;
import com.gongdel.promptserver.application.dto.RegisterPromptResponse;
import com.gongdel.promptserver.application.port.in.PromptCommandUseCase;
import com.gongdel.promptserver.application.port.in.command.RegisterPromptCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PromptCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
class PromptCommandControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PromptCommandUseCase registerPromptUseCase;

    @Test
    @DisplayName("프롬프트 생성 성공 - 모든 필수값 입력")
    void create_prompt_success_with_all_required_fields() throws Exception {
        // given
        CreatePromptRequest request = CreatePromptRequest.builder()
            .title("테스트 프롬프트")
            .description("설명")
            .content("내용")
            .tags(Set.of("tag1", "tag2"))
            .inputVariables(List.of(
                InputVariableDto.builder().name("var1").description("desc").build()))
            .categoryId(1L)
            .visibility("PRIVATE")
            .status("DRAFT")
            .build();

        RegisterPromptResponse response = RegisterPromptResponse.builder()
            .uuid(UUID.randomUUID())
            .title(request.getTitle())
            .description(request.getDescription())
            .categoryId(request.getCategoryId())
            .visibility(request.getVisibility())
            .status(request.getStatus())
            .tags(List.copyOf(request.getTags()))
            .build();

        when(registerPromptUseCase.registerPrompt(ArgumentMatchers.any(RegisterPromptCommand.class)))
            .thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/prompts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value(request.getTitle()))
            .andExpect(jsonPath("$.description").value(request.getDescription()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    @DisplayName("프롬프트 생성 실패 - title 누락")
    void create_prompt_fail_missing_title() throws Exception {
        // given: title이 누락된 요청
        CreatePromptRequest request = CreatePromptRequest.builder()
            .description("설명")
            .content("내용")
            .build();

        // when & then
        mockMvc.perform(post("/api/v1/prompts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    @DisplayName("프롬프트 생성 성공 - 입력 변수와 태그가 null")
    void create_prompt_success_with_null_input_variables_and_tags() throws Exception {
        // given
        CreatePromptRequest request = CreatePromptRequest.builder()
            .title("테스트 프롬프트")
            .description("설명")
            .content("내용")
            .inputVariables(null)
            .tags(null)
            .categoryId(1L)
            .visibility("PRIVATE")
            .status("DRAFT")
            .build();

        RegisterPromptResponse response = RegisterPromptResponse.builder()
            .uuid(UUID.randomUUID())
            .title(request.getTitle())
            .description(request.getDescription())
            .categoryId(request.getCategoryId())
            .visibility(request.getVisibility())
            .status(request.getStatus())
            .tags(null)
            .build();

        when(registerPromptUseCase.registerPrompt(ArgumentMatchers.any(RegisterPromptCommand.class)))
            .thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/prompts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value(request.getTitle()))
            .andExpect(jsonPath("$.description").value(request.getDescription()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    @DisplayName("프롬프트 생성 성공 - 입력 변수와 태그가 빈 컬렉션")
    void create_prompt_success_with_empty_input_variables_and_tags() throws Exception {
        // given
        CreatePromptRequest request = CreatePromptRequest.builder()
            .title("테스트 프롬프트")
            .description("설명")
            .content("내용")
            .inputVariables(List.of())
            .tags(Set.of())
            .categoryId(1L)
            .visibility("PRIVATE")
            .status("DRAFT")
            .build();

        RegisterPromptResponse response = RegisterPromptResponse.builder()
            .uuid(UUID.randomUUID())
            .title(request.getTitle())
            .description(request.getDescription())
            .categoryId(request.getCategoryId())
            .visibility(request.getVisibility())
            .status(request.getStatus())
            .tags(List.of())
            .build();

        when(registerPromptUseCase.registerPrompt(ArgumentMatchers.any(RegisterPromptCommand.class)))
            .thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/prompts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value(request.getTitle()))
            .andExpect(jsonPath("$.description").value(request.getDescription()));
    }
}
