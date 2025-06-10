package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.port.out.query.LoadFavoritesPort;
import com.gongdel.promptserver.application.port.out.query.SearchFavoritePort;
import com.gongdel.promptserver.domain.exception.FavoriteException;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("FavoriteQueryService 테스트")
class FavoriteQueryServiceTest {

    @Mock
    private LoadFavoritesPort loadFavoritesPort;
    @Mock
    private SearchFavoritePort searchFavoritePort;
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
    @DisplayName("searchFavorites(FavoriteSearchCondition) 메서드는")
    class SearchFavoritesTest {
        @Test
        @DisplayName("정상적으로 즐겨찾기 목록을 반환한다")
        void givenValidCondition_whenSearchFavorites_thenReturnsPage() {
            // Given
            Long userId = 1L;
            Pageable pageable = PageRequest.of(0, 10);
            FavoriteSearchCondition condition = FavoriteSearchCondition.builder()
                .userId(userId)
                .pageable(pageable)
                .build();
            Page<FavoritePromptResult> mockPage = mock(Page.class);
            when(searchFavoritePort.searchFavorites(condition)).thenReturn(mockPage);
            // When
            Page<FavoritePromptResult> result = favoriteQueryService.searchFavorites(condition);
            // Then
            assertThat(result).isEqualTo(mockPage);
            verify(searchFavoritePort).searchFavorites(condition);
        }

        @Test
        @DisplayName("condition이 null이면 IllegalArgumentException을 던진다")
        void givenNullCondition_whenSearchFavorites_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> favoriteQueryService.searchFavorites(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("FavoriteSearchCondition must not be null");
            verify(searchFavoritePort, never()).searchFavorites(any());
        }

        @Test
        @DisplayName("searchFavoritePort에서 예외 발생 시 FavoriteException을 던진다")
        void givenPortThrows_whenSearchFavorites_thenThrowsFavoriteException() {
            // Given
            Long userId = 1L;
            Pageable pageable = mock(Pageable.class);
            FavoriteSearchCondition condition = FavoriteSearchCondition.builder()
                .userId(userId)
                .pageable(pageable)
                .build();
            when(searchFavoritePort.searchFavorites(condition)).thenThrow(new RuntimeException("DB error"));
            // When & Then
            assertThatThrownBy(() -> favoriteQueryService.searchFavorites(condition))
                .isInstanceOf(FavoriteException.class)
                .hasMessageContaining("Failed to search favorites");
        }
    }
}
