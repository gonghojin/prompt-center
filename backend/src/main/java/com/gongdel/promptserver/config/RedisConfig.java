package com.gongdel.promptserver.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 설정 클래스입니다.
 * <p>
 * 애플리케이션에서 사용하는 다양한 Redis 템플릿을 제공합니다.
 * - 기본 RedisTemplate: 일반적인 객체 캐싱 및 데이터 저장
 * - StringRedisTemplate: 문자열 기반 캐싱 및 카운터 작업
 * - ViewCount RedisTemplate: 고성능 카운터 및 간단한 키-값 작업
 * <p>
 * 각 템플릿은 용도에 맞는 직렬화 전략을 사용하여 성능을 최적화합니다.
 */
@Configuration
@Slf4j
public class RedisConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Value("${spring.data.redis.database:0}")
    private int database;

    /**
     * Redis 연결 팩토리를 생성합니다.
     *
     * @return RedisConnectionFactory
     */
    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        config.setDatabase(database);

        if (redisPassword != null && !redisPassword.trim().isEmpty()) {
            config.setPassword(redisPassword);
        }

        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        factory.setValidateConnection(true);
        factory.afterPropertiesSet();

        log.info("Redis connection configured - host: {}, port: {}, database: {}, password: {}",
            redisHost, redisPort, database,
            redisPassword != null && !redisPassword.trim().isEmpty() ? "***" : "none");

        return factory;
    }

    /**
     * 기본 RedisTemplate을 생성합니다.
     * <p>
     * 범용적인 객체 캐싱 및 복잡한 데이터 구조 저장에 사용됩니다.
     * Key는 String으로, Value는 JSON으로 직렬화하여 객체 저장을 지원합니다.
     *
     * @param connectionFactory Redis 연결 팩토리
     * @return RedisTemplate<String, Object>
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key 직렬화: String
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value 직렬화: JSON
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.setDefaultSerializer(jsonSerializer);
        template.afterPropertiesSet();

        log.debug("RedisTemplate configured with JSON serialization");
        return template;
    }

    /**
     * String 전용 RedisTemplate을 생성합니다.
     * <p>
     * 문자열 기반의 캐싱, 카운터, 세션 저장 등에 최적화되어 있습니다.
     * Key와 Value 모두 String으로 직렬화하여 빠른 성능을 제공합니다.
     *
     * @param connectionFactory Redis 연결 팩토리
     * @return StringRedisTemplate
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);

        log.debug("StringRedisTemplate configured for string-based operations");
        return template;
    }

    /**
     * 고성능 String RedisTemplate을 생성합니다.
     * <p>
     * 빈번한 읽기/쓰기 작업이 필요한 카운터, 통계, 실시간 데이터 처리에 특화되었습니다.
     * 모든 직렬화를 String으로 설정하여 최대 성능을 제공합니다.
     *
     * @param connectionFactory Redis 연결 팩토리
     * @return RedisTemplate<String, String>
     */
    @Bean("viewCountRedisTemplate")
    public RedisTemplate<String, String> viewCountRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 모든 직렬화를 String으로 설정 (최대 성능 최적화)
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(stringSerializer);
        template.setDefaultSerializer(stringSerializer);

        template.afterPropertiesSet();

        log.debug("High-performance String RedisTemplate configured");
        return template;
    }
}
