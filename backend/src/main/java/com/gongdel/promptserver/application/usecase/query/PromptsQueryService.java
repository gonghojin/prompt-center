package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.exception.PromptVersionOperationFailedException;
import com.gongdel.promptserver.application.port.in.PromptsQueryUseCase;
import com.gongdel.promptserver.application.port.in.query.LoadPromptDetailQuery;
import com.gongdel.promptserver.application.port.in.query.view.GetViewCountQuery;
import com.gongdel.promptserver.application.port.out.query.LoadPromptPort;
import com.gongdel.promptserver.application.port.out.query.SearchPromptsPort;
import com.gongdel.promptserver.domain.model.PromptDetail;
import com.gongdel.promptserver.domain.model.PromptSearchCondition;
import com.gongdel.promptserver.domain.model.PromptSearchResult;
import com.gongdel.promptserver.domain.model.PromptStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 프롬프트 목록 및 상세 조회, 검색 등 다양한 조건으로 프롬프트를 조회하는 유스케이스 서비스 구현체입니다.
 * 조회수 정보를 통합하여 완전한 프롬프트 정보를 제공합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromptsQueryService implements PromptsQueryUseCase {

    private final LoadPromptPort loadPromptPort;
    private final SearchPromptsPort searchPromptsPort;
    private final ViewQueryService viewQueryService;

    /**
     * 주어진 UUID로 프롬프트 상세 정보를 조회합니다.
     * 조회수 정보를 포함한 완전한 상세 정보를 반환합니다.
     *
     * @param query 프롬프트 상세 조회 쿼리 객체
     * @return 프롬프트 상세 정보(Optional), 존재하지 않을 경우 Optional.empty()
     * @throws IllegalArgumentException 쿼리 객체가 null인 경우 발생
     */
    @Override
    public Optional<PromptDetail> loadPromptDetailByUuid(LoadPromptDetailQuery query) {
        Assert.notNull(query, "LoadPromptDetailQuery must not be null");
        log.debug("Loading prompt detail with view count by UUID: {}", query.getPromptUuid());

        Optional<PromptDetail> detail = loadPromptPort.loadPromptDetailBy(query);
        if (detail.isEmpty()) {
            log.info("No prompt detail found for UUID: {}", query.getPromptUuid());
            return Optional.empty();
        }

        PromptDetail promptDetail = detail.get();

        // 조회수 정보 통합 - 불변 객체이므로 새로운 인스턴스 생성
        long viewCount = loadViewCountForPrompt(promptDetail.getId());
        PromptDetail enrichedDetail = createPromptDetailWithViewCount(promptDetail, (int) viewCount);

        log.debug("Successfully loaded prompt detail with view count for UUID: {}, viewCount: {}",
            query.getPromptUuid(), viewCount);
        return Optional.of(enrichedDetail);
    }

    /**
     * 프롬프트 검색 결과에 조회수 정보를 통합하여 반환합니다.
     * 배치 조회를 통해 성능을 최적화합니다.
     *
     * @param condition 검색 조건
     * @return 조회수가 포함된 검색 결과 페이지
     */
    @Override
    public Page<PromptSearchResult> searchPrompts(PromptSearchCondition condition) {
        Assert.notNull(condition, "PromptSearchCondition must not be null");
        log.debug("Searching prompts with view counts, condition: {}", condition);

        try {
            // 1. 기본 프롬프트 검색 수행
            Page<PromptSearchResult> searchResults = searchPromptsPort.searchPrompts(condition);

            if (searchResults.isEmpty()) {
                log.debug("No search results found for condition: {}", condition);
                return searchResults;
            }

            // 2. 프롬프트 ID 목록 추출
            List<Long> promptIds = searchResults.getContent().stream()
                .map(PromptSearchResult::getId)
                .collect(Collectors.toList());

            // 3. 배치로 조회수 정보 조회
            Map<Long, Long> viewCountMap = viewQueryService.getViewCountsByPromptIds(promptIds);

            // 4. 검색 결과에 조회수 정보 통합
            Page<PromptSearchResult> enrichedResults = searchResults
                .map(result -> createPromptSearchResultWithViewCount(result, viewCountMap));

            log.debug("Successfully enriched {} search results with view counts",
                enrichedResults.getContent().size());
            return enrichedResults;

        } catch (Exception e) {
            log.error("Unexpected error while searching prompts with view counts, condition: {}", condition, e);
            throw new PromptVersionOperationFailedException(
                "Error occurred during prompt search operation: " + e.getMessage(),
                e);
        }
    }

    /**
     * 개별 프롬프트의 조회수를 조회합니다.
     *
     * @param promptUuid 프롬프트 UUID
     * @return 조회수 (조회 실패 시 0)
     */
    private long loadViewCountForPrompt(java.util.UUID promptUuid) {
        try {
            GetViewCountQuery viewQuery = GetViewCountQuery.byUuid(promptUuid);
            return viewQueryService.getTotalViewCount(viewQuery);
        } catch (Exception e) {
            log.warn("Failed to load view count for prompt: {}, returning 0", promptUuid, e);
            return 0L;
        }
    }

    /**
     * PromptDetail에 조회수를 통합한 새로운 인스턴스를 생성합니다.
     *
     * @param original  원본 PromptDetail
     * @param viewCount 조회수
     * @return 조회수가 통합된 새로운 PromptDetail
     */
    private PromptDetail createPromptDetailWithViewCount(PromptDetail original, int viewCount) {
        return PromptDetail.builder()
            .id(original.getId())
            .title(original.getTitle())
            .description(original.getDescription())
            .content(original.getContent())
            .author(original.getAuthor())
            .tags(original.getTags())
            .isPublic(original.isPublic())
            .createdAt(original.getCreatedAt())
            .updatedAt(original.getUpdatedAt())
            .viewCount(viewCount)
            .favoriteCount(original.getFavoriteCount())
            .categoryId(original.getCategoryId())
            .visibility(original.getVisibility())
            .status(original.getStatus())
            .isFavorite(original.isFavorite())
            .isLiked(original.isLiked())
            .inputVariables(original.getInputVariables())
            .build();
    }

    /**
     * PromptSearchResult에 조회수를 통합한 새로운 인스턴스를 생성합니다.
     *
     * @param original     원본 PromptSearchResult
     * @param viewCountMap 조회수 맵
     * @return 조회수가 통합된 새로운 PromptSearchResult
     */
    private PromptSearchResult createPromptSearchResultWithViewCount(
        PromptSearchResult original, Map<Long, Long> viewCountMap) {

        long viewCount = viewCountMap.getOrDefault(original.getId(), 0L);

        // 기존 PromptStats에 조회수를 업데이트한 새로운 PromptStats 생성
        PromptStats updatedStats = new PromptStats((int) viewCount, original.getStats().getFavoriteCount());

        return PromptSearchResult.builder()
            .id(original.getId())
            .uuid(original.getUuid())
            .title(original.getTitle())
            .description(original.getDescription())
            .currentVersionId(original.getCurrentVersionId())
            .categoryId(original.getCategoryId())
            .categoryName(original.getCategoryName())
            .createdById(original.getCreatedById())
            .createdByName(original.getCreatedByName())
            .tags(original.getTags())
            .visibility(original.getVisibility())
            .status(original.getStatus())
            .createdAt(original.getCreatedAt())
            .updatedAt(original.getUpdatedAt())
            .stats(updatedStats)
            .isFavorite(original.isFavorite())
            .isLiked(original.isLiked())
            .build();
    }
}
