package com.gongdel.promptserver.application.usecase.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.gongdel.promptserver.application.exception.PromptErrorType;
import com.gongdel.promptserver.application.exception.PromptRegistrationException;
import com.gongdel.promptserver.application.port.in.command.RegisterPromptCommand;
import com.gongdel.promptserver.application.port.out.PromptTemplateTagRelationPort;
import com.gongdel.promptserver.application.port.out.SavePromptPort;
import com.gongdel.promptserver.application.port.out.SaveTagPort;
import com.gongdel.promptserver.application.port.out.TagPort;
import com.gongdel.promptserver.application.port.out.command.SavePromptVersionPort;
import com.gongdel.promptserver.domain.model.PromptTemplate;
import com.gongdel.promptserver.domain.model.PromptVersion;
import com.gongdel.promptserver.domain.model.Tag;
import com.gongdel.promptserver.domain.model.User;
import com.gongdel.promptserver.domain.model.Visibility;
import java.util.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PromptCommandServiceTest {

    @Mock
    private SavePromptPort savePromptPort;
    @Mock
    private SavePromptVersionPort savePromptVersionPort;
    @Mock
    private TagPort tagPort;
    @Mock
    private SaveTagPort saveTagPort;
    @Mock
    private PromptTemplateTagRelationPort promptTemplateTagRelationPort;

    @InjectMocks
    private PromptCommandService promptCommandService;

    private RegisterPromptCommand createValidCommand(Set<String> tags, Map<String, Object> variablesSchema) {
        return RegisterPromptCommand.builder()
                .title("Test Title")
                .description("desc")
                .content("prompt content")
                .createdBy(User.builder().id(UUID.randomUUID()).name("tester").email("tester@test.com").build())
                .tags(tags)
                .inputVariables(Collections.singletonList("input"))
                .variablesSchema(variablesSchema)
                .categoryId(1L)
                .visibility(Visibility.PRIVATE)
                .status(com.gongdel.promptserver.domain.model.PromptStatus.DRAFT)
                .build();
    }

    private PromptTemplate createPromptTemplate(Long id, Long createdById) {
        return PromptTemplate.builder()
                .id(id)
                .uuid(UUID.randomUUID())
                .title("Test Title")
                .createdById(createdById != null ? createdById : 1L)
                .visibility(Visibility.PRIVATE)
                .categoryId(1L)
                .inputVariables(Collections.singletonList("input"))
                .status(com.gongdel.promptserver.domain.model.PromptStatus.DRAFT)
                .description("desc")
                .build();
    }

    @Nested
    @DisplayName("프롬프트 등록 테스트")
    class RegisterPromptTest {
        @Test
        @DisplayName("정상적으로 프롬프트를 등록할 수 있다")
        void registerPromptSuccessfully() {
            // given
            Set<String> tags = new HashSet<>(Arrays.asList("tag1", "tag2"));
            Map<String, Object> variablesSchema = new HashMap<>();
            variablesSchema.put("var1", "value1");
            RegisterPromptCommand command = createValidCommand(tags, variablesSchema);
            PromptTemplate savedPrompt = createPromptTemplate(1L, 100L);
            PromptVersion savedVersion = PromptVersion.builder()
                    .id(10L)
                    .promptTemplateId(1L)
                    .versionNumber(1)
                    .content("prompt content")
                    .changes("init")
                    .createdById(100L)
                    .variables(new HashMap<>())
                    .actionType(com.gongdel.promptserver.domain.model.PromptVersionActionType.CREATE)
                    .createdAt(java.time.LocalDateTime.now())
                    .uuid(UUID.randomUUID())
                    .build();
            Tag tag1 = Tag.create("tag1");
            Tag tag2 = Tag.create("tag2");

            given(tagPort.findByName("tag1")).willReturn(Optional.of(tag1));
            given(tagPort.findByName("tag2")).willReturn(Optional.of(tag2));
            given(savePromptPort.savePrompt(any(PromptTemplate.class))).willReturn(savedPrompt);
            given(savePromptVersionPort.savePromptVersion(any(PromptVersion.class))).willReturn(savedVersion);
            given(promptTemplateTagRelationPort.connectTagsToPrompt(any(PromptTemplate.class), any(Set.class)))
                    .willReturn(savedPrompt);

            // when
            PromptTemplate result = promptCommandService.registerPrompt(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(savePromptPort).savePrompt(any(PromptTemplate.class));
            verify(savePromptVersionPort).savePromptVersion(any(PromptVersion.class));
            verify(promptTemplateTagRelationPort).connectTagsToPrompt(any(PromptTemplate.class), any(Set.class));
        }

        @Test
        @DisplayName("태그가 없는 경우에도 정상 등록된다")
        void registerPromptWithoutTags() {
            // given
            RegisterPromptCommand command = createValidCommand(Collections.emptySet(), Collections.emptyMap());
            PromptTemplate savedPrompt = createPromptTemplate(1L, 100L);
            PromptVersion savedVersion = PromptVersion.builder()
                    .id(10L)
                    .promptTemplateId(1L)
                    .versionNumber(1)
                    .content("prompt content")
                    .changes("init")
                    .createdById(100L)
                    .variables(new HashMap<>())
                    .actionType(com.gongdel.promptserver.domain.model.PromptVersionActionType.CREATE)
                    .createdAt(java.time.LocalDateTime.now())
                    .uuid(UUID.randomUUID())
                    .build();
            given(savePromptPort.savePrompt(any(PromptTemplate.class))).willReturn(savedPrompt);
            given(savePromptVersionPort.savePromptVersion(any(PromptVersion.class))).willReturn(savedVersion);

            // when
            PromptTemplate result = promptCommandService.registerPrompt(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(savePromptPort).savePrompt(any(PromptTemplate.class));
            verify(savePromptVersionPort).savePromptVersion(any(PromptVersion.class));
        }

        @Test
        @DisplayName("content가 null이면 예외가 발생한다")
        void throwExceptionWhenContentIsNull() {
            // given
            RegisterPromptCommand command = RegisterPromptCommand.builder()
                    .title("Test Title")
                    .description("desc")
                    .content(null)
                    .createdBy(User.builder().id(UUID.randomUUID()).name("tester").email("tester@test.com").build())
                    .tags(Collections.singleton("tag1"))
                    .inputVariables(Collections.singletonList("input"))
                    .variablesSchema(Collections.emptyMap())
                    .categoryId(1L)
                    .visibility(Visibility.PRIVATE)
                    .status(com.gongdel.promptserver.domain.model.PromptStatus.DRAFT)
                    .build();
            PromptTemplate promptTemplate = createPromptTemplate(1L, 100L);
            given(savePromptPort.savePrompt(any(PromptTemplate.class))).willReturn(promptTemplate);

            // when & then
            assertThatThrownBy(() -> promptCommandService.registerPrompt(command))
                    .isInstanceOf(PromptRegistrationException.class)
                    .hasMessageContaining(PromptErrorType.VALIDATION_ERROR.name());
        }

        @Test
        @DisplayName("PromptTemplate의 id가 null이면 예외가 발생한다")
        void throwExceptionWhenPromptTemplateIdIsNull() {
            // given
            RegisterPromptCommand command = createValidCommand(Collections.singleton("tag1"), Collections.emptyMap());
            PromptTemplate promptTemplate = createPromptTemplate(null, 100L);
            given(savePromptPort.savePrompt(any(PromptTemplate.class))).willReturn(promptTemplate);

            // when & then
            assertThatThrownBy(() -> promptCommandService.registerPrompt(command))
                    .isInstanceOf(PromptRegistrationException.class)
                    .hasMessageContaining(PromptErrorType.VALIDATION_ERROR.name());
        }

        @Test
        @DisplayName("PromptTemplate의 createdById가 null이면 예외가 발생한다")
        void throwExceptionWhenCreatedByIdIsNull() {
            // given
            RegisterPromptCommand command = createValidCommand(Collections.singleton("tag1"), Collections.emptyMap());
            PromptTemplate promptTemplate = createPromptTemplate(1L, null);
            given(savePromptPort.savePrompt(any(PromptTemplate.class))).willReturn(promptTemplate);

            // when & then
            assertThatThrownBy(() -> promptCommandService.registerPrompt(command))
                    .isInstanceOf(PromptRegistrationException.class)
                    .hasMessageContaining(PromptErrorType.VALIDATION_ERROR.name());
        }

        @Test
        @DisplayName("variablesSchema의 key 또는 value가 null/빈 문자열이면 예외가 발생한다")
        void throwExceptionWhenVariablesSchemaInvalid() {
            // given
            Map<String, Object> variablesSchema = new HashMap<>();
            variablesSchema.put("", "value1");
            RegisterPromptCommand command = createValidCommand(Collections.singleton("tag1"), variablesSchema);
            PromptTemplate promptTemplate = createPromptTemplate(1L, 100L);
            given(savePromptPort.savePrompt(any(PromptTemplate.class))).willReturn(promptTemplate);

            // when & then
            assertThatThrownBy(() -> promptCommandService.registerPrompt(command))
                    .isInstanceOf(PromptRegistrationException.class)
                    .hasMessageContaining(PromptErrorType.VALIDATION_ERROR.name());
        }

        @Test
        @DisplayName("존재하지 않는 태그는 새로 생성된다")
        void createNewTagIfNotExist() {
            // given
            Set<String> tags = new HashSet<>(Arrays.asList("newTag"));
            RegisterPromptCommand command = createValidCommand(tags, Collections.emptyMap());
            PromptTemplate savedPrompt = createPromptTemplate(1L, 100L);
            PromptVersion savedVersion = PromptVersion.builder()
                    .id(10L)
                    .promptTemplateId(1L)
                    .versionNumber(1)
                    .content("prompt content")
                    .changes("init")
                    .createdById(100L)
                    .variables(new HashMap<>())
                    .actionType(com.gongdel.promptserver.domain.model.PromptVersionActionType.CREATE)
                    .createdAt(java.time.LocalDateTime.now())
                    .uuid(UUID.randomUUID())
                    .build();
            Tag newTag = Tag.create("newTag");

            given(tagPort.findByName("newTag")).willReturn(Optional.empty());
            given(saveTagPort.saveTag(any(Tag.class))).willReturn(newTag);
            given(savePromptPort.savePrompt(any(PromptTemplate.class))).willReturn(savedPrompt);
            given(savePromptVersionPort.savePromptVersion(any(PromptVersion.class))).willReturn(savedVersion);
            given(promptTemplateTagRelationPort.connectTagsToPrompt(any(PromptTemplate.class), any(Set.class)))
                    .willReturn(savedPrompt);

            // when
            PromptTemplate result = promptCommandService.registerPrompt(command);

            // then
            assertThat(result).isNotNull();
            verify(saveTagPort).saveTag(any(Tag.class));
        }

        @Test
        @DisplayName("예상치 못한 예외가 발생하면 PromptRegistrationException으로 감싸진다")
        void wrapUnexpectedException() {
            // given
            RegisterPromptCommand command = createValidCommand(Collections.singleton("tag1"), Collections.emptyMap());
            given(savePromptPort.savePrompt(any(PromptTemplate.class))).willThrow(new RuntimeException("DB error"));

            // when & then
            assertThatThrownBy(() -> promptCommandService.registerPrompt(command))
                    .isInstanceOf(PromptRegistrationException.class)
                    .hasMessageContaining(PromptErrorType.UNKNOWN_ERROR.name());
        }
    }
}
