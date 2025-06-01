package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.exception.PromptVersionOperationFailedException;
import com.gongdel.promptserver.application.port.out.query.LoadPromptPort;
import com.gongdel.promptserver.application.port.out.query.SearchPromptsPort;
import com.gongdel.promptserver.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PromptsQueryServiceTest {

    @Mock
    LoadPromptPort loadPromptPort;
    @Mock
    SearchPromptsPort searchPromptsPort;

    @InjectMocks
    PromptsQueryService promptsQueryService;

    UUID promptId;
    PromptDetail promptDetail;
    PromptSearchCondition searchCondition;
    PromptSearchResult searchResult;
    Pageable pageable;

    @BeforeEach
    void setUp() {
        promptId = UUID.randomUUID();
        promptDetail = PromptDetail.builder()
                .id(promptId)
                .title("테스트 프롬프트")
                .build();
        searchCondition = PromptSearchCondition.builder()
                .title("테스트")
                .build();
        searchResult = PromptSearchResult.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .title("테스트 프롬프트")
                .description("설명")
                .currentVersionId(1L)
                .categoryId(1L)
                .categoryName("카테고리")
                .createdById(1L)
                .createdByName("홍길동")
                .tags(Collections.singletonList("태그"))
                .visibility(Visibility.PUBLIC)
                .status(PromptStatus.PUBLISHED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        pageable = PageRequest.of(0, 20);
    }

    @Nested
    @DisplayName("loadPromptDetailByUuid")
    class LoadPromptDetailByUuid {
        @Test
        @DisplayName("존재하는 프롬프트 ID로 조회 시 Optional 반환")
        void loadPromptDetail_success() {
            // given
            given(loadPromptPort.loadPromptDetailByUuid(promptId)).willReturn(Optional.of(promptDetail));
            // when
            Optional<PromptDetail> result = promptsQueryService.loadPromptDetailByUuid(promptId);
            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(promptId);
        }

        @Test
        @DisplayName("존재하지 않는 프롬프트 ID로 조회 시 Optional.empty 반환")
        void loadPromptDetail_notFound() {
            // given
            given(loadPromptPort.loadPromptDetailByUuid(promptId)).willReturn(Optional.empty());
            // when
            Optional<PromptDetail> result = promptsQueryService.loadPromptDetailByUuid(promptId);
            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("searchPrompts")
    class SearchPrompts {
        @Test
        @DisplayName("정상적으로 검색 결과 반환")
        void searchPrompts_success() {
            // given
            Page<PromptSearchResult> page = new PageImpl<>(Collections.singletonList(searchResult), pageable, 1);
            given(searchPromptsPort.searchPrompts(any())).willReturn(page);
            // when
            Page<PromptSearchResult> result = promptsQueryService.searchPrompts(searchCondition);
            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 프롬프트");
        }

        @Test
        @DisplayName("검색 중 예외 발생 시 PromptVersionOperationFailedException 변환")
        void searchPrompts_exception() {
            // given
            given(searchPromptsPort.searchPrompts(any())).willThrow(new RuntimeException("DB error"));
            // when & then
            assertThatThrownBy(() -> promptsQueryService.searchPrompts(searchCondition))
                    .isInstanceOf(PromptVersionOperationFailedException.class)
                    .hasMessageContaining("Error occurred during prompt search operation");
        }
    }
}
