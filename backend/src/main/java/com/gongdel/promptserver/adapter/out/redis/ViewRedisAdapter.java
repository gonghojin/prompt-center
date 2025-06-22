package com.gongdel.promptserver.adapter.out.redis;

import com.gongdel.promptserver.application.port.out.CacheViewCountPort;
import com.gongdel.promptserver.application.port.out.LoadViewCountFromCachePort;
import com.gongdel.promptserver.application.port.out.RecordViewPort;
import com.gongdel.promptserver.domain.view.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis 기반 조회수 처리를 담당하는 어댑터
 * 캐시 우선 조회와 실시간 조회수 증가를 담당합니다.
 * 단순한 count 값을 저장하여 고성능을 제공합니다.
 * <p>
 * 헥사고날 아키텍처 원칙에 따라 ViewKeyStrategy 인터페이스를 통해
 * 도메인 포트와 연결됩니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ViewRedisAdapter implements RecordViewPort, LoadViewCountFromCachePort, CacheViewCountPort {

    private final StringRedisTemplate stringRedisTemplate;
    private final ViewKeyStrategy viewKeyStrategy;

    /**
     * 조회 기록을 저장합니다.
     * 중복 체크를 수행하고 새로운 조회인 경우에만 기록합니다.
     *
     * @param command 조회 기록 명령
     * @return 실제로 조회수가 증가했는지 여부 (중복이 아닌 경우 true)
     */
    @Override
    public boolean recordView(RecordViewCommand command) {
        try {
            Assert.notNull(command, "RecordViewCommand must not be null");

            log.debug("Recording view for prompt: {}", command.getPromptTemplateId());

            ViewRecord viewRecord = command.toViewRecord();

            // 도메인 서비스를 통한 키 생성
            String duplicateCheckKey = viewKeyStrategy.createDuplicationCheckKey(
                viewRecord.getViewIdentifier());

            // 중복 체크: 키가 이미 존재하면 중복 조회
            Boolean isNewView = stringRedisTemplate.opsForValue()
                .setIfAbsent(duplicateCheckKey, "1",
                    viewKeyStrategy.getDuplicationCheckTtl(), TimeUnit.HOURS);

            if (Boolean.TRUE.equals(isNewView)) {
                // 새로운 조회인 경우 조회수 증가
                long newCount = incrementViewCountInCache(command.getPromptTemplateId());

                log.info("New view recorded for prompt: {}, new count: {}",
                    command.getPromptTemplateId(), newCount);
                return true;
            } else {
                log.debug("Duplicate view detected for prompt: {}", command.getPromptTemplateId());
                return false;
            }

        } catch (Exception e) {
            log.error("Failed to record view for prompt: {}", command.getPromptTemplateId(), e);
            throw ViewOperationException.redisOperationFailed(
                "Failed to record view", e);
        }
    }

    /**
     * 캐시에서 조회수를 조회합니다.
     *
     * @param query 조회수 조회 쿼리
     * @return 캐시된 조회수 정보 (캐시에 없으면 Optional.empty())
     */
    @Override
    public Optional<ViewCount> loadViewCountFromCache(LoadViewCountQuery query) {
        try {
            Assert.notNull(query, "LoadViewCountQuery must not be null");

            log.debug("Loading view count from cache for prompt: {}", query.getPromptTemplateId());

            return loadViewCountFromCache(query.getPromptTemplateId());

        } catch (Exception e) {
            log.error("Failed to load view count from cache for prompt: {}", query.getPromptTemplateId(), e);
            throw ViewOperationException.redisOperationFailed(
                "Failed to load view count from cache", e);
        }
    }

    /**
     * 프롬프트 템플릿 ID로 캐시에서 조회수를 조회합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 캐시된 조회수 정보 (캐시에 없으면 Optional.empty())
     */
    @Override
    public Optional<ViewCount> loadViewCountFromCache(Long promptTemplateId) {
        try {
            Assert.notNull(promptTemplateId, "Prompt template ID must not be null");

            log.debug("Getting cached view count for prompt: {}", promptTemplateId);

            String key = viewKeyStrategy.createViewCountKey(promptTemplateId);
            String value = stringRedisTemplate.opsForValue().get(key);

            if (value != null) {
                try {
                    long count = Long.parseLong(value);
                    ViewCount viewCount = ViewCount.builder()
                        .promptTemplateId(promptTemplateId)
                        .totalViewCount(count)
                        .createdAt(LocalDateTime.now()) // 캐시에서는 메타데이터 없음
                        .updatedAt(LocalDateTime.now())
                        .build();

                    log.debug("Found cached view count for prompt: {}, count: {}",
                        promptTemplateId, count);
                    return Optional.of(viewCount);
                } catch (NumberFormatException e) {
                    log.warn("Invalid view count value in cache for prompt {}: {}", promptTemplateId, value);
                    return Optional.empty();
                }
            } else {
                log.debug("No cached view count found for prompt: {}", promptTemplateId);
                return Optional.empty();
            }

        } catch (Exception e) {
            log.error("Failed to get cached view count for prompt: {}", promptTemplateId, e);
            throw ViewOperationException.redisOperationFailed(
                "Failed to get cached view count", e);
        }
    }

    /**
     * 조회수를 캐시에 저장합니다.
     *
     * @param viewCount 저장할 조회수 정보
     */
    @Override
    public void cacheViewCount(ViewCount viewCount) {
        try {
            Assert.notNull(viewCount, "ViewCount must not be null");

            log.debug("Caching view count for prompt: {}", viewCount.getPromptTemplateId());

            String key = viewKeyStrategy.createViewCountKey(viewCount.getPromptTemplateId());
            String value = String.valueOf(viewCount.getTotalViewCount());

            stringRedisTemplate.opsForValue().set(key, value,
                viewKeyStrategy.getCountCacheTtl(), TimeUnit.HOURS);

            log.debug("View count cached successfully for prompt: {}", viewCount.getPromptTemplateId());

        } catch (Exception e) {
            log.error("Failed to cache view count for prompt: {}", viewCount.getPromptTemplateId(), e);
            throw ViewOperationException.redisOperationFailed(
                "Failed to cache view count", e);
        }
    }

    /**
     * 조회수 캐시를 삭제합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     */
    @Override
    public void evictViewCountCache(Long promptTemplateId) {
        try {
            Assert.notNull(promptTemplateId, "Prompt template ID must not be null");

            log.debug("Evicting view count cache for prompt: {}", promptTemplateId);

            String key = viewKeyStrategy.createViewCountKey(promptTemplateId);
            Boolean deleted = stringRedisTemplate.delete(key);

            if (Boolean.TRUE.equals(deleted)) {
                log.debug("View count cache evicted successfully for prompt: {}", promptTemplateId);
            } else {
                log.debug("No cache to evict for prompt: {}", promptTemplateId);
            }

        } catch (Exception e) {
            log.error("Failed to evict view count cache for prompt: {}", promptTemplateId, e);
            throw ViewOperationException.redisOperationFailed(
                "Failed to evict view count cache", e);
        }
    }

    /**
     * 조회수를 원자적으로 증가시킵니다.
     * Redis INCR 명령을 사용하여 고성능 원자적 증가를 수행합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 증가된 조회수
     */
    @Override
    public long incrementViewCountInCache(Long promptTemplateId) {
        try {
            Assert.notNull(promptTemplateId, "Prompt template ID must not be null");

            log.debug("Incrementing view count for prompt: {}", promptTemplateId);

            // 도메인 서비스를 통한 키 생성
            String key = viewKeyStrategy.createViewCountKey(promptTemplateId);

            // Redis INCR 명령으로 원자적 증가 (고성능)
            Long newCount = stringRedisTemplate.opsForValue().increment(key, 1);

            // TTL 설정 (키가 새로 생성된 경우에만)
            if (newCount != null && newCount == 1) {
                stringRedisTemplate.expire(key, viewKeyStrategy.getCountCacheTtl(), TimeUnit.HOURS);
            }

            long result = newCount != null ? newCount : 1L;

            log.debug("View count incremented for prompt: {}, new count: {}",
                promptTemplateId, result);

            return result;

        } catch (Exception e) {
            log.error("Failed to increment view count for prompt: {}", promptTemplateId, e);
            throw ViewOperationException.redisOperationFailed(
                "Failed to increment view count", e);
        }
    }

    /**
     * 캐시에 저장된 모든 프롬프트 ID 목록을 조회합니다.
     * Redis SCAN 명령을 사용하여 메모리 효율적으로 조회합니다.
     *
     * @return 캐시에 저장된 프롬프트 ID 목록
     */
    public List<Long> getCachedPromptIds() {
        try {
            log.debug("Scanning cached prompt IDs using Redis SCAN");

            // RedisKeyManager의 패턴 사용
            String pattern = viewKeyStrategy.getViewCountKeyPattern();
            Set<String> keys = new HashSet<>();

            // Redis SCAN 명령을 사용하여 메모리 효율적으로 키 스캔
            ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(pattern)
                .count(100) // 한 번에 스캔할 키 개수
                .build();

            Cursor<String> cursor = stringRedisTemplate.scan(scanOptions);
            while (cursor.hasNext()) {
                keys.add(cursor.next());
            }
            cursor.close();

            // RedisKeyManager의 키 추출 메서드 사용
            List<Long> promptIds = keys.stream()
                .map(key -> {
                    try {
                        return viewKeyStrategy.extractPromptIdFromViewCountKey(key);
                    } catch (IllegalArgumentException e) {
                        log.warn("Failed to extract prompt ID from key: {}", key, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

            log.info("Found {} cached prompt IDs with pattern: {}", promptIds.size(), pattern);
            return promptIds;

        } catch (Exception e) {
            log.error("Failed to scan cached prompt IDs", e);
            return List.of();
        }
    }

}
