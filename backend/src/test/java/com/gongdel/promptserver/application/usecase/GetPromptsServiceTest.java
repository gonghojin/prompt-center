package com.gongdel.promptserver.application.usecase;

import com.gongdel.promptserver.application.port.out.LoadPromptPort;
import com.gongdel.promptserver.domain.model.PromptTemplate;
import com.gongdel.promptserver.domain.model.PromptValidationException;
import com.gongdel.promptserver.domain.model.User;
import com.gongdel.promptserver.domain.model.UserRole;
import com.gongdel.promptserver.domain.model.Visibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GetPromptsServiceTest {

    @Mock
    private LoadPromptPort loadPromptPort;

    @InjectMocks
    private GetPromptsService getPromptsService;

    private UUID promptId;
    private UUID authorId;
    private PromptTemplate promptTemplate;
    private List<PromptTemplate> promptTemplates;

    @BeforeEach
    void setUp() throws PromptValidationException {
        // 테스트에 사용할 고정 ID 생성
        promptId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        authorId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        // 테스트용 사용자 생성
        User author = User.builder()
                .id(authorId)
                .name("테스트 작성자")
                .email("author@example.com")
                .role(UserRole.ROLE_USER)
                .build();

        // 테스트용 프롬프트 생성
        promptTemplate = PromptTemplate.builder()
                .id(promptId)
                .title("테스트 프롬프트")
                .description("테스트 프롬프트 설명")
                .content("테스트 프롬프트 내용")
                .author(author)
                .visibility(Visibility.PUBLIC)
                .build();

        // 테스트용 프롬프트 목록 생성
        promptTemplates = List.of(promptTemplate);
    }

    @Test
    @DisplayName("모든 프롬프트 목록을 조회한다")
    void getAllPrompts_ShouldReturnAllPrompts() {
        // given
        given(loadPromptPort.loadAllPrompts()).willReturn(promptTemplates);

        // when
        List<PromptTemplate> result = getPromptsService.getAllPrompts();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).containsExactlyElementsOf(promptTemplates);

        // loadAllPrompts 메소드가 호출되었는지 확인
        verify(loadPromptPort).loadAllPrompts();
    }

    @Test
    @DisplayName("공개 프롬프트 목록만 조회한다")
    void getPublicPrompts_ShouldReturnOnlyPublicPrompts() {
        // given
        given(loadPromptPort.loadPublicPrompts()).willReturn(promptTemplates);

        // when
        List<PromptTemplate> result = getPromptsService.getPublicPrompts();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).containsExactlyElementsOf(promptTemplates);
        assertThat(result.get(0).isPublic()).isTrue();

        // loadPublicPrompts 메소드가 호출되었는지 확인
        verify(loadPromptPort).loadPublicPrompts();
    }

    @Test
    @DisplayName("작성자 ID로 프롬프트 목록을 조회한다")
    void getPromptsByAuthor_ShouldReturnPromptsForSpecificAuthor() {
        // given
        given(loadPromptPort.loadPromptsByAuthor(authorId)).willReturn(promptTemplates);

        // when
        List<PromptTemplate> result = getPromptsService.getPromptsByAuthor(authorId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).containsExactlyElementsOf(promptTemplates);
        assertThat(result.get(0).getAuthor().getId()).isEqualTo(authorId);

        // loadPromptsByAuthor 메소드가 호출되었는지 확인
        verify(loadPromptPort).loadPromptsByAuthor(authorId);
    }

    @Test
    @DisplayName("키워드로 프롬프트를 검색한다")
    void searchPrompts_WithKeyword_ShouldReturnMatchingPrompts() {
        // given
        String keyword = "테스트";
        given(loadPromptPort.searchPrompts(keyword)).willReturn(promptTemplates);

        // when
        List<PromptTemplate> result = getPromptsService.searchPrompts(keyword);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).containsExactlyElementsOf(promptTemplates);

        // searchPrompts 메소드가 호출되었는지 확인
        verify(loadPromptPort).searchPrompts(keyword);
    }

    @Test
    @DisplayName("빈 키워드로 검색 시 빈 목록을 반환한다")
    void searchPrompts_WithEmptyKeyword_ShouldReturnEmptyList() {
        // given
        String emptyKeyword = "";

        // when
        List<PromptTemplate> result = getPromptsService.searchPrompts(emptyKeyword);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("null 키워드로 검색 시 빈 목록을 반환한다")
    void searchPrompts_WithNullKeyword_ShouldReturnEmptyList() {
        // given
        String nullKeyword = null;

        // when
        List<PromptTemplate> result = getPromptsService.searchPrompts(nullKeyword);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("ID로 프롬프트를 조회한다")
    void getPromptById_WithExistingId_ShouldReturnPrompt() {
        // given
        given(loadPromptPort.loadPrompt(promptId)).willReturn(Optional.of(promptTemplate));

        // when
        Optional<PromptTemplate> result = getPromptsService.getPromptById(promptId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(promptTemplate);

        // loadPrompt 메소드가 호출되었는지 확인
        verify(loadPromptPort).loadPrompt(promptId);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 프롬프트 조회 시 빈 Optional을 반환한다")
    void getPromptById_WithNonExistingId_ShouldReturnEmptyOptional() {
        // given
        UUID nonExistingId = UUID.randomUUID();
        given(loadPromptPort.loadPrompt(nonExistingId)).willReturn(Optional.empty());

        // when
        Optional<PromptTemplate> result = getPromptsService.getPromptById(nonExistingId);

        // then
        assertThat(result).isEmpty();

        // loadPrompt 메소드가 호출되었는지 확인
        verify(loadPromptPort).loadPrompt(nonExistingId);
    }
}
