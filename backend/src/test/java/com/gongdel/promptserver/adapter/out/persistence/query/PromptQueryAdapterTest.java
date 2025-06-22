package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.entity.CategoryEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.PromptTemplateEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.PromptVersionEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.UserEntity;
import com.gongdel.promptserver.adapter.out.persistence.mapper.PromptVersionMapper;
import com.gongdel.promptserver.adapter.out.persistence.mapper.UserMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.PromptTemplateJpaRepository;
import com.gongdel.promptserver.adapter.out.persistence.repository.PromptVersionRepository;
import com.gongdel.promptserver.domain.exception.PromptErrorType;
import com.gongdel.promptserver.domain.exception.PromptOperationException;
import com.gongdel.promptserver.domain.model.*;
import com.gongdel.promptserver.domain.user.Email;
import com.gongdel.promptserver.domain.user.User;
import com.gongdel.promptserver.domain.user.UserId;
import com.gongdel.promptserver.domain.user.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("PromptQueryAdapter 단위 테스트")
class PromptQueryAdapterTest {

    @Mock
    private PromptTemplateJpaRepository promptTemplateJpaRepository;
    @Mock
    private PromptVersionRepository promptVersionRepository;
    @Mock
    private PromptVersionMapper promptVersionMapper;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private PromptQueryAdapter promptQueryAdapter;

    private User user;
    private UserEntity userEntity;
    private Category category;
    private CategoryEntity categoryEntity;
    private PromptStatus status;
    private Visibility visibility;
    private Pageable pageable;
    private PromptTemplateEntity promptTemplateEntity;
    private PromptVersionEntity promptVersionEntity;
    private PromptVersion promptVersion;
    private PromptTemplate promptTemplate;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .id(null)
            .uuid(new UserId(UUID.randomUUID()))
            .email(new Email("test@naver.com"))
            .name("테스터")
            .team(null)
            .status(UserStatus.ACTIVE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("테스터");
        LocalDateTime now = LocalDateTime.now();
        category = new Category(1L, "카테고리", "카테고리명", "카테고리 설명", null, false, now, now);
        categoryEntity = new CategoryEntity();
        categoryEntity.setId(1L);
        categoryEntity.setName("카테고리");
        categoryEntity.setDisplayName("카테고리명");
        categoryEntity.setDescription("카테고리 설명");
        categoryEntity.setSystem(false);
        categoryEntity.setParentCategory(null);
        categoryEntity.setCreatedAt(now);
        categoryEntity.setUpdatedAt(now);
        status = PromptStatus.PUBLISHED;
        visibility = Visibility.PUBLIC;
        pageable = PageRequest.of(0, 10);
        promptTemplateEntity = new PromptTemplateEntity();
        promptTemplateEntity.setId(1L);
        promptTemplateEntity.setUuid(UUID.randomUUID());
        promptTemplateEntity.setTitle("테스트 프롬프트");
        promptTemplateEntity.setCreatedBy(userEntity);
        promptTemplateEntity.setCategory(categoryEntity);
        promptTemplateEntity.setStatus(status);
        promptTemplateEntity.setVisibility(visibility);
        promptTemplateEntity.setCurrentVersionId(100L);
        promptTemplateEntity.setDescription("설명");
        promptTemplateEntity.setCreatedAt(now);
        promptTemplateEntity.setUpdatedAt(now);
        promptVersionEntity = new PromptVersionEntity();
        promptVersionEntity.setId(100L);
        promptVersion = PromptVersion.builder()
            .id(100L)
            .promptTemplateId(1L)
            .versionNumber(1)
            .content("버전 내용")
            .createdById(1L)
            .actionType(PromptVersionActionType.CREATE)
            .uuid(UUID.randomUUID())
            .build();
        promptTemplate = PromptTemplate.builder()
            .id(1L)
            .uuid(promptTemplateEntity.getUuid())
            .title("테스트 프롬프트")
            .currentVersionId(100L)
            .categoryId(1L)
            .createdById(1L)
            .visibility(visibility)
            .status(status)
            .description("설명")
            .createdAt(now)
            .updatedAt(now)
            .build();
    }

    @Nested
    @DisplayName("findPromptsByCreatedByAndStatus")
    class FindPromptsByCreatedByAndStatus {
        @Test
        @DisplayName("Given 정상 입력, When 조회, Then Page<PromptTemplate> 반환")
        void givenValidInput_whenFind_thenReturnPage() {
            // Given
            Page<PromptTemplateEntity> entityPage = new PageImpl<>(List.of(promptTemplateEntity), pageable, 1);
            given(promptTemplateJpaRepository.findByCreatedByAndStatusWithRelations(isNull(), eq(status), eq(pageable)))
                .willReturn(entityPage);

            // When
            Page<PromptTemplate> result = promptQueryAdapter.findPromptsByCreatedByAndStatus(user, status, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Given repository 예외, When 조회, Then PromptOperationException 발생")
        void givenRepositoryException_whenFind_thenPromptOperationException() {
            // Given
            given(promptTemplateJpaRepository.findByCreatedByAndStatusWithRelations(isNull(), eq(status), eq(pageable)))
                .willThrow(new RuntimeException("DB error"));
            // When & Then
            assertThatThrownBy(() -> promptQueryAdapter.findPromptsByCreatedByAndStatus(user, status, pageable))
                .isInstanceOf(PromptOperationException.class)
                .hasMessageContaining("프롬프트 작성자/상태 목록 조회 실패")
                .hasFieldOrPropertyWithValue("errorCode", PromptErrorType.OPERATION_FAILED);
        }
    }

    @Nested
    @DisplayName("findPromptsByVisibilityAndStatus")
    class FindPromptsByVisibilityAndStatus {
        @Test
        @DisplayName("Given 정상 입력, When 조회, Then Page<PromptTemplate> 반환")
        void givenValidInput_whenFind_thenReturnPage() {
            // Given
            Page<PromptTemplateEntity> entityPage = new PageImpl<>(List.of(promptTemplateEntity), pageable, 1);
            given(promptTemplateJpaRepository.findByVisibilityAndStatusWithRelations(eq(visibility), eq(status),
                eq(pageable)))
                .willReturn(entityPage);

            // When
            Page<PromptTemplate> result = promptQueryAdapter.findPromptsByVisibilityAndStatus(visibility, status,
                pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Given repository 예외, When 조회, Then PromptOperationException 발생")
        void givenRepositoryException_whenFind_thenPromptOperationException() {
            // Given
            given(promptTemplateJpaRepository.findByVisibilityAndStatusWithRelations(eq(visibility), eq(status),
                eq(pageable)))
                .willThrow(new RuntimeException("DB error"));
            // When & Then
            assertThatThrownBy(() -> promptQueryAdapter.findPromptsByVisibilityAndStatus(visibility, status, pageable))
                .isInstanceOf(PromptOperationException.class)
                .hasMessageContaining("프롬프트 가시성/상태 목록 조회 실패")
                .hasFieldOrPropertyWithValue("errorCode", PromptErrorType.OPERATION_FAILED);
        }
    }

    @Nested
    @DisplayName("findPromptsByCategoryAndStatus")
    class FindPromptsByCategoryAndStatus {
        @Test
        @DisplayName("Given 정상 입력, When 조회, Then Page<PromptTemplate> 반환")
        void givenValidInput_whenFind_thenReturnPage() {
            // Given
            Page<PromptTemplateEntity> entityPage = new PageImpl<>(List.of(promptTemplateEntity), pageable, 1);
            given(promptTemplateJpaRepository.findByCategoryAndStatusWithRelations(any(CategoryEntity.class),
                eq(status), eq(pageable)))
                .willReturn(entityPage);

            // When
            Page<PromptTemplate> result = promptQueryAdapter.findPromptsByCategoryAndStatus(category, status, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Given repository 예외, When 조회, Then 예외 전파")
        void givenRepositoryException_whenFind_thenExceptionPropagated() {
            // Given
            given(promptTemplateJpaRepository.findByCategoryAndStatusWithRelations(any(CategoryEntity.class),
                eq(status), eq(pageable)))
                .willThrow(new RuntimeException("DB error"));
            // When & Then
            assertThatThrownBy(() -> promptQueryAdapter.findPromptsByCategoryAndStatus(category, status, pageable))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DB error");
        }
    }
}
