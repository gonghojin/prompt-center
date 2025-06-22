package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.repository.PromptTemplateJpaRepository;
import com.gongdel.promptserver.adapter.out.persistence.repository.PromptTemplateQueryRepository;
import com.gongdel.promptserver.domain.exception.PromptStatisticsException;
import com.gongdel.promptserver.domain.model.statistics.PromptStatisticsResult;
import com.gongdel.promptserver.domain.statistics.ComparisonPeriod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("PromptStatisticsQueryAdapter 테스트")
@ExtendWith(MockitoExtension.class)
class PromptStatisticsQueryAdapterTest {

    @Mock
    private PromptTemplateJpaRepository promptTemplateJpaRepository;

    @Mock
    private PromptTemplateQueryRepository promptTemplateQueryRepository;

    @InjectMocks
    private PromptStatisticsQueryAdapter adapter;

    @Nested
    @DisplayName("loadTotalPromptCount() 메서드는")
    class LoadTotalPromptCount {
        @Test
        @DisplayName("정상적으로 전체 개수를 반환한다")
        void givenRepositoryReturnsCount_whenLoadTotalPromptCount_thenReturnsCount() {
            // Given
            long expected = 10L;
            when(promptTemplateJpaRepository.count()).thenReturn(expected);

            // When
            long result = adapter.loadTotalPromptCount();

            // Then
            assertThat(result).isEqualTo(expected);
            verify(promptTemplateJpaRepository).count();
        }

        @Test
        @DisplayName("DB 오류 발생 시 PromptStatisticsException.databaseError()를 던진다")
        void givenRepositoryThrowsException_whenLoadTotalPromptCount_thenThrowsPromptStatisticsException() {
            // Given
            RuntimeException cause = new RuntimeException("DB error");
            when(promptTemplateJpaRepository.count()).thenThrow(cause);

            // When & Then
            assertThatThrownBy(() -> adapter.loadTotalPromptCount())
                .isInstanceOf(PromptStatisticsException.class)
                .hasMessageContaining("프롬프트 통계 DB 접근 중 오류가 발생했습니다.");
            verify(promptTemplateJpaRepository).count();
        }
    }

    @Nested
    @DisplayName("loadPromptCountByPeriod(ComparisonPeriod) 메서드는")
    class LoadPromptCountByPeriod {
        private LocalDateTime start;
        private LocalDateTime end;
        private ComparisonPeriod period;

        @BeforeEach
        void setUp() {
            start = LocalDateTime.of(2024, 1, 1, 0, 0);
            end = LocalDateTime.of(2024, 1, 31, 23, 59);
            period = new ComparisonPeriod(start, end);
        }

        @Test
        @DisplayName("정상적으로 기간 내 개수를 반환한다")
        void givenValidPeriod_whenLoadPromptCountByPeriod_thenReturnsCount() {
            // Given
            long expected = 5L;
            when(promptTemplateJpaRepository.countByCreatedAtBetween(start, end)).thenReturn(expected);

            // When
            long result = adapter.loadPromptCountByPeriod(period);

            // Then
            assertThat(result).isEqualTo(expected);
            verify(promptTemplateJpaRepository).countByCreatedAtBetween(start, end);
        }

        @Test
        @DisplayName("period가 null이면 PromptStatisticsException.invalidPeriod()를 던진다")
        void givenNullPeriod_whenLoadPromptCountByPeriod_thenThrowsPromptStatisticsException() {
            // When & Then
            assertThatThrownBy(() -> adapter.loadPromptCountByPeriod(null))
                .isInstanceOf(PromptStatisticsException.class)
                .hasMessageContaining("유효하지 않은 기간 파라미터입니다.");
            verify(promptTemplateJpaRepository, never()).countByCreatedAtBetween(any(), any());
        }

        @Test
        @DisplayName("DB 오류 발생 시 PromptStatisticsException.databaseError()를 던진다")
        void givenRepositoryThrowsException_whenLoadPromptCountByPeriod_thenThrowsPromptStatisticsException() {
            // Given
            RuntimeException cause = new RuntimeException("DB error");
            when(promptTemplateJpaRepository.countByCreatedAtBetween(start, end)).thenThrow(cause);

            // When & Then
            assertThatThrownBy(() -> adapter.loadPromptCountByPeriod(period))
                .isInstanceOf(PromptStatisticsException.class)
                .hasMessageContaining("프롬프트 통계 DB 접근 중 오류가 발생했습니다.");
            verify(promptTemplateJpaRepository).countByCreatedAtBetween(start, end);
        }
    }

    @Nested
    @DisplayName("loadPromptStatisticsByUserId(Long) 메서드는")
    class LoadPromptStatisticsByUserIdTest {
        private Long userId;
        private PromptStatisticsResult mockResult;

        @BeforeEach
        void setUp() {
            userId = 1L;
            mockResult = mock(PromptStatisticsResult.class);
        }

        @Test
        @DisplayName("정상적으로 통계 결과를 반환한다")
        void givenValidUserId_whenLoadPromptStatisticsByUserId_thenReturnsResult() {
            // Given
            when(promptTemplateQueryRepository.loadPromptStatisticsByUserId(userId)).thenReturn(mockResult);

            // When
            PromptStatisticsResult result = adapter.loadPromptStatisticsByUserId(userId);

            // Then
            assertThat(result).isEqualTo(mockResult);
            verify(promptTemplateQueryRepository).loadPromptStatisticsByUserId(userId);
        }

        @Test
        @DisplayName("userId가 null이면 PromptStatisticsException.invalidPeriod()를 던진다")
        void givenNullUserId_whenLoadPromptStatisticsByUserId_thenThrowsPromptStatisticsException() {
            // When & Then
            assertThatThrownBy(() -> adapter.loadPromptStatisticsByUserId(null))
                .isInstanceOf(PromptStatisticsException.class)
                .hasMessageContaining(PromptStatisticsException.invalidPeriod().getMessage());
        }

        @Test
        @DisplayName("repository에서 예외 발생 시 PromptStatisticsException.databaseError()를 던진다")
        void givenRepositoryThrowsException_whenLoadPromptStatisticsByUserId_thenThrowsPromptStatisticsException() {
            // Given
            when(promptTemplateQueryRepository.loadPromptStatisticsByUserId(userId))
                .thenThrow(new RuntimeException("DB error"));

            // When & Then
            assertThatThrownBy(() -> adapter.loadPromptStatisticsByUserId(userId))
                .isInstanceOf(PromptStatisticsException.class)
                .hasMessageContaining(PromptStatisticsException.databaseError(null).getMessage());
        }
    }
}
