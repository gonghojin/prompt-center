package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.port.out.CategoryStatisticsQueryPort;
import com.gongdel.promptserver.domain.exception.PromptStatisticsException;
import com.gongdel.promptserver.domain.model.statistics.CategoryPromptCount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryStatisticsQueryService 테스트")
class CategoryStatisticsQueryServiceTest {

    @Mock
    private CategoryStatisticsQueryPort categoryStatisticsQueryPort;

    @InjectMocks
    private CategoryStatisticsQueryService service;

    @Nested
    @DisplayName("getRootCategoryPromptCounts() 메서드는")
    class GetRootCategoryPromptCounts {
        @Test
        @DisplayName("정상적으로 루트 카테고리별 프롬프트 개수 목록을 반환한다")
        void givenValidRequest_whenGetRootCategoryPromptCounts_thenReturnsList() {
            // Given
            List<CategoryPromptCount> expected = Arrays.asList(mock(CategoryPromptCount.class),
                mock(CategoryPromptCount.class));
            when(categoryStatisticsQueryPort.getRootCategoryPromptCounts()).thenReturn(expected);

            // When
            List<CategoryPromptCount> result = service.getRootCategoryPromptCounts();

            // Then
            assertThat(result).isEqualTo(expected);
            verify(categoryStatisticsQueryPort).getRootCategoryPromptCounts();
        }

        @Test
        @DisplayName("포트에서 예외 발생 시 PromptStatisticsException을 던진다")
        void givenPortThrowsException_whenGetRootCategoryPromptCounts_thenThrowsPromptStatisticsException() {
            // Given
            when(categoryStatisticsQueryPort.getRootCategoryPromptCounts()).thenThrow(new RuntimeException("DB error"));

            // When & Then
            assertThatThrownBy(() -> service.getRootCategoryPromptCounts())
                .isInstanceOf(PromptStatisticsException.class)
                .hasMessageContaining("Failed to get root category prompt counts");
            verify(categoryStatisticsQueryPort).getRootCategoryPromptCounts();
        }
    }

    @Nested
    @DisplayName("getChildCategoryPromptCounts(Long rootId) 메서드는")
    class GetChildCategoryPromptCounts {
        private final Long rootId = 1L;

        @Test
        @DisplayName("정상적으로 하위 카테고리별 프롬프트 개수 목록을 반환한다")
        void givenValidRootId_whenGetChildCategoryPromptCounts_thenReturnsList() {
            // Given
            List<CategoryPromptCount> expected = Collections.singletonList(mock(CategoryPromptCount.class));
            when(categoryStatisticsQueryPort.getChildCategoryPromptCounts(rootId)).thenReturn(expected);

            // When
            List<CategoryPromptCount> result = service.getChildCategoryPromptCounts(rootId);

            // Then
            assertThat(result).isEqualTo(expected);
            verify(categoryStatisticsQueryPort).getChildCategoryPromptCounts(rootId);
        }

        @Test
        @DisplayName("rootId가 null이면 PromptStatisticsException 던진다")
        void givenNullRootId_whenGetChildCategoryPromptCounts_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> service.getChildCategoryPromptCounts(null))
                .isInstanceOf(PromptStatisticsException.class)
                .hasMessageContaining("Failed to get child category prompt counts");
            verify(categoryStatisticsQueryPort, never()).getChildCategoryPromptCounts(any());
        }

        @Test
        @DisplayName("포트에서 예외 발생 시 PromptStatisticsException을 던진다")
        void givenPortThrowsException_whenGetChildCategoryPromptCounts_thenThrowsPromptStatisticsException() {
            // Given
            when(categoryStatisticsQueryPort.getChildCategoryPromptCounts(rootId))
                .thenThrow(new RuntimeException("DB error"));

            // When & Then
            assertThatThrownBy(() -> service.getChildCategoryPromptCounts(rootId))
                .isInstanceOf(PromptStatisticsException.class)
                .hasMessageContaining("Failed to get child category prompt counts");
            verify(categoryStatisticsQueryPort).getChildCategoryPromptCounts(rootId);
        }
    }
}
