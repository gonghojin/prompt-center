package com.gongdel.promptserver.application.usecase.command;

import com.gongdel.promptserver.application.port.out.command.DeleteFavoritePort;
import com.gongdel.promptserver.application.port.out.command.SaveFavoritePort;
import com.gongdel.promptserver.application.port.out.query.LoadPromptTemplateIdPort;
import com.gongdel.promptserver.domain.exception.FavoriteException;
import com.gongdel.promptserver.domain.model.favorite.Favorite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FavoriteCommandService 테스트")
class FavoriteCommandServiceTest {

    @Mock
    private SaveFavoritePort saveFavoritePort;
    @Mock
    private DeleteFavoritePort deleteFavoritePort;
    @Mock
    private LoadPromptTemplateIdPort loadPromptTemplateIdPort;

    @InjectMocks
    private FavoriteCommandService favoriteCommandService;

    private Long userId;
    private UUID promptTemplateUuid;
    private Long promptId;
    private Favorite favorite;

    @BeforeEach
    void setUp() {
        userId = 1L;
        promptTemplateUuid = UUID.randomUUID();
        promptId = 100L;
        favorite = Favorite.builder()
            .userId(userId)
            .promptTemplateId(promptId)
            .createdAt(LocalDateTime.now())
            .build();
    }

    @Nested
    @DisplayName("addFavorite(Long, UUID) 메서드는")
    class AddFavoriteTest {
        @Test
        @DisplayName("정상적으로 즐겨찾기를 추가한다")
        void givenValidInput_whenAddFavorite_thenReturnsFavorite() {
            // Given
            given(loadPromptTemplateIdPort.findIdByUuid(promptTemplateUuid)).willReturn(Optional.of(promptId));
            given(saveFavoritePort.save(any(Favorite.class))).willReturn(favorite);

            // When
            Favorite result = favoriteCommandService.addFavorite(userId, promptTemplateUuid);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(userId);
            assertThat(result.getPromptTemplateId()).isEqualTo(promptId);
            then(saveFavoritePort).should().save(any(Favorite.class));
        }

        @Test
        @DisplayName("프롬프트 UUID가 존재하지 않으면 FavoriteException.notFound를 던진다")
        void givenNonExistentPromptUuid_whenAddFavorite_thenThrowsFavoriteException() {
            // Given
            given(loadPromptTemplateIdPort.findIdByUuid(promptTemplateUuid)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> favoriteCommandService.addFavorite(userId, promptTemplateUuid))
                .isInstanceOf(FavoriteException.class)
                .hasMessageContaining(FavoriteException.notFound(promptTemplateUuid).getMessage());
        }

        @Test
        @DisplayName("userId가 null이면 IllegalArgumentException을 던진다")
        void givenNullUserId_whenAddFavorite_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> favoriteCommandService.addFavorite(null, promptTemplateUuid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("UserId must not be null");
        }

        @Test
        @DisplayName("promptTemplateUuid가 null이면 IllegalArgumentException을 던진다")
        void givenNullPromptTemplateUuid_whenAddFavorite_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> favoriteCommandService.addFavorite(userId, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PromptTemplateUuid must not be null");
        }

        @Test
        @DisplayName("저장 중 예외 발생 시 FavoriteException.internalError를 던진다")
        void givenSaveError_whenAddFavorite_thenThrowsFavoriteException() {
            // Given
            given(loadPromptTemplateIdPort.findIdByUuid(promptTemplateUuid)).willReturn(Optional.of(promptId));
            given(saveFavoritePort.save(any(Favorite.class))).willThrow(new RuntimeException("DB error"));

            // When & Then
            assertThatThrownBy(() -> favoriteCommandService.addFavorite(userId, promptTemplateUuid))
                .isInstanceOf(FavoriteException.class)
                .hasMessageContaining("Failed to add favorite");
        }
    }

    @Nested
    @DisplayName("removeFavorite(Long, UUID) 메서드는")
    class RemoveFavoriteTest {
        @Test
        @DisplayName("정상적으로 즐겨찾기를 삭제한다")
        void givenValidInput_whenRemoveFavorite_thenDeletesFavorite() {
            // Given
            given(loadPromptTemplateIdPort.findIdByUuid(promptTemplateUuid)).willReturn(Optional.of(promptId));
            when(deleteFavoritePort.deleteByUserIdAndPromptTemplateId(userId, promptId)).thenReturn(1L);

            // When & Then
            assertThatCode(() -> favoriteCommandService.removeFavorite(userId, promptTemplateUuid))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("프롬프트 UUID가 존재하지 않으면 FavoriteException.notFound를 던진다")
        void givenNonExistentPromptUuid_whenRemoveFavorite_thenThrowsFavoriteException() {
            // Given
            given(loadPromptTemplateIdPort.findIdByUuid(promptTemplateUuid)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> favoriteCommandService.removeFavorite(userId, promptTemplateUuid))
                .isInstanceOf(FavoriteException.class)
                .hasMessageContaining(FavoriteException.notFound(promptTemplateUuid).getMessage());
        }

        @Test
        @DisplayName("userId가 null이면 IllegalArgumentException을 던진다")
        void givenNullUserId_whenRemoveFavorite_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> favoriteCommandService.removeFavorite(null, promptTemplateUuid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("UserId must not be null");
        }

        @Test
        @DisplayName("promptTemplateUuid가 null이면 IllegalArgumentException을 던진다")
        void givenNullPromptTemplateUuid_whenRemoveFavorite_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> favoriteCommandService.removeFavorite(userId, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PromptTemplateUuid must not be null");
        }

        @Test
        @DisplayName("삭제 중 예외 발생 시 FavoriteException.internalError를 던진다")
        void givenDeleteError_whenRemoveFavorite_thenThrowsFavoriteException() {
            // Given
            given(loadPromptTemplateIdPort.findIdByUuid(promptTemplateUuid)).willReturn(Optional.of(promptId));
            willThrow(new RuntimeException("DB error")).given(deleteFavoritePort)
                .deleteByUserIdAndPromptTemplateId(userId, promptId);

            // When & Then
            assertThatThrownBy(() -> favoriteCommandService.removeFavorite(userId, promptTemplateUuid))
                .isInstanceOf(FavoriteException.class)
                .hasMessageContaining("Failed to remove favorite");
        }
    }
}
