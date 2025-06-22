package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.entity.*;
import com.gongdel.promptserver.adapter.out.persistence.mapper.PromptTemplateMapper;
import com.gongdel.promptserver.adapter.out.persistence.mapper.PromptVersionMapper;
import com.gongdel.promptserver.adapter.out.persistence.mapper.UserMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.*;
import com.gongdel.promptserver.application.port.in.query.LoadPromptDetailQuery;
import com.gongdel.promptserver.domain.exception.PromptErrorType;
import com.gongdel.promptserver.domain.exception.PromptOperationException;
import com.gongdel.promptserver.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromptLoadAdapterTest {

    @Mock
    PromptTemplateJpaRepository promptTemplateJpaRepository;
    @Mock
    PromptVersionRepository promptVersionRepository;
    @Mock
    PromptTemplateMapper promptTemplateMapper;
    @Mock
    PromptVersionMapper promptVersionMapper;
    @Mock
    FavoriteRepository favoriteRepository;
    @Mock
    UserMapper userMapper;
    @Mock
    PromptLikeCountRepository promptLikeCountRepository;
    @Mock
    PromptLikeJpaRepository promptLikeJpaRepository;

    @InjectMocks
    PromptLoadAdapter promptLoadAdapter;

    @Nested
    @DisplayName("loadPromptById 메서드")
    class LoadPromptById {
        @Test
        @DisplayName("Given 정상 ID, When 조회, Then Optional<PromptTemplate> 반환")
        void givenValidId_whenLoad_thenReturnPromptTemplate() {
            // Given
            Long id = 1L;
            PromptTemplateEntity entity = new PromptTemplateEntity();
            PromptTemplate template = PromptTemplate.builder()
                .id(id)
                .title("title")
                .createdById(1L)
                .description("desc")
                .build();
            when(promptTemplateJpaRepository.findById(id)).thenReturn(Optional.of(entity));
            when(promptTemplateMapper.toDomain(entity)).thenReturn(template);

            // When
            Optional<PromptTemplate> result = promptLoadAdapter.loadPromptById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
        }

        @Test
        @DisplayName("Given repository 예외, When 조회, Then PromptOperationException 발생")
        void givenRepositoryException_whenLoad_thenPromptOperationException() {
            // Given
            Long id = 2L;
            when(promptTemplateJpaRepository.findById(id)).thenThrow(new RuntimeException("DB error"));

            // When & Then
            assertThatThrownBy(() -> promptLoadAdapter.loadPromptById(id))
                .isInstanceOf(PromptOperationException.class)
                .hasMessageContaining("프롬프트 단건(ID) 조회 실패")
                .hasFieldOrPropertyWithValue("errorCode", PromptErrorType.OPERATION_FAILED);
        }
    }

    @Nested
    @DisplayName("loadPromptByUuid 메서드")
    class LoadPromptByUuid {
        @Test
        @DisplayName("Given 정상 UUID, When 조회, Then Optional<PromptTemplate> 반환")
        void givenValidUuid_whenLoad_thenReturnPromptTemplate() {
            // Given
            UUID uuid = UUID.randomUUID();
            PromptTemplateEntity entity = new PromptTemplateEntity();
            PromptTemplate template = PromptTemplate.builder()
                .uuid(uuid)
                .title("title")
                .createdById(1L)
                .description("desc")
                .build();
            when(promptTemplateJpaRepository.findByUuid(uuid)).thenReturn(Optional.of(entity));
            when(promptTemplateMapper.toDomain(entity)).thenReturn(template);

            // When
            Optional<PromptTemplate> result = promptLoadAdapter.loadPromptByUuid(uuid);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getUuid()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("Given repository 예외, When 조회, Then PromptOperationException 발생")
        void givenRepositoryException_whenLoad_thenPromptOperationException() {
            // Given
            UUID uuid = UUID.randomUUID();
            when(promptTemplateJpaRepository.findByUuid(uuid)).thenThrow(new RuntimeException("DB error"));

            // When & Then
            assertThatThrownBy(() -> promptLoadAdapter.loadPromptByUuid(uuid))
                .isInstanceOf(PromptOperationException.class)
                .hasMessageContaining("프롬프트 단건(UUID) 조회 실패")
                .hasFieldOrPropertyWithValue("errorCode", PromptErrorType.OPERATION_FAILED);
        }
    }

    @Nested
    @DisplayName("loadPromptDetailByUuid 메서드")
    class LoadPromptDetailByUuid {
        @Test
        @DisplayName("Given 정상 UUID, When 상세 조회, Then Optional<PromptDetail> 반환")
        void givenValidUuid_whenLoadDetail_thenReturnPromptDetail() {
            // Given
            UUID uuid = UUID.randomUUID();
            Long userId = 1L;
            LoadPromptDetailQuery query = LoadPromptDetailQuery.builder()
                .promptUuid(uuid)
                .userId(userId)
                .build();
            PromptTemplateEntity entity = new PromptTemplateEntity();
            entity.setId(1L);
            entity.setUuid(uuid);
            entity.setTitle("title");
            entity.setDescription("desc");
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setVisibility(Visibility.PUBLIC);
            entity.setStatus(PromptStatus.DRAFT);
            entity.setCurrentVersionId(10L);
            UserEntity user = new UserEntity();
            user.setId(userId);
            user.setName("user");
            entity.setCreatedBy(user);
            CategoryEntity category = new CategoryEntity();
            category.setId(2L);
            category.setName("cat");
            entity.setCategory(category);
            TagEntity tag = TagEntity.create(3L, "tag1");
            PromptTemplateTagEntity tagRel = new PromptTemplateTagEntity(entity, tag);
            entity.setTagRelations(List.of(tagRel));

            PromptVersionEntity versionEntity = new PromptVersionEntity();

            PromptTemplate template = PromptTemplate.builder()
                .uuid(uuid)
                .currentVersionId(10L)
                .title("title")
                .description("desc")
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .visibility(Visibility.PUBLIC)
                .status(PromptStatus.DRAFT)
                .categoryId(2L)
                .createdById(userId)
                .build();
            PromptVersion version = PromptVersion.builder()
                .id(10L)
                .promptTemplateId(1L)
                .versionNumber(1)
                .content("content")
                .changes("최초 생성")
                .createdById(userId)
                .inputVariables(java.util.List.of(
                    InputVariable.builder()
                        .name("userName")
                        .type("string")
                        .description("사용자 이름")
                        .required(true)
                        .defaultValue("")
                        .build()))
                .actionType(PromptVersionActionType.CREATE)
                .uuid(java.util.UUID.randomUUID())
                .build();
            when(promptTemplateJpaRepository.findByUuidWithRelations(uuid)).thenReturn(Optional.of(entity));
            when(promptTemplateMapper.toDomain(entity)).thenReturn(template);
            when(promptVersionRepository.findById(10L)).thenReturn(Optional.of(versionEntity));
            when(promptVersionMapper.toDomain(versionEntity)).thenReturn(version);
            when(favoriteRepository.existsByUserAndPromptTemplate(any(UserEntity.class),
                any(PromptTemplateEntity.class)))
                .thenReturn(true);

            // When
            Optional<PromptDetail> result = promptLoadAdapter.loadPromptDetailBy(query);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(uuid);
            assertThat(result.get().getTitle()).isEqualTo("title");
            assertThat(result.get().getTags()).contains("tag1");
        }

        @Test
        @DisplayName("Given repository 예외, When 상세 조회, Then PromptOperationException 발생")
        void givenRepositoryException_whenLoadDetail_thenPromptOperationException() {
            // Given
            UUID uuid = UUID.randomUUID();
            Long userId = 1L;
            LoadPromptDetailQuery query = LoadPromptDetailQuery.builder()
                .promptUuid(uuid)
                .userId(userId)
                .build();
            when(promptTemplateJpaRepository.findByUuidWithRelations(uuid)).thenThrow(new RuntimeException("DB error"));

            // When & Then
            assertThatThrownBy(() -> promptLoadAdapter.loadPromptDetailBy(query))
                .isInstanceOf(PromptOperationException.class)
                .hasMessageContaining("프롬프트 상세 조회 실패")
                .hasFieldOrPropertyWithValue("errorCode", PromptErrorType.OPERATION_FAILED);
        }
    }

    @Nested
    @DisplayName("PromptTemplate 필수값 유효성 검증")
    class PromptTemplateValidation {
        @Test
        @DisplayName("title이 null이면 PromptValidationException 발생")
        void givenNullTitle_whenBuildPromptTemplate_thenThrowException() {
            // Given
            String title = null;
            Long createdById = 1L;
            String description = "desc";

            // When & Then
            assertThatThrownBy(() -> PromptTemplate.builder()
                .title(title)
                .createdById(createdById)
                .description(description)
                .build())
                .isInstanceOf(com.gongdel.promptserver.domain.exception.PromptValidationException.class)
                .hasMessageContaining("제목은 비어있을 수 없습니다");
        }

        @Test
        @DisplayName("title이 blank면 PromptValidationException 발생")
        void givenBlankTitle_whenBuildPromptTemplate_thenThrowException() {
            // Given
            String title = "   ";
            Long createdById = 1L;
            String description = "desc";

            // When & Then
            assertThatThrownBy(() -> PromptTemplate.builder()
                .title(title)
                .createdById(createdById)
                .description(description)
                .build())
                .isInstanceOf(com.gongdel.promptserver.domain.exception.PromptValidationException.class)
                .hasMessageContaining("제목은 비어있을 수 없습니다");
        }

        @Test
        @DisplayName("createdById가 null이면 PromptValidationException 발생")
        void givenNullCreatedById_whenBuildPromptTemplate_thenThrowException() {
            // Given
            String title = "title";
            Long createdById = null;
            String description = "desc";

            // When & Then
            assertThatThrownBy(() -> PromptTemplate.builder()
                .title(title)
                .createdById(createdById)
                .description(description)
                .build())
                .isInstanceOf(com.gongdel.promptserver.domain.exception.PromptValidationException.class)
                .hasMessageContaining("생성자 ID는 필수입니다");
        }

        @Test
        @DisplayName("description이 1000자 초과면 PromptValidationException 발생")
        void givenTooLongDescription_whenBuildPromptTemplate_thenThrowException() {
            // Given
            String title = "title";
            Long createdById = 1L;
            String description = "a".repeat(1001);

            // When & Then
            assertThatThrownBy(() -> PromptTemplate.builder()
                .title(title)
                .createdById(createdById)
                .description(description)
                .build())
                .isInstanceOf(com.gongdel.promptserver.domain.exception.PromptValidationException.class)
                .hasMessageContaining("설명은 1000자를 초과할 수 없습니다");
        }

        @Test
        @DisplayName("정상 입력 시 PromptTemplate 생성 성공")
        void givenValidFields_whenBuildPromptTemplate_thenSuccess() {
            // Given
            String title = "title";
            Long createdById = 1L;
            String description = "desc";

            // When
            PromptTemplate template = PromptTemplate.builder()
                .title(title)
                .createdById(createdById)
                .description(description)
                .build();

            // Then
            assertThat(template.getTitle()).isEqualTo(title);
            assertThat(template.getCreatedById()).isEqualTo(createdById);
            assertThat(template.getDescription()).isEqualTo(description);
        }
    }
}
