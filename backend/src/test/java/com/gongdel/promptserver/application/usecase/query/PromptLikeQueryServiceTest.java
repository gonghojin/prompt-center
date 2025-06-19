package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.port.out.like.query.*;
import com.gongdel.promptserver.domain.exception.BaseException;
import com.gongdel.promptserver.domain.exception.LikeOperationException;
import com.gongdel.promptserver.domain.like.LikeStatus;
import com.gongdel.promptserver.domain.like.LikedPromptResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PromptLikeQueryService 테스트")
class PromptLikeQueryServiceTest {
    @Mock
    private LoadPromptLikeStatusPort loadPromptLikeStatusPort;
    @Mock
    private LoadPromptLikeCountPort loadPromptLikeCountPort;
    @Mock
    private FindLikedPromptsPort findLikedPromptsPort;
    @InjectMocks
    private PromptLikeQueryService promptLikeQueryService;

    @Nested
    @DisplayName("getLikeStatus(LikeStatus) 메서드는")
    class GetLikeStatus {
        @Test
        @DisplayName("정상적으로 LikeStatus를 반환한다")
        void givenValidRequest_whenGetLikeStatus_thenReturnsLikeStatus() {
            // Given
            LoadPromptLikeStatus request = mock(LoadPromptLikeStatus.class);
            LikeStatus likeStatus = mock(LikeStatus.class);
            when(loadPromptLikeStatusPort.loadStatus(request)).thenReturn(likeStatus);
            // When
            LikeStatus result = promptLikeQueryService.getLikeStatus(request);
            // Then
            assertThat(result).isEqualTo(likeStatus);
            verify(loadPromptLikeStatusPort).loadStatus(request);
        }

        @Test
        @DisplayName("BaseException 발생 시 그대로 throw 한다")
        void givenBaseException_whenGetLikeStatus_thenThrowsBaseException() {
            // Given
            LoadPromptLikeStatus request = mock(LoadPromptLikeStatus.class);
            BaseException ex = mock(BaseException.class);
            when(loadPromptLikeStatusPort.loadStatus(request)).thenThrow(ex);
            // When & Then
            assertThatThrownBy(() -> promptLikeQueryService.getLikeStatus(request))
                .isSameAs(ex);
        }

        @Test
        @DisplayName("기타 Exception 발생 시 LikeOperationException을 throw 한다")
        void givenOtherException_whenGetLikeStatus_thenThrowsLikeOperationException() {
            // Given
            LoadPromptLikeStatus request = mock(LoadPromptLikeStatus.class);
            when(loadPromptLikeStatusPort.loadStatus(request)).thenThrow(new RuntimeException("error"));
            // When & Then
            assertThatThrownBy(() -> promptLikeQueryService.getLikeStatus(request))
                .isInstanceOf(LikeOperationException.class)
                .hasMessageContaining("Failed to query like status");
        }

        @Test
        @DisplayName("null 파라미터 전달 시 IllegalArgumentException을 throw 한다")
        void givenNullRequest_whenGetLikeStatus_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> promptLikeQueryService.getLikeStatus(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("LoadPromptLikeStatus must not be null");
        }
    }

    @Nested
    @DisplayName("getLikeCount(Long) 메서드는")
    class GetLikeCount {
        @Test
        @DisplayName("정상적으로 count를 반환한다")
        void givenValidId_whenGetLikeCount_thenReturnsCount() {
            // Given
            Long promptTemplateId = 1L;
            when(loadPromptLikeCountPort.loadLikeCount(promptTemplateId)).thenReturn(10L);
            // When
            long result = promptLikeQueryService.getLikeCount(promptTemplateId);
            // Then
            assertThat(result).isEqualTo(10L);
            verify(loadPromptLikeCountPort).loadLikeCount(promptTemplateId);
        }

        @Test
        @DisplayName("BaseException 발생 시 그대로 throw 한다")
        void givenBaseException_whenGetLikeCount_thenThrowsBaseException() {
            // Given
            Long promptTemplateId = 1L;
            BaseException ex = mock(BaseException.class);
            when(loadPromptLikeCountPort.loadLikeCount(promptTemplateId)).thenThrow(ex);
            // When & Then
            assertThatThrownBy(() -> promptLikeQueryService.getLikeCount(promptTemplateId))
                .isSameAs(ex);
        }

        @Test
        @DisplayName("기타 Exception 발생 시 LikeOperationException을 throw 한다")
        void givenOtherException_whenGetLikeCount_thenThrowsLikeOperationException() {
            // Given
            Long promptTemplateId = 1L;
            when(loadPromptLikeCountPort.loadLikeCount(promptTemplateId)).thenThrow(new RuntimeException("error"));
            // When & Then
            assertThatThrownBy(() -> promptLikeQueryService.getLikeCount(promptTemplateId))
                .isInstanceOf(LikeOperationException.class)
                .hasMessageContaining("Failed to query like count");
        }

        @Test
        @DisplayName("null 파라미터 전달 시 IllegalArgumentException을 throw 한다")
        void givenNullId_whenGetLikeCount_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> promptLikeQueryService.getLikeCount(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PromptTemplate ID must not be null");
        }
    }

    @Nested
    @DisplayName("getLikedPrompts(FindLikedPrompts) 메서드는")
    class GetLikedPrompts {
        @Test
        @DisplayName("정상적으로 Page<LikedPromptResult>를 반환한다")
        void givenValidRequest_whenGetLikedPrompts_thenReturnsPage() {
            // Given
            FindLikedPrompts request = mock(FindLikedPrompts.class);
            when(request.getUserId()).thenReturn(1L);
            when(request.getPageable()).thenReturn(Pageable.unpaged());
            Page<LikedPromptResult> page = new PageImpl<>(Collections.emptyList());
            when(findLikedPromptsPort.findLikedPrompts(request)).thenReturn(page);
            // When
            Page<LikedPromptResult> result = promptLikeQueryService.getLikedPrompts(request);
            // Then
            assertThat(result).isSameAs(page);
            verify(findLikedPromptsPort).findLikedPrompts(request);
        }

        @Test
        @DisplayName("BaseException 발생 시 그대로 throw 한다")
        void givenBaseException_whenGetLikedPrompts_thenThrowsBaseException() {
            // Given
            FindLikedPrompts request = mock(FindLikedPrompts.class);
            when(request.getUserId()).thenReturn(1L);
            BaseException ex = mock(BaseException.class);
            when(findLikedPromptsPort.findLikedPrompts(request)).thenThrow(ex);
            // When & Then
            assertThatThrownBy(() -> promptLikeQueryService.getLikedPrompts(request))
                .isSameAs(ex);
        }

        @Test
        @DisplayName("기타 Exception 발생 시 LikeOperationException을 throw 한다")
        void givenOtherException_whenGetLikedPrompts_thenThrowsLikeOperationException() {
            // Given
            FindLikedPrompts request = mock(FindLikedPrompts.class);
            when(request.getUserId()).thenReturn(1L);
            when(findLikedPromptsPort.findLikedPrompts(request)).thenThrow(new RuntimeException("error"));
            // When & Then
            assertThatThrownBy(() -> promptLikeQueryService.getLikedPrompts(request))
                .isInstanceOf(LikeOperationException.class)
                .hasMessageContaining("Failed to query liked prompts");
        }

        @Test
        @DisplayName("null 파라미터 전달 시 IllegalArgumentException을 throw 한다")
        void givenNullRequest_whenGetLikedPrompts_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> promptLikeQueryService.getLikedPrompts(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("FindLikedPrompts must not be null");
        }

        @Test
        @DisplayName("userId가 null일 때 IllegalArgumentException을 throw 한다")
        void givenNullUserId_whenGetLikedPrompts_thenThrowsIllegalArgumentException() {
            // Given
            FindLikedPrompts request = mock(FindLikedPrompts.class);
            when(request.getUserId()).thenReturn(null);
            // When & Then
            assertThatThrownBy(() -> promptLikeQueryService.getLikedPrompts(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID must not be null");
        }
    }
}
