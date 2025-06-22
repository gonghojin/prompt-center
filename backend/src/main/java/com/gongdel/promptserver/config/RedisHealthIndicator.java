package com.gongdel.promptserver.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Redis 연결 상태를 체크하는 유틸리티 클래스입니다.
 * <p>
 * 조회수 기능에서 Redis 장애 시 fallback 로직을 위해 사용됩니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisHealthIndicator {

    private static final String HEALTH_CHECK_KEY = "health:check";
    private static final String HEALTH_CHECK_VALUE = "ok";
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisConnectionFactory redisConnectionFactory;

    /**
     * Redis 연결 가능 여부를 간단히 체크합니다.
     *
     * @return 연결 가능하면 true
     */
    public boolean isRedisAvailable() {
        try {
            String result = redisTemplate.execute((RedisCallback<String>) connection -> {
                try {
                    return connection.ping();
                } catch (Exception e) {
                    log.debug("Redis PING command failed", e);
                    return null;
                }
            });
            return "PONG".equals(result);
        } catch (Exception e) {
            log.debug("Redis availability check failed", e);
            return false;
        }
    }

    /**
     * Redis 연결을 테스트합니다. (읽기/쓰기 테스트)
     *
     * @return 테스트 성공 여부
     */
    public boolean testRedisReadWrite() {
        try {
            String testKey = HEALTH_CHECK_KEY + ":" + System.currentTimeMillis();

            // 쓰기 테스트
            redisTemplate.opsForValue().set(testKey, HEALTH_CHECK_VALUE, Duration.ofSeconds(10));

            // 읽기 테스트
            String value = (String) redisTemplate.opsForValue().get(testKey);

            // 정리
            redisTemplate.delete(testKey);

            return HEALTH_CHECK_VALUE.equals(value);

        } catch (Exception e) {
            log.debug("Redis read/write test failed", e);
            return false;
        }
    }

    /**
     * Redis 연결 상태를 상세히 체크합니다.
     *
     * @return Redis 연결 정보
     */
    public RedisConnectionInfo checkRedisConnection() {
        long startTime = System.currentTimeMillis();

        try {
            // PING 명령으로 연결 상태 확인
            String result = redisTemplate.execute((RedisCallback<String>) connection -> {
                try {
                    return connection.ping();
                } catch (Exception e) {
                    log.warn("Redis PING command failed", e);
                    return null;
                }
            });

            long responseTime = System.currentTimeMillis() - startTime;

            if ("PONG".equals(result)) {
                // 추가 정보 수집
                String version = getRedisVersion();

                return RedisConnectionInfo.builder()
                    .connected(true)
                    .responseTimeMs(responseTime)
                    .version(version)
                    .build();
            } else {
                return RedisConnectionInfo.builder()
                    .connected(false)
                    .errorMessage("PING command failed - expected PONG but got: " + result)
                    .responseTimeMs(responseTime)
                    .build();
            }

        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            log.error("Redis connection check failed", e);

            return RedisConnectionInfo.builder()
                .connected(false)
                .errorMessage(e.getMessage())
                .responseTimeMs(responseTime)
                .build();
        }
    }

    /**
     * Redis 서버 버전을 조회합니다.
     *
     * @return Redis 버전 정보
     */
    private String getRedisVersion() {
        try {
            return redisTemplate.execute((RedisCallback<String>) connection -> {
                try {
                    return connection.info("server")
                        .getProperty("redis_version");
                } catch (Exception e) {
                    log.debug("Failed to get Redis version", e);
                    return "unknown";
                }
            });
        } catch (Exception e) {
            log.debug("Failed to get Redis version", e);
            return "unknown";
        }
    }

    /**
     * Redis 연결 정보를 담는 클래스입니다.
     */
    public static class RedisConnectionInfo {
        private final boolean connected;
        private final long responseTimeMs;
        private final String version;
        private final String errorMessage;
        private final LocalDateTime checkedAt;

        private RedisConnectionInfo(Builder builder) {
            this.connected = builder.connected;
            this.responseTimeMs = builder.responseTimeMs;
            this.version = builder.version;
            this.errorMessage = builder.errorMessage;
            this.checkedAt = LocalDateTime.now();
        }

        public static Builder builder() {
            return new Builder();
        }

        public boolean isConnected() {
            return connected;
        }

        public long getResponseTimeMs() {
            return responseTimeMs;
        }

        public String getVersion() {
            return version;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public LocalDateTime getCheckedAt() {
            return checkedAt;
        }

        @Override
        public String toString() {
            return String.format(
                "RedisConnectionInfo{connected=%s, responseTime=%dms, version='%s', error='%s', checkedAt=%s}",
                connected, responseTimeMs, version, errorMessage, checkedAt);
        }

        public static class Builder {
            private boolean connected;
            private long responseTimeMs;
            private String version;
            private String errorMessage;

            public Builder connected(boolean connected) {
                this.connected = connected;
                return this;
            }

            public Builder responseTimeMs(long responseTimeMs) {
                this.responseTimeMs = responseTimeMs;
                return this;
            }

            public Builder version(String version) {
                this.version = version;
                return this;
            }

            public Builder errorMessage(String errorMessage) {
                this.errorMessage = errorMessage;
                return this;
            }

            public RedisConnectionInfo build() {
                return new RedisConnectionInfo(this);
            }
        }
    }
}
