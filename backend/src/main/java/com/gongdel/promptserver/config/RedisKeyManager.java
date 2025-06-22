package com.gongdel.promptserver.config;

import com.gongdel.promptserver.domain.view.ViewIdentifier;
import com.gongdel.promptserver.domain.view.ViewKeyStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.format.DateTimeFormatter;

/**
 * Redis 키 네이밍 전략 및 TTL 관리를 담당하는 유틸리티 클래스입니다.
 * <p>
 * 조회수 기능에 사용되는 모든 Redis 키의 생성과 관리를 중앙화합니다.
 * ViewKeyStrategy 인터페이스를 구현하여 도메인 포트와 연결됩니다.
 * <p>
 * 키 네이밍 규칙:
 * - 중복 체크: view:user:{userId}:prompt:{promptId} 또는
 * view:ip:{ipAddress}:prompt:{promptId}
 * - 조회수 캐시: viewcount:{promptId}
 */
@Component
@Slf4j
public class RedisKeyManager implements ViewKeyStrategy {

    // 키 접두사 상수
    private static final String VIEW_DUPLICATION_PREFIX = "view";
    private static final String VIEW_COUNT_PREFIX = "viewcount";

    // 날짜 포맷터
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    // TTL 설정값 (application.yml에서 오버라이드 가능)
    @Value("${redis.view.duplication-check-ttl-hours:1}")
    private int duplicationCheckTtlHours;

    @Value("${redis.view.count-cache-ttl-hours:24}")
    private int countCacheTtlHours;

    @Value("${redis.view.daily-stats-ttl-days:7}")
    private int dailyStatsTtlDays;

    @Value("${redis.view.batch-lock-ttl-minutes:5}")
    private int batchLockTtlMinutes;

    /**
     * 사용자 중복 체크 키 생성
     */
    public String createUserDuplicationKey(Long userId, Long promptId) {
        Assert.notNull(userId, "User ID must not be null");
        Assert.notNull(promptId, "Prompt ID must not be null");

        String key = String.format("%s:user:%d:prompt:%d",
            VIEW_DUPLICATION_PREFIX, userId, promptId);

        log.debug("Created user duplication key: {}", key);
        return key;
    }

    /**
     * IP 중복 체크 키 생성
     */
    public String createIpDuplicationKey(String ipAddress, Long promptId) {
        Assert.hasText(ipAddress, "IP address must not be empty");
        Assert.notNull(promptId, "Prompt ID must not be null");

        // IP 주소의 특수문자를 안전한 형태로 변환
        String safeIpAddress = ipAddress.replaceAll("[^a-zA-Z0-9.-]", "_");

        String key = String.format("%s:ip:%s:prompt:%d",
            VIEW_DUPLICATION_PREFIX, safeIpAddress, promptId);

        log.debug("Created IP duplication key: {}", key);
        return key;
    }

    /**
     * 익명 사용자 중복 체크 키 생성
     */
    public String createAnonymousDuplicationKey(String anonymousId, Long promptId) {
        Assert.hasText(anonymousId, "Anonymous ID must not be empty");
        Assert.notNull(promptId, "Prompt ID must not be null");

        String key = String.format("%s:anon:%s:prompt:%d",
            VIEW_DUPLICATION_PREFIX, anonymousId, promptId);

        log.debug("Created anonymous duplication key: {}", key);
        return key;
    }

    /**
     * 조회수 캐시 키 생성
     */
    public String createViewCountKey(String promptId) {
        Assert.notNull(promptId, "Prompt ID must not be null");

        String key = String.format("%s:%s", VIEW_COUNT_PREFIX, promptId);
        log.debug("Created view count key: {}", key);
        return key;
    }

    /**
     * 중복 체크 TTL (시간 단위)
     */
    public long getDuplicationCheckTtl() {
        return duplicationCheckTtlHours;
    }

    /**
     * 조회수 캐시 TTL (시간 단위)
     */
    public long getCountCacheTtl() {
        return countCacheTtlHours;
    }

    /**
     * 모든 조회수 캐시 키를 찾기 위한 패턴을 반환합니다.
     *
     * @return 조회수 캐시 키 패턴
     */
    public String getViewCountKeyPattern() {
        return VIEW_COUNT_PREFIX + ":*";
    }

    /**
     * 키에서 프롬프트 ID를 추출합니다.
     *
     * @param key Redis 키
     * @return 프롬프트 ID
     * @throws IllegalArgumentException 키 형식이 올바르지 않은 경우
     */
    public Long extractPromptIdFromViewCountKey(String key) {
        Assert.hasText(key, "key must not be blank");

        if (!key.startsWith(VIEW_COUNT_PREFIX + ":")) {
            throw new IllegalArgumentException("Invalid view count key format: " + key);
        }

        try {
            String promptIdStr = key.substring(VIEW_COUNT_PREFIX.length() + 1);
            return Long.parseLong(promptIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid prompt ID in key: " + key, e);
        }
    }

    /**
     * ViewKeyStrategy 인터페이스 구현: 중복 체크 키 생성
     */
    @Override
    public String createDuplicationCheckKey(ViewIdentifier identifier) {
        Assert.notNull(identifier, "ViewIdentifier must not be null");

        switch (identifier.getViewerType()) {
            case AUTHENTICATED_USER:
                return createUserDuplicationKey(identifier.getUserId(), identifier.getPromptTemplateId());
            case ANONYMOUS_USER:
                return createAnonymousDuplicationKey(identifier.getAnonymousId(), identifier.getPromptTemplateId());
            case IP_BASED_USER:
                return createIpDuplicationKey(identifier.getIpAddress(), identifier.getPromptTemplateId());
            default:
                throw new IllegalArgumentException("Unsupported viewer type: " + identifier.getViewerType());
        }
    }

    /**
     * ViewKeyStrategy 인터페이스 구현: 조회수 캐시 키 생성
     */
    @Override
    public String createViewCountKey(Long promptTemplateId) {
        return createViewCountKey(promptTemplateId.toString());
    }
}
