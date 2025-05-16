package com.gongdel.promptserver.application.usecase;

import com.gongdel.promptserver.application.port.in.command.RegisterPromptCommand;
import com.gongdel.promptserver.application.port.out.LoadPromptPort;
import com.gongdel.promptserver.application.port.out.SavePromptPort;
import com.gongdel.promptserver.domain.model.PromptTemplate;
import com.gongdel.promptserver.domain.model.PromptValidationException;
import com.gongdel.promptserver.domain.model.User;
import com.gongdel.promptserver.domain.model.UserRole;
import com.gongdel.promptserver.domain.model.Visibility;
import com.gongdel.promptserver.domain.service.PromptDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegisterPromptServiceTest {

    @Mock
    private LoadPromptPort loadPromptPort;

    @Mock
    private SavePromptPort savePromptPort;

    @Mock
    private PromptDomainService promptDomainService;

    @InjectMocks
    private RegisterPromptService registerPromptService;

    @Captor
    private ArgumentCaptor<PromptTemplate> promptTemplateCaptor;

    private User testUser;
    private RegisterPromptCommand validCommand;
    private PromptTemplate savedPromptTemplate;

    @BeforeEach
    void setUp() throws PromptValidationException {
        // 테스트용 사용자 생성
        testUser = User.builder()
                .id(UUID.randomUUID())
                .name("테스트 사용자")
                .email("test@example.com")
                .role(UserRole.ROLE_USER)
                .build();

        // 유효한 명령 객체 생성
        validCommand = RegisterPromptCommand.builder()
                .title("테스트 프롬프트 제목")
                .description("테스트 프롬프트 설명")
                .content("테스트 프롬프트 내용")
                .author(testUser)
                .tagIds(new HashSet<>())
                .isPublic(true)
                .build();

        // 저장된 프롬프트 템플릿 생성
        savedPromptTemplate = PromptTemplate.builder()
                .id(UUID.randomUUID())
                .title(validCommand.getTitle())
                .description(validCommand.getDescription())
                .content(validCommand.getContent())
                .author(testUser)
                .visibility(Visibility.PUBLIC)
                .build();
    }

    @Test
    @DisplayName("유효한 프롬프트 등록 명령을 처리하여 프롬프트 템플릿을 성공적으로 등록한다")
    void registerPrompt_WithValidCommand_ShouldRegisterPromptTemplate() throws PromptValidationException {
        // given
        given(savePromptPort.savePrompt(any(PromptTemplate.class))).willReturn(savedPromptTemplate);

        // when
        PromptTemplate result = registerPromptService.registerPrompt(validCommand);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(validCommand.getTitle());
        assertThat(result.getDescription()).isEqualTo(validCommand.getDescription());
        assertThat(result.getContent()).isEqualTo(validCommand.getContent());
        assertThat(result.getAuthor()).isEqualTo(testUser);
        assertThat(result.isPublic()).isTrue();

        // 저장 포트가 호출되었는지 확인
        verify(savePromptPort).savePrompt(promptTemplateCaptor.capture());

        // 캡처된 프롬프트 템플릿 검증
        PromptTemplate capturedTemplate = promptTemplateCaptor.getValue();
        assertThat(capturedTemplate.getTitle()).isEqualTo(validCommand.getTitle());
        assertThat(capturedTemplate.getDescription()).isEqualTo(validCommand.getDescription());
        assertThat(capturedTemplate.getContent()).isEqualTo(validCommand.getContent());
        assertThat(capturedTemplate.getAuthor()).isEqualTo(testUser);
    }

    @Test
    @DisplayName("도메인 모델 생성 시 유효성 검증에 실패하면 예외를 발생시킨다")
    void registerPrompt_WithInvalidData_ShouldThrowException() {
        // given
        RegisterPromptCommand invalidCommand = RegisterPromptCommand.builder()
                .title("") // 빈 제목으로 유효성 검증 실패 유도
                .description("테스트 프롬프트 설명")
                .content("테스트 프롬프트 내용")
                .author(testUser)
                .tagIds(new HashSet<>())
                .isPublic(true)
                .build();

        // when & then
        assertThatThrownBy(() -> registerPromptService.registerPrompt(invalidCommand))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(PromptValidationException.class);

        // 저장 포트는 호출되지 않아야 함
        then(savePromptPort).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("프롬프트 등록 시 생성되는 프롬프트 템플릿의 ID는 새로운 UUID여야 한다")
    void registerPrompt_ShouldGenerateNewUUID() throws PromptValidationException {
        // given
        given(savePromptPort.savePrompt(any(PromptTemplate.class))).willReturn(savedPromptTemplate);

        // when
        registerPromptService.registerPrompt(validCommand);

        // then
        verify(savePromptPort).savePrompt(promptTemplateCaptor.capture());
        PromptTemplate capturedTemplate = promptTemplateCaptor.getValue();

        assertThat(capturedTemplate.getId()).isNotNull();
        assertThat(capturedTemplate.getId()).isInstanceOf(UUID.class);
    }

    @Test
    @DisplayName("공개 상태로 프롬프트를 등록한다")
    void registerPrompt_WithPublicVisibility_ShouldSetVisibilityToPublic() throws PromptValidationException {
        // given
        given(savePromptPort.savePrompt(any(PromptTemplate.class))).willReturn(savedPromptTemplate);

        // when
        registerPromptService.registerPrompt(validCommand);

        // then
        verify(savePromptPort).savePrompt(promptTemplateCaptor.capture());
        PromptTemplate capturedTemplate = promptTemplateCaptor.getValue();

        assertThat(capturedTemplate.isPublic()).isTrue();
    }

    @Test
    @DisplayName("비공개 상태로 프롬프트를 등록한다")
    void registerPrompt_WithPrivateVisibility_ShouldSetVisibilityToPrivate() throws PromptValidationException {
        // given
        RegisterPromptCommand privateCommand = RegisterPromptCommand.builder()
                .title("비공개 프롬프트")
                .description("비공개 프롬프트 설명")
                .content("비공개 프롬프트 내용")
                .author(testUser)
                .tagIds(new HashSet<>())
                .isPublic(false)
                .build();

        PromptTemplate privatePromptTemplate = PromptTemplate.builder()
                .id(UUID.randomUUID())
                .title(privateCommand.getTitle())
                .description(privateCommand.getDescription())
                .content(privateCommand.getContent())
                .author(testUser)
                .visibility(Visibility.PRIVATE)
                .build();

        given(savePromptPort.savePrompt(any(PromptTemplate.class))).willReturn(privatePromptTemplate);

        // when
        registerPromptService.registerPrompt(privateCommand);

        // then
        verify(savePromptPort).savePrompt(promptTemplateCaptor.capture());
        PromptTemplate capturedTemplate = promptTemplateCaptor.getValue();

        assertThat(capturedTemplate.isPublic()).isFalse();
    }
}
