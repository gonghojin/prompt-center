package com.gongdel.promptserver.adapter.in.rest.controller.dashboard;

import com.gongdel.promptserver.adapter.in.rest.BaseControllerTest;
import com.gongdel.promptserver.application.port.in.PromptStatisticsQueryUseCase;
import com.gongdel.promptserver.domain.statistics.ComparisonPeriod;
import com.gongdel.promptserver.domain.statistics.ComparisonResult;
import com.gongdel.promptserver.domain.statistics.PromptStatistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PromptStatisticsQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
class PromptStatisticsQueryControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PromptStatisticsQueryUseCase promptStatisticsQueryUseCase;

    @Nested
    @DisplayName("프롬프트 통계 조회 API")
    class GetPromptStatistics {

        private final LocalDateTime startDate = LocalDateTime.of(2024, 6, 1, 0, 0);
        private final LocalDateTime endDate = LocalDateTime.of(2024, 6, 2, 0, 0);

        @Test
        @DisplayName("정상 요청 시 프롬프트 통계 응답을 반환한다")
        void getPromptStatistics_success() throws Exception {
            // Given
            ComparisonPeriod period = new ComparisonPeriod(startDate, endDate);
            ComparisonResult result = ComparisonResult.of(100, 90); // current=100, previous=90
            PromptStatistics mockStats = PromptStatistics.builder()
                .totalCount(100)
                .comparisonPeriod(period)
                .comparisonResult(result)
                .build();
            when(promptStatisticsQueryUseCase.getPromptStatistics(any()))
                .thenReturn(mockStats);

            // When & Then
            mockMvc.perform(get("/api/v1/dashboard/prompt-statistics")
                    .param("startDate", startDate.toString())
                    .param("endDate", endDate.toString())
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(100))
                .andExpect(jsonPath("$.currentCount").value(100))
                .andExpect(jsonPath("$.previousCount").value(90));
        }

        @Test
        @DisplayName("startDate가 null이면 400 Bad Request를 반환한다")
        void getPromptStatistics_nullStartDate() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/dashboard/prompt-statistics")
                    .param("endDate", endDate.toString())
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("endDate가 null이면 400 Bad Request를 반환한다")
        void getPromptStatistics_nullEndDate() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/dashboard/prompt-statistics")
                    .param("startDate", startDate.toString())
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("endDate가 startDate보다 이전이면 400 Bad Request를 반환한다")
        void getPromptStatistics_invalidDateRange() throws Exception {
            // Given
            LocalDateTime invalidEndDate = startDate.minusDays(1);

            // When & Then
            mockMvc.perform(get("/api/v1/dashboard/prompt-statistics")
                    .param("startDate", startDate.toString())
                    .param("endDate", invalidEndDate.toString())
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        }
    }
}
