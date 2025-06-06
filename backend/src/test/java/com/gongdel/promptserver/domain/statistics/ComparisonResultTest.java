package com.gongdel.promptserver.domain.statistics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ComparisonResult 도메인 테스트")
class ComparisonResultTest {

    @Nested
    @DisplayName("생성자 및 팩토리 메서드 of()는")
    class ConstructorTest {

        @Test
        @DisplayName("정상적인 값으로 생성 시 필드가 올바르게 설정된다")
        void givenValidValues_whenCreate_thenFieldsSetCorrectly() {
            // Given
            long current = 10;
            long previous = 8;
            double expectedChange = 25.0;

            // When
            ComparisonResult result = new ComparisonResult(current, previous, expectedChange);

            // Then
            assertThat(result.getCurrentCount()).isEqualTo(current);
            assertThat(result.getPreviousCount()).isEqualTo(previous);
            assertThat(result.getPercentageChange()).isEqualTo(expectedChange);
        }

        @Test
        @DisplayName("currentCount가 음수면 예외를 던진다")
        void givenNegativeCurrentCount_whenCreate_thenThrowsException() {
            // When & Then
            assertThatThrownBy(() -> new ComparisonResult(-1, 5, 10.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("currentCount must be non-negative");
        }

        @Test
        @DisplayName("previousCount가 음수면 예외를 던진다")
        void givenNegativePreviousCount_whenCreate_thenThrowsException() {
            // When & Then
            assertThatThrownBy(() -> new ComparisonResult(5, -1, 10.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("previousCount must be non-negative");
        }

        @Test
        @DisplayName("percentageChange가 NaN이면 예외를 던진다")
        void givenNaNPercentageChange_whenCreate_thenThrowsException() {
            // When & Then
            assertThatThrownBy(() -> new ComparisonResult(5, 5, Double.NaN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("percentageChange must be a valid number");
        }
    }

    @Nested
    @DisplayName("of() 팩토리 메서드는")
    class FactoryMethodTest {
        @Test
        @DisplayName("previousCount가 0이고 currentCount가 0이면 변동률 0.0을 반환한다")
        void givenZeroPreviousAndCurrent_whenOf_thenZeroPercent() {
            // When
            ComparisonResult result = ComparisonResult.of(0, 0);
            // Then
            assertThat(result.getPercentageChange()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("previousCount가 0이고 currentCount가 1 이상이면 변동률 100.0을 반환한다")
        void givenZeroPreviousAndPositiveCurrent_whenOf_thenHundredPercent() {
            // When
            ComparisonResult result = ComparisonResult.of(5, 0);
            // Then
            assertThat(result.getPercentageChange()).isEqualTo(100.0);
        }

        @Test
        @DisplayName("previousCount가 10, currentCount가 15면 변동률 50.0을 반환한다")
        void givenIncrease_whenOf_thenPositivePercent() {
            // When
            ComparisonResult result = ComparisonResult.of(15, 10);
            // Then
            assertThat(result.getPercentageChange()).isEqualTo(50.0);
        }

        @Test
        @DisplayName("previousCount가 10, currentCount가 5면 변동률 -50.0을 반환한다")
        void givenDecrease_whenOf_thenNegativePercent() {
            // When
            ComparisonResult result = ComparisonResult.of(5, 10);
            // Then
            assertThat(result.getPercentageChange()).isEqualTo(-50.0);
        }
    }
}
