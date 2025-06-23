package com.gongdel.promptserver.application.usecase.command;

import com.gongdel.promptserver.application.port.out.like.command.AddPromptLikePort;
import com.gongdel.promptserver.application.port.out.like.command.RemovePromptLikePort;
import com.gongdel.promptserver.application.port.out.like.command.UpdatePromptLikeCountPort;
import com.gongdel.promptserver.application.port.out.query.LoadPromptTemplateIdPort;
import com.gongdel.promptserver.domain.exception.LikeOperationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PromptLikeCommandService 테스트")
class PromptLikeCommandServiceTest {
    @Mock
    private AddPromptLikePort addPromptLikePort;
    @Mock
    private RemovePromptLikePort removePromptLikePort;
    @Mock
    private UpdatePromptLikeCountPort updatePromptLikeCountPort;
    @Mock
    private LoadPromptTemplateIdPort loadPromptTemplateIdPort;
    @InjectMocks
    private PromptLikeCommandService promptLikeCommandService;

    private Long userId;
    private UUID promptTemplateUuid;
    private Long promptId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        promptTemplateUuid = UUID.randomUUID();
        promptId = 100L;
    }

    @Nested
    @DisplayName("addLike(Long, UUID) 메서드는")
    class AddLikeTest {
        @Test
        @DisplayName("정상적으로 좋아요를 추가하고 count를 반환한다")
        void givenValidInput_whenAddLike_thenReturnsLikeCount() {
            // Given
            when(loadPromptTemplateIdPort.findIdByUuid(promptTemplateUuid)).thenReturn(Optional.of(promptId));
            when(updatePromptLikeCountPort.updateLikeCount(any())).thenReturn(5L);
            // When
            long result = promptLikeCommandService.addLike(userId, promptTemplateUuid);
            // Then
            assertThat(result).isEqualTo(5L);
            verify(addPromptLikePort).addLike(any());
            verify(updatePromptLikeCountPort).updateLikeCount(any());
        }

        @Test
        @DisplayName("userId가 null이면 IllegalArgumentException을 throw 한다")
        void givenNullUserId_whenAddLike_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> promptLikeCommandService.addLike(null, promptTemplateUuid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID must not be null");
        }

        @Test
        @DisplayName("promptTemplateUuid가 null이면 IllegalArgumentException을 throw 한다")
        void givenNullPromptTemplateUuid_whenAddLike_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> promptLikeCommandService.addLike(userId, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PromptTemplate ID must not be null");
        }

        @Test
        @DisplayName("프롬프트 UUID에 해당하는 ID가 없으면 LikeOperationException.notFound를 throw 한다")
        void givenNotFoundPromptId_whenAddLike_thenThrowsLikeOperationException() {
            // Given
            when(loadPromptTemplateIdPort.findIdByUuid(promptTemplateUuid)).thenReturn(Optional.empty());
            // When & Then
            assertThatThrownBy(() -> promptLikeCommandService.addLike(userId, promptTemplateUuid))
                .isInstanceOf(LikeOperationException.class)
                .hasMessageContaining("Failed to add like");
        }

        @Test
        @DisplayName("addLike 또는 updateLikeCount에서 예외 발생 시 LikeOperationException을 throw 한다")
        void givenException_whenAddLike_thenThrowsLikeOperationException() {
            // Given
            when(loadPromptTemplateIdPort.findIdByUuid(promptTemplateUuid)).thenReturn(Optional.of(promptId));
            doThrow(new RuntimeException("error")).when(addPromptLikePort).addLike(any());
            // When & Then
            assertThatThrownBy(() -> promptLikeCommandService.addLike(userId, promptTemplateUuid))
                .isInstanceOf(LikeOperationException.class)
                .hasMessageContaining("Failed to add like");
        }
    }

    @Nested
    @DisplayName("removeLike(Long, UUID) 메서드는")
    class RemoveLikeTest {
        @Test
        @DisplayName("정상적으로 좋아요를 취소하고 count를 반환한다")
        void givenValidInput_whenRemoveLike_thenReturnsLikeCount() {
            // Given
            when(loadPromptTemplateIdPort.findIdByUuid(promptTemplateUuid)).thenReturn(Optional.of(promptId));
            when(updatePromptLikeCountPort.updateLikeCount(any())).thenReturn(3L);
            // When
            long result = promptLikeCommandService.removeLike(userId, promptTemplateUuid);
            // Then
            assertThat(result).isEqualTo(3L);
            verify(removePromptLikePort).removeLike(any());
            verify(updatePromptLikeCountPort).updateLikeCount(any());
        }

        @Test
        @DisplayName("userId가 null이면 IllegalArgumentException을 throw 한다")
        void givenNullUserId_whenRemoveLike_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> promptLikeCommandService.removeLike(null, promptTemplateUuid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID must not be null");
        }

        @Test
        @DisplayName("promptTemplateUuid가 null이면 IllegalArgumentException을 throw 한다")
        void givenNullPromptTemplateUuid_whenRemoveLike_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> promptLikeCommandService.removeLike(userId, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PromptTemplate ID must not be null");
        }

        @Test
        @DisplayName("프롬프트 UUID에 해당하는 ID가 없으면 LikeOperationException.notFound를 throw 한다")
        void givenNotFoundPromptId_whenRemoveLike_thenThrowsLikeOperationException() {
            // Given
            when(loadPromptTemplateIdPort.findIdByUuid(promptTemplateUuid)).thenReturn(Optional.empty());
            // When & Then
            assertThatThrownBy(() -> promptLikeCommandService.removeLike(userId, promptTemplateUuid))
                .isInstanceOf(LikeOperationException.class)
                .hasMessageContaining("Failed to remove like");
        }

        @Test
        @DisplayName("removeLike 또는 updateLikeCount에서 예외 발생 시 LikeOperationException을 throw 한다")
        void givenException_whenRemoveLike_thenThrowsLikeOperationException() {
            // Given
            when(loadPromptTemplateIdPort.findIdByUuid(promptTemplateUuid)).thenReturn(Optional.of(promptId));
            doThrow(new RuntimeException("error")).when(removePromptLikePort).removeLike(any());
            // When & Then
            assertThatThrownBy(() -> promptLikeCommandService.removeLike(userId, promptTemplateUuid))
                .isInstanceOf(LikeOperationException.class)
                .hasMessageContaining("Failed to remove like");
        }
    }
}
