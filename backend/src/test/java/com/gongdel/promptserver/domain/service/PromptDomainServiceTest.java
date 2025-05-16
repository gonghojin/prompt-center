package com.gongdel.promptserver.domain.service;

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
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PromptDomainServiceTest {

    @InjectMocks
    private PromptDomainService promptDomainService;

    private User author;
    private User otherUser;
    private PromptTemplate publicPrompt;
    private PromptTemplate privatePrompt;

    @BeforeEach
    void setUp() throws PromptValidationException {
        // 테스트용 사용자 설정
        author = User.builder()
                .id(UUID.randomUUID())
                .name("테스트 작성자")
                .email("author@example.com")
                .role(UserRole.ROLE_USER)
                .build();

        otherUser = User.builder()
                .id(UUID.randomUUID())
                .name("다른 사용자")
                .email("other@example.com")
                .role(UserRole.ROLE_USER)
                .build();

        // 테스트용 공개 프롬프트 설정
        publicPrompt = PromptTemplate.builder()
                .id(UUID.randomUUID())
                .title("공개 프롬프트")
                .description("공개 프롬프트 설명")
                .content("공개 프롬프트 내용")
                .author(author)
                .visibility(Visibility.PUBLIC)
                .build();

        // 테스트용 비공개 프롬프트 설정
        privatePrompt = PromptTemplate.builder()
                .id(UUID.randomUUID())
                .title("비공개 프롬프트")
                .description("비공개 프롬프트 설명")
                .content("비공개 프롬프트 내용")
                .author(author)
                .visibility(Visibility.PRIVATE)
                .build();
    }

    @Test
    @DisplayName("도메인 모델 생성 - 유효한 데이터로 생성 성공")
    void createPromptTemplate_WithValidData_Succeeds() throws PromptValidationException {
        // when
        PromptTemplate prompt = PromptTemplate.builder()
                .title("테스트 프롬프트")
                .description("프롬프트 설명")
                .content("프롬프트 내용")
                .author(author)
                .visibility(Visibility.PUBLIC)
                .build();

        // then
        assertThat(prompt).isNotNull();
        assertThat(prompt.getTitle()).isEqualTo("테스트 프롬프트");
        assertThat(prompt.getContent()).isEqualTo("프롬프트 내용");
    }

    @Test
    @DisplayName("제목이 비어있는 프롬프트 템플릿 생성 - 실패")
    void createPromptTemplate_WithEmptyTitle_ThrowsException() {
        // given & when & then
        assertThatThrownBy(() -> {
            PromptTemplate invalidPrompt = PromptTemplate.builder()
                    .id(UUID.randomUUID())
                    .title("")
                    .content("내용")
                    .author(author)
                    .visibility(Visibility.PUBLIC)
                    .build();
        }).isInstanceOf(PromptValidationException.class)
                .hasMessageContaining("Title cannot be empty");
    }

    @Test
    @DisplayName("제목이 null인 프롬프트 템플릿 생성 - 실패")
    void createPromptTemplate_WithNullTitle_ThrowsException() {
        // given & when & then
        assertThatThrownBy(() -> {
            PromptTemplate invalidPrompt = PromptTemplate.builder()
                    .id(UUID.randomUUID())
                    .title(null)
                    .content("내용")
                    .author(author)
                    .visibility(Visibility.PUBLIC)
                    .build();
        }).isInstanceOf(PromptValidationException.class)
                .hasMessageContaining("Title cannot be empty");
    }

    @Test
    @DisplayName("내용이 비어있는 프롬프트 템플릿 생성 - 실패")
    void createPromptTemplate_WithEmptyContent_ThrowsException() {
        // given & when & then
        assertThatThrownBy(() -> {
            PromptTemplate invalidPrompt = PromptTemplate.builder()
                    .id(UUID.randomUUID())
                    .title("제목")
                    .content("")
                    .author(author)
                    .visibility(Visibility.PUBLIC)
                    .build();
        }).isInstanceOf(PromptValidationException.class)
                .hasMessageContaining("Content cannot be empty");
    }

    @Test
    @DisplayName("내용이 null인 프롬프트 템플릿 생성 - 실패")
    void createPromptTemplate_WithNullContent_ThrowsException() {
        // given & when & then
        assertThatThrownBy(() -> {
            PromptTemplate invalidPrompt = PromptTemplate.builder()
                    .id(UUID.randomUUID())
                    .title("제목")
                    .content(null)
                    .author(author)
                    .visibility(Visibility.PUBLIC)
                    .build();
        }).isInstanceOf(PromptValidationException.class)
                .hasMessageContaining("Content cannot be empty");
    }

    @Test
    @DisplayName("작성자가 프롬프트 템플릿 편집 권한 검증 - 성공")
    void validateUserCanEdit_WhenUserIsAuthor_Succeeds() {
        // when & then
        promptDomainService.validateUserCanEdit(publicPrompt, author);
        // 예외가 발생하지 않으면 테스트 성공
    }

    @Test
    @DisplayName("작성자가 아닌 사용자의 프롬프트 템플릿 편집 권한 검증 - 실패")
    void validateUserCanEdit_WhenUserIsNotAuthor_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> promptDomainService.validateUserCanEdit(publicPrompt, otherUser))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User is not authorized to edit this prompt template");
    }

    @Test
    @DisplayName("공개 프롬프트 템플릿 조회 권한 검증 (작성자) - 성공")
    void validateUserCanView_PublicPromptAndUserIsAuthor_Succeeds() {
        // when & then
        promptDomainService.validateUserCanView(publicPrompt, author);
        // 예외가 발생하지 않으면 테스트 성공
    }

    @Test
    @DisplayName("공개 프롬프트 템플릿 조회 권한 검증 (다른 사용자) - 성공")
    void validateUserCanView_PublicPromptAndUserIsNotAuthor_Succeeds() {
        // when & then
        promptDomainService.validateUserCanView(publicPrompt, otherUser);
        // 예외가 발생하지 않으면 테스트 성공
    }

    @Test
    @DisplayName("비공개 프롬프트 템플릿 조회 권한 검증 (작성자) - 성공")
    void validateUserCanView_PrivatePromptAndUserIsAuthor_Succeeds() {
        // when & then
        promptDomainService.validateUserCanView(privatePrompt, author);
        // 예외가 발생하지 않으면 테스트 성공
    }

    @Test
    @DisplayName("비공개 프롬프트 템플릿 조회 권한 검증 (다른 사용자) - 실패")
    void validateUserCanView_PrivatePromptAndUserIsNotAuthor_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> promptDomainService.validateUserCanView(privatePrompt, otherUser))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User is not authorized to view this prompt template");
    }
}
