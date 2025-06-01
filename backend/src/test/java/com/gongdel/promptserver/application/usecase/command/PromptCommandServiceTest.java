package com.gongdel.promptserver.application.usecase.command;

import com.gongdel.promptserver.application.dto.RegisterPromptResponse;
import com.gongdel.promptserver.application.exception.PromptErrorType;
import com.gongdel.promptserver.application.exception.PromptRegistrationException;
import com.gongdel.promptserver.application.port.in.command.RegisterPromptCommand;
import com.gongdel.promptserver.application.port.out.PromptTemplateTagRelationPort;
import com.gongdel.promptserver.application.port.out.SavePromptPort;
import com.gongdel.promptserver.application.port.out.SaveTagPort;
import com.gongdel.promptserver.application.port.out.command.SavePromptVersionPort;
import com.gongdel.promptserver.application.port.out.query.LoadPromptPort;
import com.gongdel.promptserver.application.port.out.query.LoadTagPort;
import com.gongdel.promptserver.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PromptCommandServiceTest {

        @Mock
        SavePromptPort savePromptPort;
        @Mock
        SavePromptVersionPort savePromptVersionPort;
        @Mock
        LoadTagPort loadTagPort;
        @Mock
        SaveTagPort saveTagPort;
        @Mock
        PromptTemplateTagRelationPort promptTemplateTagRelationPort;
        @Mock
        LoadPromptPort loadPromptPort;

        @InjectMocks
        PromptCommandService promptCommandService;

        RegisterPromptCommand baseCommand;
        PromptTemplate savedPrompt;
        PromptVersion savedVersion;
        Tag tag1, tag2;
        User testUser;
        List<InputVariable> inputVariables;

        @BeforeEach
        void setUp() {
                testUser = User.builder().id(UUID.randomUUID()).name("TestUser").email("test@test.com").build();
                inputVariables = List.of(
                                InputVariable.builder().name("var1").type("String").description("desc1").required(true)
                                                .defaultValue("").build(),
                                InputVariable.builder().name("var2").type("String").description("desc2").required(false)
                                                .defaultValue("default").build());
                baseCommand = RegisterPromptCommand.builder()
                                .title("Test Prompt")
                                .description("Description")
                                .content("Content")
                                .categoryId(1L)
                                .status(PromptStatus.PUBLISHED)
                                .tags(Set.of("Tag1", "Tag2"))
                                .inputVariables(inputVariables)
                                .createdBy(testUser)
                                .build();
                savedPrompt = PromptTemplate.builder()
                                .id(10L)
                                .uuid(UUID.randomUUID())
                                .title("Test Prompt")
                                .description("Description")
                                .createdById(1L)
                                .visibility(Visibility.PRIVATE)
                                .categoryId(1L)
                                .status(PromptStatus.PUBLISHED)
                                .build();
                savedVersion = PromptVersion.builder()
                                .id(100L)
                                .promptTemplateId(10L)
                                .uuid(UUID.randomUUID())
                                .versionNumber(1)
                                .content("Content")
                                .changes("Initial version")
                                .createdById(1L)
                                .createdAt(LocalDateTime.now())
                                .inputVariables(inputVariables)
                                .actionType(PromptVersionActionType.CREATE)
                                .build();
                tag1 = Tag.create("Tag1");
                tag2 = Tag.create("Tag2");
        }

        @Test
        @DisplayName("정상적으로 프롬프트 등록 성공")
        void registerPrompt_success() {
                // given
                given(loadTagPort.loadTagByName("Tag1")).willReturn(Optional.of(tag1));
                given(loadTagPort.loadTagByName("Tag2")).willReturn(Optional.of(tag2));
                given(savePromptPort.savePrompt(any(PromptTemplate.class))).willReturn(savedPrompt);
                given(savePromptVersionPort.savePromptVersion(any(PromptVersion.class))).willReturn(savedVersion);
                given(loadPromptPort.loadPromptByUuid(any(UUID.class))).willReturn(Optional.of(savedPrompt));

                // when
                RegisterPromptResponse response = promptCommandService.registerPrompt(baseCommand);

                // then
                assertThat(response).isNotNull();
                assertThat(response.getTitle()).isEqualTo("Test Prompt");
                then(promptTemplateTagRelationPort).should().connectTagsToPrompt(any(), any());
        }

        @Test
        @DisplayName("태그가 없는 경우 정상 등록")
        void registerPrompt_noTags() {
                // given
                RegisterPromptCommand command = RegisterPromptCommand.builder()
                                .title("Prompt")
                                .description("Description")
                                .content("Content")
                                .categoryId(1L)
                                .status(PromptStatus.PUBLISHED)
                                .tags(Collections.emptySet())
                                .createdBy(testUser)
                                .build();
                given(savePromptPort.savePrompt(any(PromptTemplate.class))).willReturn(savedPrompt);
                given(savePromptVersionPort.savePromptVersion(any(PromptVersion.class))).willReturn(savedVersion);
                given(loadPromptPort.loadPromptByUuid(any(UUID.class))).willReturn(Optional.of(savedPrompt));

                // when
                RegisterPromptResponse response = promptCommandService.registerPrompt(command);

                // then
                assertThat(response).isNotNull();
                then(promptTemplateTagRelationPort).should(never()).connectTagsToPrompt(any(), any());
        }

        @Test
        @DisplayName("중복 및 공백 태그가 포함된 경우 정상 처리")
        void registerPrompt_duplicateAndBlankTags() {
                // given
                RegisterPromptCommand command = RegisterPromptCommand.builder()
                                .title("Prompt")
                                .description("Description")
                                .content("Content")
                                .categoryId(1L)
                                .status(PromptStatus.PUBLISHED)
                                .tags(new HashSet<>(Arrays.asList("Tag1", "  ", "Tag1", null, "Tag2")))
                                .createdBy(testUser)
                                .build();
                given(loadTagPort.loadTagByName("Tag1")).willReturn(Optional.of(tag1));
                given(loadTagPort.loadTagByName("Tag2")).willReturn(Optional.of(tag2));
                given(savePromptPort.savePrompt(any(PromptTemplate.class))).willReturn(savedPrompt);
                given(savePromptVersionPort.savePromptVersion(any(PromptVersion.class))).willReturn(savedVersion);
                given(loadPromptPort.loadPromptByUuid(any(UUID.class))).willReturn(Optional.of(savedPrompt));

                // when
                RegisterPromptResponse response = promptCommandService.registerPrompt(command);

                // then
                assertThat(response).isNotNull();
                then(promptTemplateTagRelationPort).should().connectTagsToPrompt(any(), any());
        }

        @Test
        @DisplayName("프롬프트 내용이 없으면 유효성 예외 발생")
        void registerPrompt_contentValidationException() {
                // given
                RegisterPromptCommand command = RegisterPromptCommand.builder()
                                .title("Prompt")
                                .description("Description")
                                .content("")
                                .categoryId(1L)
                                .status(PromptStatus.PUBLISHED)
                                .createdBy(testUser)
                                .build();
                given(savePromptPort.savePrompt(any(PromptTemplate.class))).willReturn(savedPrompt);

                // when
                Throwable thrown = catchThrowable(() -> promptCommandService.registerPrompt(command));

                // then
                assertThat(thrown)
                                .isInstanceOf(PromptRegistrationException.class);
                PromptRegistrationException ex = (PromptRegistrationException) thrown;
                assertThat(ex.getErrorCode()).isEqualTo(PromptErrorType.VALIDATION_ERROR);
        }

        @Test
        @DisplayName("알 수 없는 예외 발생 시 PromptRegistrationException으로 변환")
        void registerPrompt_unknownException() {
                // given
                given(loadTagPort.loadTagByName(anyString())).willThrow(new RuntimeException("DB error"));

                // when & then
                assertThatThrownBy(() -> promptCommandService.registerPrompt(baseCommand))
                                .isInstanceOf(PromptRegistrationException.class);
        }
}
