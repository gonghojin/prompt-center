package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.port.out.query.LoadFavoritesPort;
import com.gongdel.promptserver.application.port.out.query.SearchFavoritePort;
import com.gongdel.promptserver.domain.exception.FavoriteException;
import com.gongdel.promptserver.domain.model.PromptStats;
import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.model.Visibility;
import com.gongdel.promptserver.domain.model.favorite.FavoritePromptResult;
import com.gongdel.promptserver.domain.model.favorite.FavoriteSearchCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import static org.mockito.Mockito.*;

@DisplayName("FavoriteQueryService 테스트")
class FavoriteQueryServiceTest {

    @Mock
    private LoadFavoritesPort loadFavoritesPort;
    @Mock
    private SearchFavoritePort searchFavoritePort;
    @Mock
    private ViewQueryService viewQueryService;
    @InjectMocks
    private FavoriteQueryService favoriteQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("countByUser(Long) 메서드는")
    class CountByUserTest {
        @Test
        @DisplayName("정상적으로 즐겨찾기 개수를 반환한다")
        void givenValidUserId_whenCountByUser_thenReturnsCount() {
            // Given
            Long userId = 1L;
            when(loadFavoritesPort.countByUser(userId)).thenReturn(5L);
            // When
            long result = favoriteQueryService.countByUser(userId);
            // Then
            assertThat(result).isEqualTo(5L);
            verify(loadFavoritesPort).countByUser(userId);
        }

        @Test
        @DisplayName("userId가 null이면 IllegalArgumentException을 던진다")
        void givenNullUserId_whenCountByUser_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> favoriteQueryService.countByUser(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("userId must not be null");
            verify(loadFavoritesPort, never()).countByUser(any());
        }

        @Test
        @DisplayName("loadFavoritesPort에서 예외 발생 시 FavoriteException을 던진다")
        void givenPortThrows_whenCountByUser_thenThrowsFavoriteException() {
            // Given
            Long userId = 1L;
            when(loadFavoritesPort.countByUser(userId)).thenThrow(new RuntimeException("DB error"));
            // When & Then
            assertThatThrownBy(() -> favoriteQueryService.countByUser(userId))
                .isInstanceOf(FavoriteException.class)
                .hasMessageContaining("Failed to count favorites");
        }
    }

    @Nested
    @DisplayName("getTotalFavoriteCount() 메서드는")
    class GetTotalFavoriteCountTest {
        @Test
        @DisplayName("정상적으로 전체 즐겨찾기 개수를 반환한다")
        void givenNoCondition_whenGetTotalFavoriteCount_thenReturnsTotalCount() {
            // Given
            when(loadFavoritesPort.countTotal()).thenReturn(100L);

            // When
            long result = favoriteQueryService.getTotalFavoriteCount();

            // Then
            assertThat(result).isEqualTo(100L);
            verify(loadFavoritesPort).countTotal();
        }

        @Test
        @DisplayName("loadFavoritesPort에서 예외 발생 시 FavoriteException을 던진다")
        void givenPortThrows_whenGetTotalFavoriteCount_thenThrowsFavoriteException() {
            // Given
            when(loadFavoritesPort.countTotal()).thenThrow(new RuntimeException("DB error"));

            // When & Then
            assertThatThrownBy(() -> favoriteQueryService.getTotalFavoriteCount())
                .isInstanceOf(FavoriteException.class)
                .hasMessageContaining("Failed to count total favorites");
        }
    }

    @Nested
    @DisplayName("searchFavorites(FavoriteSearchCondition) 메서드는")
    class SearchFavoritesTest {

        private FavoriteSearchCondition createSearchCondition(Long userId, Pageable pageable) {
            return FavoriteSearchCondition.builder()
                .userId(userId)
                .pageable(pageable)
                .build();
        }

        private FavoritePromptResult createFavoritePromptResult(Long promptId, int favoriteCount) {
            return FavoritePromptResult.builder()
                .favoriteId(1L)
                .promptId(promptId)
                .promptUuid(UUID.randomUUID())
                .title("Test Prompt")
                .description("Test Description")
                .tags(Collections.emptyList())
                .createdById(1L)
                .createdByName("Test User")
                .categoryId(1L)
                .categoryName("Test Category")
                .visibility(Visibility.PUBLIC)
                .status(PromptStatus.PUBLISHED)
                .promptCreatedAt(LocalDateTime.now())
                .promptUpdatedAt(LocalDateTime.now())
                .favoriteCreatedAt(LocalDateTime.now())
                .stats(new PromptStats(0, favoriteCount))
                .isLiked(false)
                .build();
        }

        @Test
        @DisplayName("조회수 정보가 통합된 즐겨찾기 목록을 정상적으로 반환한다")
        void givenValidCondition_whenSearchFavorites_thenReturnsPageWithViewCounts() {
            // Given
            Long userId = 1L;
            Pageable pageable = PageRequest.of(0, 10);
            FavoriteSearchCondition condition = createSearchCondition(userId, pageable);

            // 기본 즐겨찾기 검색 결과 (조회수 0)
            FavoritePromptResult result1 = createFavoritePromptResult(101L, 5);
            FavoritePromptResult result2 = createFavoritePromptResult(102L, 3);
            List<FavoritePromptResult> resultList = Arrays.asList(result1, result2);
            Page<FavoritePromptResult> mockPage = new PageImpl<>(resultList, pageable, 2);

            // ViewQueryService에서 반환할 조회수 정보
            Map<Long, Long> viewCountMap = Map.of(
                101L, 150L,
                102L, 200L);

            when(searchFavoritePort.searchFavorites(condition)).thenReturn(mockPage);
            when(viewQueryService.getViewCountsByPromptIds(Arrays.asList(101L, 102L))).thenReturn(viewCountMap);

            // When
            Page<FavoritePromptResult> result = favoriteQueryService.searchFavorites(condition);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);

            // 첫 번째 결과 검증
            FavoritePromptResult firstResult = result.getContent().get(0);
            assertThat(firstResult.getPromptId()).isEqualTo(101L);
            assertThat(firstResult.getStats().getViewCount()).isEqualTo(150);
            assertThat(firstResult.getStats().getFavoriteCount()).isEqualTo(5);

            // 두 번째 결과 검증
            FavoritePromptResult secondResult = result.getContent().get(1);
            assertThat(secondResult.getPromptId()).isEqualTo(102L);
            assertThat(secondResult.getStats().getViewCount()).isEqualTo(200);
            assertThat(secondResult.getStats().getFavoriteCount()).isEqualTo(3);

            verify(searchFavoritePort).searchFavorites(condition);
            verify(viewQueryService).getViewCountsByPromptIds(Arrays.asList(101L, 102L));
        }

        @Test
        @DisplayName("빈 결과일 때 ViewQueryService를 호출하지 않고 빈 페이지를 반환한다")
        void givenValidConditionWithEmptyResult_whenSearchFavorites_thenReturnsEmptyPageWithoutViewServiceCall() {
            // Given
            Long userId = 1L;
            Pageable pageable = PageRequest.of(0, 10);
            FavoriteSearchCondition condition = createSearchCondition(userId, pageable);

            Page<FavoritePromptResult> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            when(searchFavoritePort.searchFavorites(condition)).thenReturn(emptyPage);

            // When
            Page<FavoritePromptResult> result = favoriteQueryService.searchFavorites(condition);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);

            verify(searchFavoritePort).searchFavorites(condition);
            verify(viewQueryService, never()).getViewCountsByPromptIds(anyList());
        }

        @Test
        @DisplayName("조회수 정보가 없는 프롬프트는 조회수 0으로 설정된다")
        void givenPromptWithoutViewCount_whenSearchFavorites_thenSetsViewCountToZero() {
            // Given
            Long userId = 1L;
            Pageable pageable = PageRequest.of(0, 10);
            FavoriteSearchCondition condition = createSearchCondition(userId, pageable);

            FavoritePromptResult result1 = createFavoritePromptResult(101L, 5);
            List<FavoritePromptResult> resultList = Collections.singletonList(result1);
            Page<FavoritePromptResult> mockPage = new PageImpl<>(resultList, pageable, 1);

            // ViewQueryService가 해당 프롬프트의 조회수 정보가 없는 빈 맵을 반환
            Map<Long, Long> emptyViewCountMap = Collections.emptyMap();

            when(searchFavoritePort.searchFavorites(condition)).thenReturn(mockPage);
            when(viewQueryService.getViewCountsByPromptIds(List.of(101L))).thenReturn(emptyViewCountMap);

            // When
            Page<FavoritePromptResult> result = favoriteQueryService.searchFavorites(condition);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);

            FavoritePromptResult firstResult = result.getContent().get(0);
            assertThat(firstResult.getPromptId()).isEqualTo(101L);
            assertThat(firstResult.getStats().getViewCount()).isEqualTo(0); // 기본값 0
            assertThat(firstResult.getStats().getFavoriteCount()).isEqualTo(5);

            verify(searchFavoritePort).searchFavorites(condition);
            verify(viewQueryService).getViewCountsByPromptIds(List.of(101L));
        }

        @Test
        @DisplayName("condition이 null이면 IllegalArgumentException을 던진다")
        void givenNullCondition_whenSearchFavorites_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> favoriteQueryService.searchFavorites(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("FavoriteSearchCondition must not be null");
            verify(searchFavoritePort, never()).searchFavorites(any());
            verify(viewQueryService, never()).getViewCountsByPromptIds(anyList());
        }

        @Test
        @DisplayName("searchFavoritePort에서 예외 발생 시 FavoriteException을 던진다")
        void givenPortThrows_whenSearchFavorites_thenThrowsFavoriteException() {
            // Given
            Long userId = 1L;
            Pageable pageable = PageRequest.of(0, 10);
            FavoriteSearchCondition condition = createSearchCondition(userId, pageable);

            when(searchFavoritePort.searchFavorites(condition)).thenThrow(new RuntimeException("DB error"));

            // When & Then
            assertThatThrownBy(() -> favoriteQueryService.searchFavorites(condition))
                .isInstanceOf(FavoriteException.class)
                .hasMessageContaining("Failed to search favorites");

            verify(viewQueryService, never()).getViewCountsByPromptIds(anyList());
        }

        @Test
        @DisplayName("ViewQueryService에서 예외 발생 시 FavoriteException을 던진다")
        void givenViewServiceThrows_whenSearchFavorites_thenThrowsFavoriteException() {
            // Given
            Long userId = 1L;
            Pageable pageable = PageRequest.of(0, 10);
            FavoriteSearchCondition condition = createSearchCondition(userId, pageable);

            FavoritePromptResult result1 = createFavoritePromptResult(101L, 5);
            List<FavoritePromptResult> resultList = Collections.singletonList(result1);
            Page<FavoritePromptResult> mockPage = new PageImpl<>(resultList, pageable, 1);

            when(searchFavoritePort.searchFavorites(condition)).thenReturn(mockPage);
            when(viewQueryService.getViewCountsByPromptIds(List.of(101L)))
                .thenThrow(new RuntimeException("ViewService error"));

            // When & Then
            assertThatThrownBy(() -> favoriteQueryService.searchFavorites(condition))
                .isInstanceOf(FavoriteException.class)
                .hasMessageContaining("Failed to search favorites");
        }
    }
}
