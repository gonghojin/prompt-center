package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.exception.PromptVersionOperationFailedException;
import com.gongdel.promptserver.application.port.in.query.LoadPromptDetailQuery;
import com.gongdel.promptserver.application.port.in.query.view.GetViewCountQuery;
import com.gongdel.promptserver.application.port.out.query.LoadPromptPort;
import com.gongdel.promptserver.application.port.out.query.SearchPromptsPort;
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
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class PromptsQueryServiceTest {

    @Mock
    LoadPromptPort loadPromptPort;
    @Mock
    SearchPromptsPort searchPromptsPort;
    @Mock
    ViewQueryService viewQueryService;

    @InjectMocks
    PromptsQueryService promptsQueryService;

    UUID promptId;
    PromptDetail promptDetail;
    PromptSearchCondition searchCondition;
    PromptSearchResult searchResult;
    Pageable pageable;
    User testUser;

    @BeforeEach
    void setUp() {
        promptId = UUID.randomUUID();

        // User 객체 생성
        testUser = User.builder()
            .id(1L)
            .uuid(new UserId(UUID.randomUUID()))
            .email(new Email("test@example.com"))
            .name("테스트 사용자")
            .status(UserStatus.ACTIVE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        promptDetail = PromptDetail.builder()
            .id(promptId)
            .title("테스트 프롬프트")
            .description("설명")
            .content("내용")
            .author(testUser)
            .tags(Set.of("태그1", "태그2"))
            .isPublic(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .viewCount(0)
            .favoriteCount(10)
            .categoryId(1L)
            .visibility(Visibility.PUBLIC.name())
            .status(PromptStatus.PUBLISHED.name())
            .isFavorite(false)
            .isLiked(false)
            .build();
        searchCondition = PromptSearchCondition.builder()
            .title("테스트")
            .build();
        searchResult = PromptSearchResult.builder()
            .id(1L)
            .uuid(UUID.randomUUID())
            .title("테스트 프롬프트")
            .description("설명")
            .currentVersionId(1L)
            .categoryId(1L)
            .categoryName("카테고리")
            .createdById(1L)
            .createdByName("홍길동")
            .tags(Collections.singletonList("태그"))
            .visibility(Visibility.PUBLIC)
            .status(PromptStatus.PUBLISHED)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .stats(new PromptStats(0, 50))
            .isFavorite(false)
            .isLiked(false)
            .build();
        pageable = PageRequest.of(0, 20);
    }

    @Nested
    @DisplayName("loadPromptDetailByUuid")
    class LoadPromptDetailByUuid {
        @Test
        @DisplayName("존재하는 프롬프트 ID로 조회 시 조회수와 함께 Optional 반환")
        void loadPromptDetail_success() {
            // given
            LoadPromptDetailQuery query = LoadPromptDetailQuery.builder()
                .promptUuid(promptId)
                .userId(1L)
                .build();
            long expectedViewCount = 150L;

            given(loadPromptPort.loadPromptDetailBy(query)).willReturn(Optional.of(promptDetail));
            given(viewQueryService.getTotalViewCount(any(GetViewCountQuery.class))).willReturn(expectedViewCount);

            // when
            Optional<PromptDetail> result = promptsQueryService.loadPromptDetailByUuid(query);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(promptId);
            assertThat(result.get().getViewCount()).isEqualTo((int) expectedViewCount);
            verify(viewQueryService).getTotalViewCount(any(GetViewCountQuery.class));
        }

        @Test
        @DisplayName("존재하지 않는 프롬프트 ID로 조회 시 Optional.empty 반환")
        void loadPromptDetail_notFound() {
            // given
            LoadPromptDetailQuery query = LoadPromptDetailQuery.builder()
                .promptUuid(promptId)
                .userId(1L)
                .build();
            given(loadPromptPort.loadPromptDetailBy(query)).willReturn(Optional.empty());

            // when
            Optional<PromptDetail> result = promptsQueryService.loadPromptDetailByUuid(query);

            // then
            assertThat(result).isEmpty();
            verifyNoInteractions(viewQueryService);
        }

        @Test
        @DisplayName("ViewQueryService에서 예외 발생 시 조회수 0으로 설정")
        void loadPromptDetail_viewServiceException() {
            // given
            LoadPromptDetailQuery query = LoadPromptDetailQuery.builder()
                .promptUuid(promptId)
                .userId(1L)
                .build();

            given(loadPromptPort.loadPromptDetailBy(query)).willReturn(Optional.of(promptDetail));
            given(viewQueryService.getTotalViewCount(any(GetViewCountQuery.class)))
                .willThrow(new RuntimeException("View service error"));

            // when
            Optional<PromptDetail> result = promptsQueryService.loadPromptDetailByUuid(query);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getViewCount()).isEqualTo(0);
            verify(viewQueryService).getTotalViewCount(any(GetViewCountQuery.class));
        }
    }

    @Nested
    @DisplayName("searchPrompts")
    class SearchPrompts {
        @Test
        @DisplayName("정상적으로 검색 결과와 조회수 정보를 통합하여 반환")
        void searchPrompts_success() {
            // given
            PromptSearchResult searchResult2 = PromptSearchResult.builder()
                .id(2L)
                .uuid(UUID.randomUUID())
                .title("테스트 프롬프트 2")
                .description("설명 2")
                .currentVersionId(2L)
                .categoryId(1L)
                .categoryName("카테고리")
                .createdById(1L)
                .createdByName("홍길동")
                .tags(Collections.singletonList("태그"))
                .visibility(Visibility.PUBLIC)
                .status(PromptStatus.PUBLISHED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .stats(new PromptStats(0, 30))
                .isFavorite(false)
                .isLiked(false)
                .build();

            Page<PromptSearchResult> page = new PageImpl<>(
                Arrays.asList(searchResult, searchResult2), pageable, 2);

            Map<Long, Long> viewCountMap = Map.of(
                1L, 100L,
                2L, 200L);

            given(searchPromptsPort.searchPrompts(any())).willReturn(page);
            given(viewQueryService.getViewCountsByPromptIds(anyList())).willReturn(viewCountMap);

            // when
            Page<PromptSearchResult> result = promptsQueryService.searchPrompts(searchCondition);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);

            PromptSearchResult firstResult = result.getContent().get(0);
            assertThat(firstResult.getStats().getViewCount()).isEqualTo(100);

            PromptSearchResult secondResult = result.getContent().get(1);
            assertThat(secondResult.getStats().getViewCount()).isEqualTo(200);

            verify(viewQueryService).getViewCountsByPromptIds(Arrays.asList(1L, 2L));
        }

        @Test
        @DisplayName("검색 결과가 비어있을 때 ViewQueryService 호출하지 않음")
        void searchPrompts_emptyResults() {
            // given
            Page<PromptSearchResult> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            given(searchPromptsPort.searchPrompts(any())).willReturn(emptyPage);

            // when
            Page<PromptSearchResult> result = promptsQueryService.searchPrompts(searchCondition);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            verifyNoInteractions(viewQueryService);
        }

        @Test
        @DisplayName("ViewQueryService에서 조회수 정보 누락 시 기본값 0으로 설정")
        void searchPrompts_missingViewCount() {
            // given
            Page<PromptSearchResult> page = new PageImpl<>(
                Collections.singletonList(searchResult), pageable, 1);

            // 일부 프롬프트의 조회수 정보만 있는 맵
            Map<Long, Long> partialViewCountMap = Map.of(999L, 100L); // 다른 ID

            given(searchPromptsPort.searchPrompts(any())).willReturn(page);
            given(viewQueryService.getViewCountsByPromptIds(anyList())).willReturn(partialViewCountMap);

            // when
            Page<PromptSearchResult> result = promptsQueryService.searchPrompts(searchCondition);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getStats().getViewCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("검색 중 예외 발생 시 PromptVersionOperationFailedException 변환")
        void searchPrompts_exception() {
            // given
            given(searchPromptsPort.searchPrompts(any())).willThrow(new RuntimeException("DB error"));

            // when & then
            assertThatThrownBy(() -> promptsQueryService.searchPrompts(searchCondition))
                .isInstanceOf(PromptVersionOperationFailedException.class)
                .hasMessageContaining("Error occurred during prompt search operation");
        }

        @Test
        @DisplayName("ViewQueryService에서 예외 발생 시 PromptVersionOperationFailedException 변환")
        void searchPrompts_viewServiceException() {
            // given
            Page<PromptSearchResult> page = new PageImpl<>(
                Collections.singletonList(searchResult), pageable, 1);

            given(searchPromptsPort.searchPrompts(any())).willReturn(page);
            given(viewQueryService.getViewCountsByPromptIds(anyList()))
                .willThrow(new RuntimeException("View service error"));

            // when & then
            assertThatThrownBy(() -> promptsQueryService.searchPrompts(searchCondition))
                .isInstanceOf(PromptVersionOperationFailedException.class)
                .hasMessageContaining("Error occurred during prompt search operation");
        }
    }

    @Nested
    @DisplayName("조회수 통합 로직 검증")
    class ViewCountIntegrationLogic {
        @Test
        @DisplayName("PromptDetail에 조회수 정보가 정확히 통합됨")
        void promptDetailWithViewCount() {
            // given
            LoadPromptDetailQuery query = LoadPromptDetailQuery.builder()
                .promptUuid(promptId)
                .userId(1L)
                .build();
            long expectedViewCount = 42L;

            given(loadPromptPort.loadPromptDetailBy(query)).willReturn(Optional.of(promptDetail));
            given(viewQueryService.getTotalViewCount(any(GetViewCountQuery.class))).willReturn(expectedViewCount);

            // when
            Optional<PromptDetail> result = promptsQueryService.loadPromptDetailByUuid(query);

            // then
            assertThat(result).isPresent();
            PromptDetail enrichedDetail = result.get();

            // 기존 정보는 그대로 유지
            assertThat(enrichedDetail.getId()).isEqualTo(promptDetail.getId());
            assertThat(enrichedDetail.getTitle()).isEqualTo(promptDetail.getTitle());
            assertThat(enrichedDetail.getFavoriteCount()).isEqualTo(promptDetail.getFavoriteCount());

            // 조회수만 업데이트됨
            assertThat(enrichedDetail.getViewCount()).isEqualTo((int) expectedViewCount);
        }

        @Test
        @DisplayName("PromptSearchResult에 조회수 정보가 정확히 통합됨")
        void promptSearchResultWithViewCount() {
            // given
            Page<PromptSearchResult> page = new PageImpl<>(
                Collections.singletonList(searchResult), pageable, 1);

            Map<Long, Long> viewCountMap = Map.of(1L, 789L);

            given(searchPromptsPort.searchPrompts(any())).willReturn(page);
            given(viewQueryService.getViewCountsByPromptIds(anyList())).willReturn(viewCountMap);

            // when
            Page<PromptSearchResult> result = promptsQueryService.searchPrompts(searchCondition);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);

            PromptSearchResult enrichedResult = result.getContent().get(0);

            // 기존 정보는 그대로 유지
            assertThat(enrichedResult.getId()).isEqualTo(searchResult.getId());
            assertThat(enrichedResult.getTitle()).isEqualTo(searchResult.getTitle());
            assertThat(enrichedResult.getStats().getFavoriteCount())
                .isEqualTo(searchResult.getStats().getFavoriteCount());

            // 조회수만 업데이트됨
            assertThat(enrichedResult.getStats().getViewCount()).isEqualTo(789);
        }
    }

    @Nested
    @DisplayName("파라미터 검증")
    class ParameterValidation {
        @Test
        @DisplayName("loadPromptDetailByUuid에 null 쿼리 전달 시 예외 발생")
        void loadPromptDetail_nullQuery() {
            // when & then
            assertThatThrownBy(() -> promptsQueryService.loadPromptDetailByUuid(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("LoadPromptDetailQuery must not be null");
        }

        @Test
        @DisplayName("searchPrompts에 null 조건 전달 시 예외 발생")
        void searchPrompts_nullCondition() {
            // when & then
            assertThatThrownBy(() -> promptsQueryService.searchPrompts(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PromptSearchCondition must not be null");
        }
    }
}
