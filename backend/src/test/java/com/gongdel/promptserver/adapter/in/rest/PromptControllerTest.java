package com.gongdel.promptserver.adapter.in.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gongdel.promptserver.adapter.in.rest.request.CreatePromptRequest;
import com.gongdel.promptserver.application.port.in.GetPromptsUseCase;
import com.gongdel.promptserver.application.port.in.RegisterPromptUseCase;
import com.gongdel.promptserver.domain.model.PromptTemplate;
import com.gongdel.promptserver.domain.model.PromptValidationException;
import com.gongdel.promptserver.domain.model.User;
import com.gongdel.promptserver.domain.model.Visibility;

import java.util.List;
import java.util.Optional;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PromptControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RegisterPromptUseCase registerPromptUseCase;

    @Mock
    private GetPromptsUseCase getPromptsUseCase;

    @InjectMocks
    private PromptController promptController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = standaloneSetup(promptController).build();
    }

    @Test
    @DisplayName("프롬프트 생성 API 호출 시 성공적으로 프롬프트가 생성된다")
    void createPromptSuccessfully() throws Exception, PromptValidationException {
        // given
        CreatePromptRequest request = new CreatePromptRequest();
        request.setTitle("테스트 프롬프트");
        request.setDescription("프롬프트 설명");
        request.setContent("프롬프트 내용");
        request.setPublic(true);

        UUID promptId = UUID.randomUUID();
        PromptTemplate prompt = PromptTemplate.builder()
                .id(promptId)
                .title("테스트 프롬프트")
                .description("프롬프트 설명")
                .content("프롬프트 내용")
                .author(User.builder().id(UUID.randomUUID()).name("임시 사용자").email("temp@example.com").build())
                .visibility(Visibility.PUBLIC)
                .build();

        when(registerPromptUseCase.registerPrompt(any())).thenReturn(prompt);

        // when & then
        mockMvc.perform(post("/api/v1/prompts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(promptId.toString()))
                .andExpect(jsonPath("$.title").value("테스트 프롬프트"))
                .andExpect(jsonPath("$.description").value("프롬프트 설명"))
                .andExpect(jsonPath("$.content").value("프롬프트 내용"))
                .andExpect(jsonPath("$.public").value(true));
    }

    @Test
    @DisplayName("ID로 프롬프트 조회 API 호출 시 해당 프롬프트가 반환된다")
    void getPromptByIdSuccessfully() throws Exception, PromptValidationException {
        // given
        UUID promptId = UUID.randomUUID();
        PromptTemplate prompt = PromptTemplate.builder()
                .id(promptId)
                .title("테스트 프롬프트")
                .description("프롬프트 설명")
                .content("프롬프트 내용")
                .author(User.builder().id(UUID.randomUUID()).name("임시 사용자").email("temp@example.com").build())
                .visibility(Visibility.PUBLIC)
                .build();

        when(getPromptsUseCase.getPromptById(promptId)).thenReturn(Optional.of(prompt));

        // when & then
        mockMvc.perform(get("/api/v1/prompts/{id}", promptId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(promptId.toString()))
                .andExpect(jsonPath("$.title").value("테스트 프롬프트"))
                .andExpect(jsonPath("$.description").value("프롬프트 설명"))
                .andExpect(jsonPath("$.content").value("프롬프트 내용"))
                .andExpect(jsonPath("$.public").value(true));
    }

    @Test
    @DisplayName("존재하지 않는 ID로 프롬프트 조회 시 404 응답을 반환한다")
    void getPromptByIdNotFoundReturns404() throws Exception {
        // given
        UUID nonExistentId = UUID.randomUUID();
        when(getPromptsUseCase.getPromptById(nonExistentId)).thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(get("/api/v1/prompts/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("모든 프롬프트 목록 조회 API 호출 시 프롬프트 목록이 반환된다")
    void getAllPromptsSuccessfully() throws Exception, PromptValidationException {
        // given
        UUID promptId1 = UUID.randomUUID();
        UUID promptId2 = UUID.randomUUID();

        PromptTemplate prompt1 = PromptTemplate.builder()
                .id(promptId1)
                .title("테스트 프롬프트1")
                .description("프롬프트 설명1")
                .content("프롬프트 내용1")
                .author(User.builder().id(UUID.randomUUID()).name("임시 사용자").email("temp@example.com").build())
                .visibility(Visibility.PUBLIC)
                .build();

        PromptTemplate prompt2 = PromptTemplate.builder()
                .id(promptId2)
                .title("테스트 프롬프트2")
                .description("프롬프트 설명2")
                .content("프롬프트 내용2")
                .author(User.builder().id(UUID.randomUUID()).name("임시 사용자").email("temp@example.com").build())
                .visibility(Visibility.PRIVATE)
                .build();

        List<PromptTemplate> prompts = List.of(prompt1, prompt2);
        when(getPromptsUseCase.getAllPrompts()).thenReturn(prompts);

        // when & then
        mockMvc.perform(get("/api/v1/prompts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(promptId1.toString()))
                .andExpect(jsonPath("$[1].id").value(promptId2.toString()));
    }

    @Test
    @DisplayName("공개 프롬프트 목록 조회 API 호출 시 공개 프롬프트 목록이 반환된다")
    void getPublicPromptsSuccessfully() throws Exception, PromptValidationException {
        // given
        UUID promptId1 = UUID.randomUUID();

        PromptTemplate prompt1 = PromptTemplate.builder()
                .id(promptId1)
                .title("공개 프롬프트")
                .description("공개 프롬프트 설명")
                .content("공개 프롬프트 내용")
                .author(User.builder().id(UUID.randomUUID()).name("임시 사용자").email("temp@example.com").build())
                .visibility(Visibility.PUBLIC)
                .build();

        List<PromptTemplate> prompts = List.of(prompt1);
        when(getPromptsUseCase.getPublicPrompts()).thenReturn(prompts);

        // when & then
        mockMvc.perform(get("/api/v1/prompts/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(promptId1.toString()))
                .andExpect(jsonPath("$[0].title").value("공개 프롬프트"))
                .andExpect(jsonPath("$[0].public").value(true));
    }

    @Test
    @DisplayName("작성자 ID로 프롬프트 목록 조회 API 호출 시 해당 작성자의 프롬프트 목록이 반환된다")
    void getPromptsByAuthorSuccessfully() throws Exception, PromptValidationException {
        // given
        UUID authorId = UUID.randomUUID();
        UUID promptId1 = UUID.randomUUID();

        User author = User.builder()
                .id(authorId)
                .name("테스트 사용자")
                .email("test@example.com")
                .build();

        PromptTemplate prompt1 = PromptTemplate.builder()
                .id(promptId1)
                .title("작성자 프롬프트")
                .description("작성자 프롬프트 설명")
                .content("작성자 프롬프트 내용")
                .author(author)
                .visibility(Visibility.PUBLIC)
                .build();

        List<PromptTemplate> prompts = List.of(prompt1);
        when(getPromptsUseCase.getPromptsByAuthor(authorId)).thenReturn(prompts);

        // when & then
        mockMvc.perform(get("/api/v1/prompts/author/{authorId}", authorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(promptId1.toString()))
                .andExpect(jsonPath("$[0].title").value("작성자 프롬프트"));
    }

    @Test
    @DisplayName("키워드로 프롬프트 검색 API 호출 시 검색 결과가 반환된다")
    void searchPromptsSuccessfully() throws Exception, PromptValidationException {
        // given
        UUID promptId1 = UUID.randomUUID();

        PromptTemplate prompt1 = PromptTemplate.builder()
                .id(promptId1)
                .title("검색 프롬프트")
                .description("검색 프롬프트 설명")
                .content("검색 프롬프트 내용")
                .author(User.builder().id(UUID.randomUUID()).name("임시 사용자").email("temp@example.com").build())
                .visibility(Visibility.PUBLIC)
                .build();

        List<PromptTemplate> prompts = List.of(prompt1);
        when(getPromptsUseCase.searchPrompts("검색")).thenReturn(prompts);

        // when & then
        mockMvc.perform(get("/api/v1/prompts/search")
                .param("keyword", "검색"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(promptId1.toString()))
                .andExpect(jsonPath("$[0].title").value("검색 프롬프트"));
    }
}
