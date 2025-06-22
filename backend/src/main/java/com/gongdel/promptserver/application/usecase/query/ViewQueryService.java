package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.port.in.ViewQueryUseCase;
import com.gongdel.promptserver.application.port.in.query.view.GetViewCountQuery;
import com.gongdel.promptserver.application.port.out.CacheViewCountPort;
import com.gongdel.promptserver.application.port.out.LoadViewCountFromCachePort;
import com.gongdel.promptserver.application.port.out.LoadViewCountFromStoragePort;
import com.gongdel.promptserver.application.port.out.query.LoadPromptTemplateIdPort;
import com.gongdel.promptserver.domain.view.LoadViewCountQuery;
import com.gongdel.promptserver.domain.view.ViewCount;
import com.gongdel.promptserver.domain.view.ViewOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 조회수 조회 관련 쿼리 작업을 처리하는 서비스 구현체입니다.
 * 캐시 우선 조회 → DB 폴백 → 캐시 저장 패턴을 구현합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ViewQueryService implements ViewQueryUseCase {

    private final LoadViewCountFromCachePort loadViewCountFromCachePort;
    private final LoadViewCountFromStoragePort loadViewCountFromStoragePort;
    private final CacheViewCountPort cacheViewCountPort;
    private final LoadPromptTemplateIdPort loadPromptTemplateIdPort;

    /**
     * 프롬프트의 조회수 정보를 조회합니다.
     * 1. Redis 캐시에서 우선 조회
     * 2. 캐시에 없으면 DB에서 조회
     * 3. DB에서 조회한 데이터를 캐시에 저장
     *
     * @param query 조회수 조회 쿼리 객체
     * @return 조회수 정보 (없는 경우 빈 Optional 반환)
     */
    @Override
    public Optional<ViewCount> getViewCount(GetViewCountQuery query) {
        Assert.notNull(query, "GetViewCountQuery must not be null");

        log.debug("Loading view count with cache-first strategy: promptUuid={}, promptId={}",
            query.getPromptTemplateUuid(), query.getPromptTemplateId());

        try {
            // 1. 내부 ID 확보 (UUID 또는 ID 중 하나는 반드시 있음)
            Long promptTemplateId = resolvePromptTemplateId(query);
            LoadViewCountQuery domainQuery = LoadViewCountQuery.of(promptTemplateId);

            // 2. 캐시에서 우선 조회
            Optional<ViewCount> cachedResult = loadFromCache(domainQuery);
            if (cachedResult.isPresent()) {
                log.debug("View count found in cache for prompt: {}, count: {}",
                    promptTemplateId, cachedResult.get().getTotalViewCount());
                return cachedResult;
            }

            // 3. 캐시에 없으면 DB에서 조회
            log.debug("Cache miss, loading from storage for prompt: {}", promptTemplateId);
            Optional<ViewCount> storageResult = loadFromStorage(domainQuery);

            // 4. DB에서 조회한 데이터를 캐시에 저장 (비동기)
            if (storageResult.isPresent()) {
                cacheViewCountAsync(storageResult.get());
                log.debug("View count loaded from storage and cached for prompt: {}, count: {}",
                    promptTemplateId, storageResult.get().getTotalViewCount());
            } else {
                log.debug("No view count found for prompt: {}", promptTemplateId);
            }

            return storageResult;

        } catch (ViewOperationException e) {
            log.error("View count loading failed: promptUuid={}, error={}",
                query.getPromptTemplateUuid(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while loading view count: promptUuid={}",
                query.getPromptTemplateUuid(), e);
            throw ViewOperationException.viewCountLoadFailed(
                resolvePromptTemplateId(query), e);
        }
    }

    /**
     * 프롬프트의 총 조회수를 조회합니다.
     * 빠른 응답을 위해 숫자만 반환합니다.
     *
     * @param query 조회수 조회 쿼리 객체
     * @return 총 조회수 (없는 경우 0 반환)
     */
    @Override
    public long getTotalViewCount(GetViewCountQuery query) {
        Assert.notNull(query, "GetViewCountQuery must not be null");

        log.debug("Loading total view count with cache-first strategy: promptUuid={}, promptId={}",
            query.getPromptTemplateUuid(), query.getPromptTemplateId());

        try {
            Optional<ViewCount> viewCount = getViewCount(query);
            long totalCount = viewCount.map(ViewCount::getTotalViewCount).orElse(0L);

            log.debug("Total view count loaded: promptId={}, count={}",
                resolvePromptTemplateId(query), totalCount);

            return totalCount;

        } catch (Exception e) {
            log.error("Failed to get total view count: promptUuid={}",
                query.getPromptTemplateUuid(), e);
            // 조회수 조회 실패 시 0 반환 (시스템 안정성을 위해)
            return 0L;
        }
    }

    /**
     * 캐시에서 조회수를 조회합니다.
     *
     * @param query 조회수 조회 쿼리
     * @return 캐시된 조회수 정보
     */
    private Optional<ViewCount> loadFromCache(LoadViewCountQuery query) {
        try {
            return loadViewCountFromCachePort.loadViewCountFromCache(query);
        } catch (Exception e) {
            log.warn("Failed to load from cache for prompt: {}, falling back to storage: {}",
                query.getPromptTemplateId(), e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 데이터베이스에서 조회수를 조회합니다.
     *
     * @param query 조회수 조회 쿼리
     * @return 저장된 조회수 정보
     */
    private Optional<ViewCount> loadFromStorage(LoadViewCountQuery query) {
        try {
            return loadViewCountFromStoragePort.loadViewCountFromStorage(query);
        } catch (Exception e) {
            log.error("Failed to load from storage for prompt: {}: {}",
                query.getPromptTemplateId(), e.getMessage());
            throw e;
        }
    }

    /**
     * 조회수 정보를 비동기적으로 캐시에 저장합니다.
     * 캐시 저장 실패가 전체 조회 로직에 영향을 주지 않도록 합니다.
     *
     * @param viewCount 캐시할 조회수 정보
     */
    private void cacheViewCountAsync(ViewCount viewCount) {
        try {
            cacheViewCountPort.cacheViewCount(viewCount);
            log.debug("Successfully cached view count for prompt: {}", viewCount.getPromptTemplateId());
        } catch (Exception e) {
            log.warn("Failed to cache view count for prompt: {}, continuing without cache: {}",
                viewCount.getPromptTemplateId(), e.getMessage());
            // 캐시 저장 실패는 무시 (조회 로직에 영향 없음)
        }
    }

    /**
     * UUID 또는 ID에서 내부 프롬프트 템플릿 ID를 확보합니다.
     *
     * @param query 조회 쿼리
     * @return 프롬프트 내부 ID
     * @throws ViewOperationException 프롬프트가 존재하지 않을 때
     */
    private Long resolvePromptTemplateId(GetViewCountQuery query) {
        // 내부 ID가 이미 있는 경우 그대로 사용
        if (query.getPromptTemplateId() != null) {
            return query.getPromptTemplateId();
        }

        // UUID를 내부 ID로 변환
        if (query.getPromptTemplateUuid() != null) {
            return findPromptIdOrThrow(query.getPromptTemplateUuid());
        }

        // 둘 다 없는 경우 (생성자에서 이미 검증되지만 안전장치)
        throw new IllegalStateException("Either promptTemplateUuid or promptTemplateId must be provided");
    }

    /**
     * UUID로 프롬프트 내부 ID를 조회합니다.
     *
     * @param promptTemplateUuid 프롬프트 템플릿 UUID
     * @return 프롬프트 내부 ID
     * @throws ViewOperationException 프롬프트가 존재하지 않을 때
     */
    private Long findPromptIdOrThrow(UUID promptTemplateUuid) {
        return loadPromptTemplateIdPort.findIdByUuid(promptTemplateUuid)
            .orElseThrow(() -> {
                log.error("Prompt not found for UUID: {}", promptTemplateUuid);
                return ViewOperationException.promptNotFound(promptTemplateUuid);
            });
    }

    /**
     * 여러 프롬프트의 조회수를 일괄 조회합니다.
     * Redis Pipeline을 사용하여 성능을 최적화합니다.
     *
     * @param promptTemplateIds 프롬프트 템플릿 ID 목록
     * @return 프롬프트 ID를 키로 하는 조회수 맵
     */
    public Map<Long, Long> getViewCountsByPromptIds(List<Long> promptTemplateIds) {
        Assert.notNull(promptTemplateIds, "promptTemplateIds must not be null");
        Assert.notEmpty(promptTemplateIds, "promptTemplateIds must not be empty");

        log.debug("Loading view counts for {} prompts with batch optimization", promptTemplateIds.size());

        try {
            Map<Long, Long> viewCountMap = new HashMap<>();

            // 1. Redis에서 배치 조회 시도
            Map<Long, Long> cachedCounts = loadViewCountsFromCacheBatch(promptTemplateIds);
            viewCountMap.putAll(cachedCounts);

            // 2. 캐시 미스된 프롬프트 ID 추출
            List<Long> cacheMissIds = promptTemplateIds.stream()
                .filter(id -> !cachedCounts.containsKey(id))
                .collect(Collectors.toList());

            // 3. DB에서 캐시 미스된 항목 조회
            if (!cacheMissIds.isEmpty()) {
                log.debug("Cache miss for {} prompts, loading from storage", cacheMissIds.size());
                Map<Long, Long> storageCounts = loadViewCountsFromStorageBatch(cacheMissIds);
                viewCountMap.putAll(storageCounts);

                // 4. DB에서 조회한 데이터를 비동기로 캐시에 저장
                cacheViewCountsBatchAsync(storageCounts);
            }

            log.debug("Batch view count loading completed: {} prompts, {} cached, {} from storage",
                promptTemplateIds.size(), cachedCounts.size(), cacheMissIds.size());

            return viewCountMap;

        } catch (Exception e) {
            log.error("Failed to load view counts for prompts: {}", promptTemplateIds, e);
            // 실패 시 빈 맵 반환 (시스템 안정성 우선)
            return Collections.emptyMap();
        }
    }

    /**
     * Redis에서 여러 프롬프트의 조회수를 배치로 조회합니다.
     *
     * @param promptTemplateIds 프롬프트 템플릿 ID 목록
     * @return 프롬프트 ID를 키로 하는 조회수 맵
     */
    private Map<Long, Long> loadViewCountsFromCacheBatch(List<Long> promptTemplateIds) {
        try {
            Map<Long, Long> result = new HashMap<>();

            // Redis Pipeline을 사용한 배치 조회
            for (Long promptId : promptTemplateIds) {
                LoadViewCountQuery query = LoadViewCountQuery.of(promptId);
                Optional<ViewCount> cached = loadFromCache(query);
                if (cached.isPresent()) {
                    result.put(promptId, cached.get().getTotalViewCount());
                }
            }

            log.debug("Loaded {} view counts from cache", result.size());
            return result;

        } catch (Exception e) {
            log.warn("Failed to load view counts from cache batch: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * DB에서 여러 프롬프트의 조회수를 배치로 조회합니다.
     *
     * @param promptTemplateIds 프롬프트 템플릿 ID 목록
     * @return 프롬프트 ID를 키로 하는 조회수 맵
     */
    private Map<Long, Long> loadViewCountsFromStorageBatch(List<Long> promptTemplateIds) {
        try {
            Map<Long, Long> result = new HashMap<>();

            for (Long promptId : promptTemplateIds) {
                LoadViewCountQuery query = LoadViewCountQuery.of(promptId);
                Optional<ViewCount> stored = loadFromStorage(query);
                result.put(promptId, stored.map(ViewCount::getTotalViewCount).orElse(0L));
            }

            log.debug("Loaded {} view counts from storage", result.size());
            return result;

        } catch (Exception e) {
            log.error("Failed to load view counts from storage batch: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * 여러 조회수 정보를 비동기적으로 캐시에 저장합니다.
     *
     * @param viewCounts 캐시할 조회수 맵
     */
    @Async
    private void cacheViewCountsBatchAsync(Map<Long, Long> viewCounts) {
        try {
            for (Map.Entry<Long, Long> entry : viewCounts.entrySet()) {
                ViewCount viewCount = ViewCount.builder()
                    .promptTemplateId(entry.getKey())
                    .totalViewCount(entry.getValue())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

                cacheViewCountPort.cacheViewCount(viewCount);
            }
            log.debug("Successfully cached {} view counts asynchronously", viewCounts.size());
        } catch (Exception e) {
            log.warn("Failed to cache view counts batch: {}", e.getMessage());
            // 캐시 저장 실패는 무시 (조회 로직에 영향 없음)
        }
    }
}
