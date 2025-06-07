package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.exception.StatisticsOperationFailedException;
import com.gongdel.promptserver.application.port.out.query.LoadPromptStatisticsPort;
import com.gongdel.promptserver.application.port.out.query.SearchPromptsPort;
import com.gongdel.promptserver.domain.exception.PromptOperationException;
import com.gongdel.promptserver.domain.model.PromptSearchResult;
import com.gongdel.promptserver.domain.model.my.MyPromptSearchCondition;
import com.gongdel.promptserver.domain.model.statistics.PromptStatisticsResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("MyPromptsQueryService 테스트")
class MyPromptsQueryServiceTest {
    @Mock
    private SearchPromptsPort searchPromptsPort;
    @Mock
    private LoadPromptStatisticsPort loadPromptStatisticsPort;
    @InjectMocks
    private MyPromptsQueryService myPromptsQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("findMyPrompts 메서드는")
    class FindMyPromptsTest {
        private MyPromptSearchCondition condition;
        private Page<PromptSearchResult> mockPage;
        private Long userId = 1L;

        @BeforeEach
        void setUp() {
            condition = mock(MyPromptSearchCondition.class);
            when(condition.getUserId()).thenReturn(userId);
            when(condition.getStatusFilters()).thenReturn(null);
            when(condition.getVisibilityFilters()).thenReturn(null);
            when(condition.getSearchKeyword()).thenReturn(null);
            when(condition.getSortType()).thenReturn(null);
            when(condition.getPageable()).thenReturn(Pageable.unpaged());
            mockPage = new PageImpl<>(Collections.emptyList());
        }

        @Test
        @DisplayName("정상적으로 내 프롬프트 목록을 조회한다")
        void givenValidCondition_whenFindMyPrompts_thenReturnsPage() {
            // Given
            when(searchPromptsPort.searchPrompts(any())).thenReturn(mockPage);
            // When
            Page<PromptSearchResult> result = myPromptsQueryService.findMyPrompts(condition);
            // Then
            assertThat(result).isEqualTo(mockPage);
            verify(searchPromptsPort).searchPrompts(any());
        }

        @Test
        @DisplayName("검색 조건이 null이면 IllegalArgumentException을 던진다")
        void givenNullCondition_whenFindMyPrompts_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> myPromptsQueryService.findMyPrompts(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("MyPromptSearchCondition must not be null");
        }

        @Test
        @DisplayName("userId가 null이면 IllegalArgumentException을 던진다")
        void givenNullUserId_whenFindMyPrompts_thenThrowsIllegalArgumentException() {
            // Given
            when(condition.getUserId()).thenReturn(null);
            // When & Then
            assertThatThrownBy(() -> myPromptsQueryService.findMyPrompts(condition))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("userId must not be null");
        }

        @Test
        @DisplayName("searchPromptsPort에서 예외 발생 시 PromptOperationException을 던진다")
        void givenPortThrowsException_whenFindMyPrompts_thenThrowsPromptOperationException() {
            // Given
            when(searchPromptsPort.searchPrompts(any())).thenThrow(new RuntimeException("DB error"));
            // When & Then
            assertThatThrownBy(() -> myPromptsQueryService.findMyPrompts(condition))
                .isInstanceOf(PromptOperationException.class)
                .hasMessageContaining("Failed to search my prompts");
        }
    }

    @Nested
    @DisplayName("getMyPromptStatistics 메서드는")
    class GetMyPromptStatisticsTest {
        private Long userId = 1L;
        private PromptStatisticsResult mockResult;

        @BeforeEach
        void setUp() {
            mockResult = mock(PromptStatisticsResult.class);
        }

        @Test
        @DisplayName("정상적으로 내 프롬프트 통계를 조회한다")
        void givenValidUserId_whenGetMyPromptStatistics_thenReturnsResult() {
            // Given
            when(loadPromptStatisticsPort.loadPromptStatisticsByUserId(userId)).thenReturn(mockResult);
            // When
            PromptStatisticsResult result = myPromptsQueryService.getMyPromptStatistics(userId);
            // Then
            assertThat(result).isEqualTo(mockResult);
            verify(loadPromptStatisticsPort).loadPromptStatisticsByUserId(userId);
        }

        @Test
        @DisplayName("userId가 null이면 IllegalArgumentException을 던진다")
        void givenNullUserId_whenGetMyPromptStatistics_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> myPromptsQueryService.getMyPromptStatistics(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("userId must not be null");
        }

        @Test
        @DisplayName("loadPromptStatisticsPort에서 예외 발생 시 StatisticsOperationFailedException을 던진다")
        void givenPortThrowsException_whenGetMyPromptStatistics_thenThrowsStatisticsOperationFailedException() {
            // Given
            when(loadPromptStatisticsPort.loadPromptStatisticsByUserId(userId))
                .thenThrow(new RuntimeException("DB error"));
            // When & Then
            assertThatThrownBy(() -> myPromptsQueryService.getMyPromptStatistics(userId))
                .isInstanceOf(StatisticsOperationFailedException.class)
                .hasMessageContaining("Failed to load my prompt statistics");
        }
    }
}
