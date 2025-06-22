package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.entity.*;
import com.gongdel.promptserver.adapter.out.persistence.repository.*;
import com.gongdel.promptserver.domain.exception.PromptErrorType;
import com.gongdel.promptserver.domain.exception.PromptOperationException;
import com.gongdel.promptserver.domain.model.PromptSearchCondition;
import com.gongdel.promptserver.domain.model.PromptSearchResult;
import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.model.Visibility;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PromptSearchAdapter 단위 테스트")
class PromptSearchAdapterTest {

    @Mock
    PromptTemplateQueryRepository promptTemplateQueryRepository;

    @Mock
    FavoriteRepository favoriteRepository;

    @Mock
    PromptLikeCountRepository promptLikeCountRepository;

    @Mock
    PromptLikeJpaRepository promptLikeJpaRepository;

    @InjectMocks
    PromptSearchAdapter promptSearchAdapter;

    @Nested
    @DisplayName("searchPrompts 메서드")
    class SearchPrompts {
        @Test
        @DisplayName("Given 정상 조건, When 검색, Then 결과 Page 반환")
        void givenValidCondition_whenSearch_thenReturnPage() {
            // Given
            PromptSearchCondition condition = PromptSearchCondition.builder()
                .title("test")
                .pageable(PageRequest.of(0, 10))
                .build();

            PromptTemplateEntity entity = new PromptTemplateEntity();
            entity.setId(1L);
            entity.setUuid(UUID.randomUUID());
            entity.setTitle("test");
            entity.setDescription("desc");
            entity.setCurrentVersionId(100L);
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setStatus(PromptStatus.DRAFT);
            entity.setVisibility(Visibility.PUBLIC);

            // 카테고리
            CategoryEntity category = new CategoryEntity();
            category.setId(10L);
            category.setName("cat");
            entity.setCategory(category);

            // 작성자
            UserEntity user = new UserEntity();
            user.setId(20L);
            user.setName("user");
            entity.setCreatedBy(user);

            // 태그
            TagEntity tag = TagEntity.create(30L, "tag1");
            PromptTemplateTagEntity tagRel = new PromptTemplateTagEntity(entity, tag);
            entity.setTagRelations(List.of(tagRel));

            Page<PromptTemplateEntity> page = new PageImpl<>(List.of(entity), condition.getPageable(), 1);
            when(promptTemplateQueryRepository.searchPrompts(condition)).thenReturn(page);

            // When
            Page<PromptSearchResult> result = promptSearchAdapter.searchPrompts(condition);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);
            PromptSearchResult res = result.getContent().get(0);
            assertThat(res.getId()).isEqualTo(entity.getId());
            assertThat(res.getTitle()).isEqualTo(entity.getTitle());
            assertThat(res.getTags()).containsExactly("tag1");
        }

        @Test
        @DisplayName("Given null 조건, When 검색, Then IllegalArgumentException 발생")
        void givenNullCondition_whenSearch_thenIllegalArgumentException() {
            // Given
            PromptSearchCondition condition = null;

            // When & Then
            assertThatThrownBy(() -> promptSearchAdapter.searchPrompts(condition))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Search condition must not be null");
        }

        @Test
        @DisplayName("Given repository 예외, When 검색, Then PromptOperationException 발생")
        void givenRepositoryException_whenSearch_thenPromptOperationException() {
            // Given
            PromptSearchCondition condition = PromptSearchCondition.builder()
                .title("test")
                .pageable(PageRequest.of(0, 10))
                .build();
            when(promptTemplateQueryRepository.searchPrompts(condition))
                .thenThrow(new RuntimeException("DB error"));

            // When & Then
            assertThatThrownBy(() -> promptSearchAdapter.searchPrompts(condition))
                .isInstanceOf(PromptOperationException.class)
                .hasMessageContaining("프롬프트 검색 실패")
                .hasFieldOrPropertyWithValue("errorCode", PromptErrorType.OPERATION_FAILED);
        }

        @Test
        @DisplayName("Given 즐겨찾기 ID 포함, When 검색, Then isFavorite true")
        void givenFavoriteId_whenSearch_thenIsFavoriteTrue() {
            // Given
            PromptSearchCondition condition = PromptSearchCondition.builder()
                .userId(99L)
                .title("test")
                .pageable(PageRequest.of(0, 10))
                .build();

            PromptTemplateEntity entity = new PromptTemplateEntity();
            entity.setId(1L);
            entity.setUuid(UUID.randomUUID());
            entity.setTitle("test");
            entity.setDescription("desc");
            entity.setCurrentVersionId(100L);
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setStatus(PromptStatus.DRAFT);
            entity.setVisibility(Visibility.PUBLIC);
            CategoryEntity category = new CategoryEntity();
            category.setId(10L);
            category.setName("cat");
            entity.setCategory(category);
            UserEntity user = new UserEntity();
            user.setId(20L);
            user.setName("user");
            entity.setCreatedBy(user);
            TagEntity tag = TagEntity.create(30L, "tag1");
            PromptTemplateTagEntity tagRel = new PromptTemplateTagEntity(entity, tag);
            entity.setTagRelations(List.of(tagRel));

            Page<PromptTemplateEntity> page = new PageImpl<>(List.of(entity), condition.getPageable(), 1);
            when(promptTemplateQueryRepository.searchPrompts(condition)).thenReturn(page);
            when(favoriteRepository.findPromptTemplateIdsByUserIdAndPromptTemplateIdsIn(eq(99L), anyList()))
                .thenReturn(List.of(1L));

            // When
            Page<PromptSearchResult> result = promptSearchAdapter.searchPrompts(condition);

            // Then
            assertThat(result.getContent().get(0).isFavorite()).isTrue();
        }

        @Test
        @DisplayName("Given 즐겨찾기 ID 미포함, When 검색, Then isFavorite false")
        void givenNoFavoriteId_whenSearch_thenIsFavoriteFalse() {
            // Given
            PromptSearchCondition condition = PromptSearchCondition.builder()
                .userId(99L)
                .title("test")
                .pageable(PageRequest.of(0, 10))
                .build();

            PromptTemplateEntity entity = new PromptTemplateEntity();
            entity.setId(1L);
            entity.setUuid(UUID.randomUUID());
            entity.setTitle("test");
            entity.setDescription("desc");
            entity.setCurrentVersionId(100L);
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setStatus(PromptStatus.DRAFT);
            entity.setVisibility(Visibility.PUBLIC);
            CategoryEntity category = new CategoryEntity();
            category.setId(10L);
            category.setName("cat");
            entity.setCategory(category);
            UserEntity user = new UserEntity();
            user.setId(20L);
            user.setName("user");
            entity.setCreatedBy(user);
            TagEntity tag = TagEntity.create(30L, "tag1");
            PromptTemplateTagEntity tagRel = new PromptTemplateTagEntity(entity, tag);
            entity.setTagRelations(List.of(tagRel));

            Page<PromptTemplateEntity> page = new PageImpl<>(List.of(entity), condition.getPageable(), 1);
            when(promptTemplateQueryRepository.searchPrompts(condition)).thenReturn(page);
            when(favoriteRepository.findPromptTemplateIdsByUserIdAndPromptTemplateIdsIn(eq(99L), anyList()))
                .thenReturn(List.of(2L)); // 즐겨찾기 미포함

            // When
            Page<PromptSearchResult> result = promptSearchAdapter.searchPrompts(condition);

            // Then
            assertThat(result.getContent().get(0).isFavorite()).isFalse();
        }

        @Test
        @DisplayName("Given 즐겨찾기 repository가 null 반환, When 검색, Then isFavorite false")
        void givenFavoriteRepositoryReturnsNull_whenSearch_thenIsFavoriteFalse() {
            // Given
            PromptSearchCondition condition = PromptSearchCondition.builder()
                .userId(99L)
                .title("test")
                .pageable(PageRequest.of(0, 10))
                .build();

            PromptTemplateEntity entity = new PromptTemplateEntity();
            entity.setId(1L);
            entity.setUuid(UUID.randomUUID());
            entity.setTitle("test");
            entity.setDescription("desc");
            entity.setCurrentVersionId(100L);
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setStatus(PromptStatus.DRAFT);
            entity.setVisibility(Visibility.PUBLIC);
            CategoryEntity category = new CategoryEntity();
            category.setId(10L);
            category.setName("cat");
            entity.setCategory(category);
            UserEntity user = new UserEntity();
            user.setId(20L);
            user.setName("user");
            entity.setCreatedBy(user);
            TagEntity tag = TagEntity.create(30L, "tag1");
            PromptTemplateTagEntity tagRel = new PromptTemplateTagEntity(entity, tag);
            entity.setTagRelations(List.of(tagRel));

            Page<PromptTemplateEntity> page = new PageImpl<>(List.of(entity), condition.getPageable(), 1);
            when(promptTemplateQueryRepository.searchPrompts(condition)).thenReturn(page);
            when(favoriteRepository.findPromptTemplateIdsByUserIdAndPromptTemplateIdsIn(eq(99L), anyList()))
                .thenReturn(null); // null 반환

            // When
            Page<PromptSearchResult> result = promptSearchAdapter.searchPrompts(condition);

            // Then
            assertThat(result.getContent().get(0).isFavorite()).isFalse();
        }

        @Test
        @DisplayName("Given 좋아요 수, When 검색, Then likeCount가 매핑된다")
        void givenLikeCount_whenSearch_thenLikeCountMapped() {
            // Given
            PromptSearchCondition condition = PromptSearchCondition.builder()
                .title("test")
                .pageable(PageRequest.of(0, 10))
                .build();
            PromptTemplateEntity entity = new PromptTemplateEntity();
            entity.setId(1L);
            entity.setUuid(UUID.randomUUID());
            entity.setTitle("test");
            entity.setDescription("desc");
            entity.setCurrentVersionId(100L);
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setStatus(PromptStatus.DRAFT);
            entity.setVisibility(Visibility.PUBLIC);
            CategoryEntity category = new CategoryEntity();
            category.setId(10L);
            category.setName("cat");
            entity.setCategory(category);
            UserEntity user = new UserEntity();
            user.setId(20L);
            user.setName("user");
            entity.setCreatedBy(user);
            TagEntity tag = TagEntity.create(30L, "tag1");
            PromptTemplateTagEntity tagRel = new PromptTemplateTagEntity(entity, tag);
            entity.setTagRelations(List.of(tagRel));
            Page<PromptTemplateEntity> page = new PageImpl<>(List.of(entity), condition.getPageable(), 1);
            when(promptTemplateQueryRepository.searchPrompts(condition)).thenReturn(page);
            // 좋아요 수 5
            PromptLikeCountProjection projection;
            projection = PromptLikeCountProjection.of(1L, 5L);
            when(promptLikeCountRepository.findLikeCountsByPromptTemplateIds(List.of(1L)))
                .thenReturn(List.of(projection));

            // When
            Page<PromptSearchResult> result = promptSearchAdapter.searchPrompts(condition);

            // Then
            assertThat(result.getContent().get(0).getStats().getFavoriteCount()).isEqualTo(5);
        }

        @Test
        @DisplayName("Given 사용자가 좋아요한 프롬프트, When 검색, Then isLiked true")
        void givenUserLikedPrompt_whenSearch_thenIsLikedTrue() {
            // Given
            PromptSearchCondition condition = PromptSearchCondition.builder()
                .userId(99L)
                .title("test")
                .pageable(PageRequest.of(0, 10))
                .build();
            PromptTemplateEntity entity = new PromptTemplateEntity();
            entity.setId(1L);
            entity.setUuid(UUID.randomUUID());
            entity.setTitle("test");
            entity.setDescription("desc");
            entity.setCurrentVersionId(100L);
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setStatus(PromptStatus.DRAFT);
            entity.setVisibility(Visibility.PUBLIC);
            CategoryEntity category = new CategoryEntity();
            category.setId(10L);
            category.setName("cat");
            entity.setCategory(category);
            UserEntity user = new UserEntity();
            user.setId(20L);
            user.setName("user");
            entity.setCreatedBy(user);
            TagEntity tag = TagEntity.create(30L, "tag1");
            PromptTemplateTagEntity tagRel = new PromptTemplateTagEntity(entity, tag);
            entity.setTagRelations(List.of(tagRel));
            Page<PromptTemplateEntity> page = new PageImpl<>(List.of(entity), condition.getPageable(), 1);
            when(promptTemplateQueryRepository.searchPrompts(condition)).thenReturn(page);
            when(promptLikeJpaRepository.findPromptTemplateIdsLikedByUser(99L, List.of(1L)))
                .thenReturn(List.of(1L));
            when(promptLikeCountRepository.findLikeCountsByPromptTemplateIds(List.of(1L)))
                .thenReturn(List.of());

            // When
            Page<PromptSearchResult> result = promptSearchAdapter.searchPrompts(condition);

            // Then
            assertThat(result.getContent().get(0).isLiked()).isTrue();
        }

        @Test
        @DisplayName("Given 태그 없는 프롬프트, When 검색, Then tags는 빈 리스트")
        void givenNoTags_whenSearch_thenTagsEmpty() {
            // Given
            PromptSearchCondition condition = PromptSearchCondition.builder()
                .title("test")
                .pageable(PageRequest.of(0, 10))
                .build();
            PromptTemplateEntity entity = new PromptTemplateEntity();
            entity.setId(1L);
            entity.setUuid(UUID.randomUUID());
            entity.setTitle("test");
            entity.setDescription("desc");
            entity.setCurrentVersionId(100L);
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setStatus(PromptStatus.DRAFT);
            entity.setVisibility(Visibility.PUBLIC);
            CategoryEntity category = new CategoryEntity();
            category.setId(10L);
            category.setName("cat");
            entity.setCategory(category);
            UserEntity user = new UserEntity();
            user.setId(20L);
            user.setName("user");
            entity.setCreatedBy(user);
            // 태그 없음
            entity.setTagRelations(null);
            Page<PromptTemplateEntity> page = new PageImpl<>(List.of(entity), condition.getPageable(), 1);
            when(promptTemplateQueryRepository.searchPrompts(condition)).thenReturn(page);
            when(promptLikeCountRepository.findLikeCountsByPromptTemplateIds(List.of(1L)))
                .thenReturn(List.of());

            // When
            Page<PromptSearchResult> result = promptSearchAdapter.searchPrompts(condition);

            // Then
            assertThat(result.getContent().get(0).getTags()).isEmpty();
        }

        @Test
        @DisplayName("Given promptTemplateIds 비어있음, When 검색, Then favorite/like repository 호출 안함")
        void givenEmptyPromptTemplateIds_whenSearch_thenNoFavoriteOrLikeRepoCall() {
            // Given
            PromptSearchCondition condition = PromptSearchCondition.builder()
                .userId(99L)
                .title("test")
                .pageable(PageRequest.of(0, 10))
                .build();
            Page<PromptTemplateEntity> page = new PageImpl<>(List.of(), condition.getPageable(), 0);
            when(promptTemplateQueryRepository.searchPrompts(condition)).thenReturn(page);

            // When
            Page<PromptSearchResult> result = promptSearchAdapter.searchPrompts(condition);

            // Then
            assertThat(result.getContent()).isEmpty();
        }
    }
}
