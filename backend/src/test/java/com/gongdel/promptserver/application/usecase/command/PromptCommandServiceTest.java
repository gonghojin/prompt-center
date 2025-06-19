package com.gongdel.promptserver.application.usecase.command;

import com.gongdel.promptserver.application.dto.DeletePromptResponse;
import com.gongdel.promptserver.application.dto.RegisterPromptResponse;
import com.gongdel.promptserver.application.exception.PromptErrorType;
import com.gongdel.promptserver.application.exception.PromptRegistrationException;
import com.gongdel.promptserver.application.port.in.command.DeletePromptCommand;
import com.gongdel.promptserver.application.port.in.command.RegisterPromptCommand;
import com.gongdel.promptserver.application.port.in.command.UpdatePromptCommand;
import com.gongdel.promptserver.application.port.in.result.UpdatePromptResult;
import com.gongdel.promptserver.application.port.out.PromptTemplateTagRelationPort;
import com.gongdel.promptserver.application.port.out.SavePromptPort;
import com.gongdel.promptserver.application.port.out.SaveTagPort;
import com.gongdel.promptserver.application.port.out.command.SavePromptVersionPort;
import com.gongdel.promptserver.application.port.out.query.LoadPromptPort;
import com.gongdel.promptserver.application.port.out.query.LoadPromptVersionPort;
import com.gongdel.promptserver.application.port.out.query.LoadTagPort;
import com.gongdel.promptserver.domain.exception.PromptOperationException;
import com.gongdel.promptserver.domain.model.*;
import com.gongdel.promptserver.domain.team.Team;
import com.gongdel.promptserver.domain.team.TeamId;
import com.gongdel.promptserver.domain.user.Email;
import com.gongdel.promptserver.domain.user.User;
import com.gongdel.promptserver.domain.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
    @Mock
    LoadPromptVersionPort loadPromptVersionPort;

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
        testUser = User.builder().id(1L).uuid(new UserId(UUID.randomUUID())).name("TestUser")
            .email(new Email("test@test.com")).build();
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

    @Nested
    @DisplayName("deletePrompt(DeletePromptCommand) 메서드는")
    class DeletePromptTest {
        UUID promptUuid;
        User creator;
        User otherUser;
        PromptTemplate prompt;

        @BeforeEach
        void setUp() {
            promptUuid = UUID.randomUUID();
            creator = User.builder()
                .id(1L)
                .uuid(new UserId(UUID.randomUUID()))
                .email(new Email("creator@test.com"))
                .name("Creator")
                .build();
            otherUser = User.builder()
                .id(2L)
                .uuid(new UserId(UUID.randomUUID()))
                .email(new Email("other@test.com"))
                .name("Other")
                .build();
            prompt = PromptTemplate.builder()
                .id(10L)
                .uuid(promptUuid)
                .title("Prompt Title")
                .createdById(creator.getId())
                .visibility(Visibility.PRIVATE)
                .categoryId(1L)
                .status(PromptStatus.PUBLISHED)
                .description("desc")
                .build();
        }

        @Test
        @DisplayName("정상적으로 프롬프트를 논리 삭제한다")
        void givenValidCommand_whenDeletePrompt_thenSuccess() {
            // Given
            given(loadPromptPort.loadPromptByUuid(promptUuid)).willReturn(Optional.of(prompt));
            given(savePromptPort.savePrompt(any(PromptTemplate.class)))
                .willAnswer(invocation -> invocation.getArgument(0));
            DeletePromptCommand command = DeletePromptCommand.builder()
                .uuid(promptUuid)
                .currentUser(creator)
                .build();

            // When
            DeletePromptResponse response = promptCommandService.deletePrompt(command);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getUuid()).isEqualTo(promptUuid);
            assertThat(response.getTitle()).isEqualTo(prompt.getTitle());
            assertThat(response.getPreviousStatus()).isEqualTo(PromptStatus.PUBLISHED);
            assertThat(response.getDeletedBy()).isEqualTo(creator.getUuid());
            assertThat(response.getDeletedAt()).isNotNull();
            then(savePromptPort).should().savePrompt(any(PromptTemplate.class));
        }

        @Test
        @DisplayName("삭제 권한이 없는 사용자가 요청하면 예외가 발생한다")
        void givenNoPermissionUser_whenDeletePrompt_thenThrowsException() {
            // Given
            given(loadPromptPort.loadPromptByUuid(promptUuid)).willReturn(Optional.of(prompt));
            DeletePromptCommand command = DeletePromptCommand.builder()
                .uuid(promptUuid)
                .currentUser(otherUser)
                .build();

            // When & Then
            assertThatThrownBy(() -> promptCommandService.deletePrompt(command))
                .isInstanceOf(PromptRegistrationException.class)
                .hasMessageContaining("삭제 권한이 없습니다.")
                .extracting("errorCode")
                .isEqualTo(PromptErrorType.INSUFFICIENT_PERMISSION);
        }

        @Test
        @DisplayName("존재하지 않는 프롬프트를 삭제하려 하면 예외가 발생한다")
        void givenNonExistentPrompt_whenDeletePrompt_thenThrowsException() {
            // Given
            given(loadPromptPort.loadPromptByUuid(promptUuid)).willReturn(Optional.empty());
            DeletePromptCommand command = DeletePromptCommand.builder()
                .uuid(promptUuid)
                .currentUser(creator)
                .build();

            // When & Then
            assertThatThrownBy(() -> promptCommandService.deletePrompt(command))
                .isInstanceOf(PromptRegistrationException.class)
                .extracting("errorCode")
                .isEqualTo(PromptErrorType.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("updatePrompt(UpdatePromptCommand) 메서드는")
    class UpdatePromptTest {
        private UUID promptUuid;
        private User editor;
        private User otherUser;
        private PromptTemplate existingPrompt;
        private UpdatePromptCommand command;
        private Set<Tag> newTags;
        private List<InputVariable> newInputVariables;

        @BeforeEach
        void setUp() {
            promptUuid = UUID.randomUUID();
            editor = User.builder()
                .id(1L)
                .uuid(new UserId(UUID.randomUUID()))
                .email(new Email("editor@test.com"))
                .name("Editor")
                .build();
            otherUser = User.builder()
                .id(2L)
                .uuid(new UserId(UUID.randomUUID()))
                .email(new Email("other@test.com"))
                .name("Other")
                .build();
            existingPrompt = PromptTemplate.builder()
                .id(10L)
                .uuid(promptUuid)
                .title("Original Title")
                .description("Original Description")
                .createdById(editor.getId())
                .visibility(Visibility.PRIVATE)
                .categoryId(1L)
                .status(PromptStatus.PUBLISHED)
                .currentVersionId(1L)
                .build();

            newTags = Set.of(
                Tag.create("NewTag1"));

            newInputVariables = List.of(
                InputVariable.builder()
                    .name("newVar1")
                    .type("String")
                    .description("New description 1")
                    .required(true)
                    .build());

            command = UpdatePromptCommand.builder()
                .promptTemplateId(promptUuid)
                .title("Updated Title")
                .description("Updated Description")
                .content("Updated Content")
                .categoryId(2L)
                .visibility(Visibility.TEAM)
                .status(PromptStatus.DRAFT)
                .tags(Set.of("NewTag1", "NewTag2"))
                .inputVariables(newInputVariables)
                .editor(editor)
                .build();
        }

        @Test
        @DisplayName("정상적으로 프롬프트를 수정한다")
        void givenValidCommand_whenUpdatePrompt_thenSuccess() {
            // Given
            given(loadPromptPort.loadPromptByUuid(promptUuid))
                .willReturn(Optional.of(existingPrompt));
            given(loadTagPort.loadTagByName(anyString()))
                .willAnswer(inv -> Optional.of(Tag.create(inv.getArgument(0))));
            given(savePromptVersionPort.savePromptVersion(any()))
                .willAnswer(inv -> {
                    PromptVersion version = inv.getArgument(0);
                    return PromptVersion.builder()
                        .id(100L)
                        .promptTemplateId(version.getPromptTemplateId())
                        .versionNumber(version.getVersionNumber())
                        .content(version.getContent())
                        .changes(version.getChanges())
                        .createdById(version.getCreatedById())
                        .inputVariables(version.getInputVariables())
                        .actionType(version.getActionType())
                        .createdAt(version.getCreatedAt())
                        .uuid(version.getUuid())
                        .build();
                });
            given(savePromptPort.savePrompt(any()))
                .willAnswer(inv -> inv.getArgument(0));
            given(promptTemplateTagRelationPort.findTagsByPromptTemplateId(anyLong()))
                .willReturn(newTags);

            // When
            UpdatePromptResult result = promptCommandService.updatePrompt(command);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("Updated Title");
            assertThat(result.getDescription()).isEqualTo("Updated Description");
            assertThat(result.getVisibility()).isEqualTo(Visibility.TEAM);
            assertThat(result.getStatus()).isEqualTo(PromptStatus.DRAFT);
            assertThat(result.getTags()).hasSize(1);
            verify(savePromptVersionPort).savePromptVersion(any());
            verify(savePromptPort, times(1)).savePrompt(any());
        }

        @Test
        @DisplayName("수정 권한이 없는 사용자가 요청하면 예외가 발생한다")
        void givenUnauthorizedUser_whenUpdatePrompt_thenThrowsException() {
            // Given
            command = UpdatePromptCommand.builder()
                .promptTemplateId(command.getPromptTemplateId())
                .title(command.getTitle())
                .description(command.getDescription())
                .content(command.getContent())
                .categoryId(command.getCategoryId())
                .visibility(command.getVisibility())
                .status(command.getStatus())
                .tags(command.getTags())
                .inputVariables(command.getInputVariables())
                .editor(otherUser)
                .build();
            given(loadPromptPort.loadPromptByUuid(promptUuid))
                .willReturn(Optional.of(existingPrompt));

            // When & Then
            assertThatThrownBy(() -> promptCommandService.updatePrompt(command))
                .isInstanceOf(PromptOperationException.class)
                .hasMessageContaining("수정 권한이 없습니다");
        }

        @Test
        @DisplayName("존재하지 않는 프롬프트를 수정하려 하면 예외가 발생한다")
        void givenNonExistentPrompt_whenUpdatePrompt_thenThrowsException() {
            // Given
            given(loadPromptPort.loadPromptByUuid(promptUuid))
                .willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> promptCommandService.updatePrompt(command))
                .isInstanceOf(PromptRegistrationException.class)
                .hasMessageContaining("프롬프트 템플릿을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("새로운 버전이 정상적으로 생성된다")
        void givenValidCommand_whenUpdatePrompt_thenCreatesNewVersion() {
            // Given
            given(loadPromptPort.loadPromptByUuid(promptUuid))
                .willReturn(Optional.of(existingPrompt));
            given(loadPromptVersionPort.loadPromptVersionById(anyLong()))
                .willReturn(Optional.of(PromptVersion.builder()
                    .id(1L)
                    .promptTemplateId(existingPrompt.getId())
                    .versionNumber(1)
                    .content("Old Content")
                    .changes("Initial")
                    .createdById(editor.getId())
                    .inputVariables(newInputVariables)
                    .actionType(PromptVersionActionType.CREATE)
                    .createdAt(LocalDateTime.now())
                    .uuid(UUID.randomUUID())
                    .build()));
            given(savePromptVersionPort.savePromptVersion(any()))
                .willAnswer(inv -> {
                    PromptVersion version = inv.getArgument(0);
                    return PromptVersion.builder()
                        .id(2L)
                        .promptTemplateId(version.getPromptTemplateId())
                        .versionNumber(version.getVersionNumber())
                        .content(version.getContent())
                        .changes(version.getChanges())
                        .createdById(version.getCreatedById())
                        .inputVariables(version.getInputVariables())
                        .actionType(version.getActionType())
                        .createdAt(version.getCreatedAt())
                        .uuid(version.getUuid())
                        .build();
                });
            given(savePromptPort.savePrompt(any()))
                .willAnswer(inv -> inv.getArgument(0));
            given(promptTemplateTagRelationPort.findTagsByPromptTemplateId(anyLong()))
                .willReturn(newTags);

            // When
            UpdatePromptResult result = promptCommandService.updatePrompt(command);

            // Then
            assertThat(result).isNotNull();
            verify(savePromptVersionPort)
                .savePromptVersion(argThat(version -> version.getVersionNumber() == 2 &&
                    version.getContent().equals("Updated Content") &&
                    version.getActionType() == PromptVersionActionType.EDIT));
        }
    }

    @Nested
    @DisplayName("registerPrompt() 메서드 추가 테스트")
    class ExtendedRegisterPromptTest {

        @Test
        @DisplayName("팀 소속 사용자의 경우 기본 가시성이 TEAM으로 설정된다")
        void givenTeamUser_whenRegisterPrompt_thenTeamVisibility() {
            // Given
            User teamUser = User.builder()
                .id(1L)
                .uuid(new UserId(UUID.randomUUID()))
                .name("테스트유저")
                .email(new Email("test@example.com"))
                .team(new Team(1L, TeamId.randomId(), "TestTeam", "Test Description", null))
                .build();
            RegisterPromptCommand command = RegisterPromptCommand.builder()
                .title(baseCommand.getTitle())
                .description(baseCommand.getDescription())
                .content(baseCommand.getContent())
                .categoryId(baseCommand.getCategoryId())
                .status(baseCommand.getStatus())
                .tags(baseCommand.getTags())
                .inputVariables(baseCommand.getInputVariables())
                .createdBy(teamUser)
                .visibility(null)
                .build();

            PromptTemplate teamTemplate = PromptTemplate.of(
                10L, UUID.randomUUID(), command.getTitle(),
                1L, command.getCategoryId(), teamUser.getId(), Visibility.TEAM,
                command.getStatus(), command.getDescription(), LocalDateTime.now(), LocalDateTime.now(),
                Set.of(Tag.create("Tag1")));
            given(savePromptPort.savePrompt(any())).willReturn(teamTemplate);
            given(savePromptVersionPort.savePromptVersion(any())).willReturn(savedVersion);
            given(loadPromptPort.loadPromptByUuid(any())).willReturn(Optional.of(teamTemplate));
            given(loadTagPort.loadTagByName(anyString()))
                .willAnswer(inv -> Optional.of(Tag.create(inv.getArgument(0))));

            // When
            RegisterPromptResponse response = promptCommandService.registerPrompt(command);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getVisibility()).isEqualTo(Visibility.TEAM.name());
        }
    }
}
