package com.gongdel.promptserver.application.usecase.command;

import com.gongdel.promptserver.adapter.out.redis.ViewRedisAdapter;
import com.gongdel.promptserver.application.port.out.LoadViewCountFromCachePort;
import com.gongdel.promptserver.application.port.out.LoadViewCountFromStoragePort;
import com.gongdel.promptserver.application.port.out.UpdateViewCountPort;
import com.gongdel.promptserver.domain.view.ViewCount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 조회수 캐시와 데이터베이스 간의 동기화를 담당하는 배치 서비스입니다.
 * 주기적으로 Redis 캐시의 조회수를 데이터베이스에 동기화합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ViewSyncBatchService {

    private final LoadViewCountFromCachePort loadViewCountFromCachePort;
    private final LoadViewCountFromStoragePort loadViewCountFromStoragePort;
    private final UpdateViewCountPort updateViewCountPort;
    private final ViewRedisAdapter viewRedisAdapter; // Redis 키 스캔용

    /**
     * 매 30분마다 Redis 캐시의 조회수를 데이터베이스에 동기화합니다.
     * 실운영에서는 트래픽 패턴에 따라 주기를 조정할 수 있습니다.
     */
    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.MINUTES)
    @Transactional
    public void syncViewCountsToDatabase() {
        log.info("Starting view count synchronization batch job");

        try {
            long startTime = System.currentTimeMillis();

            // Redis에서 캐시된 프롬프트 ID 목록 조회
            List<Long> promptIdsToSync = viewRedisAdapter.getCachedPromptIds();

            if (promptIdsToSync.isEmpty()) {
                log.info("No view counts to synchronize");
                return;
            }

            log.info("Found {} prompts with cached view counts to synchronize", promptIdsToSync.size());

            // 비동기로 동기화 처리 (성능 최적화)
            CompletableFuture<Void> syncFuture = CompletableFuture.runAsync(() -> {
                promptIdsToSync.parallelStream().forEach(this::syncSinglePromptViewCount);
            });

            // 최대 5분 대기
            syncFuture.get(5, TimeUnit.MINUTES);

            long duration = System.currentTimeMillis() - startTime;
            log.info("View count synchronization completed in {}ms for {} prompts",
                duration, promptIdsToSync.size());

        } catch (Exception e) {
            log.error("Failed to synchronize view counts to database", e);
        }
    }

    /**
     * 개별 프롬프트의 조회수를 동기화합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     */
    private void syncSinglePromptViewCount(Long promptTemplateId) {
        try {
            // 캐시에서 현재 조회수 조회
            Optional<ViewCount> cachedViewCount = loadViewCountFromCachePort.loadViewCountFromCache(promptTemplateId);
            if (cachedViewCount.isEmpty()) {
                log.debug("No cached view count found for prompt: {}", promptTemplateId);
                return;
            }

            ViewCount cached = cachedViewCount.get();

            // 데이터베이스에서 현재 조회수 조회
            Optional<ViewCount> storedViewCount = loadViewCountFromStoragePort
                .loadViewCountFromStorage(promptTemplateId);
            long storedCount = storedViewCount.map(ViewCount::getTotalViewCount).orElse(0L);

            // 차이가 있는 경우에만 동기화
            long difference = cached.getTotalViewCount() - storedCount;
            if (difference > 0) {
                // 차이만큼 증가시키는 로직 (여러 번 호출하는 대신 배치로 처리)
                for (int i = 0; i < difference; i++) {
                    updateViewCountPort.incrementViewCount(promptTemplateId);
                }

                log.debug("Synchronized view count for prompt {}: added {} views (cached: {}, stored: {})",
                    promptTemplateId, difference, cached.getTotalViewCount(), storedCount);
            }

        } catch (Exception e) {
            log.error("Failed to sync view count for prompt: {}", promptTemplateId, e);
        }
    }

    /**
     * 특정 프롬프트의 조회수를 즉시 동기화합니다.
     * 관리자 도구나 긴급 상황에서 사용할 수 있습니다.
     *
     * @param promptTemplateId 동기화할 프롬프트 템플릿 ID
     * @return 동기화 성공 여부
     */
    public boolean forceSyncViewCount(Long promptTemplateId) {
        try {
            log.info("Force synchronizing view count for prompt: {}", promptTemplateId);
            syncSinglePromptViewCount(promptTemplateId);
            return true;
        } catch (Exception e) {
            log.error("Failed to force sync view count for prompt: {}", promptTemplateId, e);
            return false;
        }
    }

    /**
     * 전체 조회수 데이터의 정합성을 검증합니다.
     * 정기적으로 실행하여 데이터 일관성을 확인할 수 있습니다.
     */
    @Scheduled(cron = "0 0 2 * * ?") // 매일 새벽 2시에 실행
    public void validateViewCountConsistency() {
        log.info("Starting view count consistency validation");

        try {
            List<Long> cachedPromptIds = viewRedisAdapter.getCachedPromptIds();
            int inconsistentCount = 0;

            for (Long promptId : cachedPromptIds) {
                Optional<ViewCount> cached = loadViewCountFromCachePort.loadViewCountFromCache(promptId);
                Optional<ViewCount> stored = loadViewCountFromStoragePort.loadViewCountFromStorage(promptId);

                if (cached.isPresent() && stored.isPresent()) {
                    long difference = cached.get().getTotalViewCount() - stored.get().getTotalViewCount();
                    if (Math.abs(difference) > 10) { // 10 이상 차이나면 경고
                        log.warn("Large view count difference detected for prompt {}: cached={}, stored={}, diff={}",
                            promptId, cached.get().getTotalViewCount(), stored.get().getTotalViewCount(),
                            difference);
                        inconsistentCount++;
                    }
                }
            }

            if (inconsistentCount > 0) {
                log.warn("Found {} prompts with significant view count inconsistencies", inconsistentCount);
            } else {
                log.info("View count consistency validation passed for {} prompts", cachedPromptIds.size());
            }

        } catch (Exception e) {
            log.error("Failed to validate view count consistency", e);
        }
    }
}
