package com.gongdel.promptserver.adapter.out.redis.mapper;

import com.gongdel.promptserver.domain.view.ViewCount;
import com.gongdel.promptserver.domain.view.ViewIdentifier;
import com.gongdel.promptserver.domain.view.ViewOperationException;
import com.gongdel.promptserver.domain.view.ViewRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * View 도메인 객체와 Redis 데이터 간의 변환을 담당하는 매퍼입니다.
 */
@Slf4j
@Component
public class ViewRedisMapper {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * ViewCount 도메인 객체를 Redis 저장용 JSON 문자열로 변환합니다.
     */
    public String toRedisValue(ViewCount viewCount) {
        Assert.notNull(viewCount, "ViewCount must not be null");

        try {
            return String.format(
                "{\"promptTemplateId\":%d,\"totalViewCount\":%d,\"createdAt\":\"%s\",\"updatedAt\":\"%s\"}",
                viewCount.getPromptTemplateId(),
                viewCount.getTotalViewCount(),
                viewCount.getCreatedAt().format(DATE_TIME_FORMATTER),
                viewCount.getUpdatedAt().format(DATE_TIME_FORMATTER));
        } catch (Exception e) {
            log.error("Failed to convert ViewCount to Redis value: {}", viewCount, e);
            throw ViewOperationException.redisSerializationFailed("ViewCount", e.getMessage());
        }
    }

    /**
     * ViewRecord 도메인 객체를 Redis 저장용 JSON 문자열로 변환합니다.
     */
    public String toRedisValue(ViewRecord viewRecord) {
        Assert.notNull(viewRecord, "ViewRecord must not be null");

        try {
            return String.format(
                "{\"id\":\"%s\",\"promptTemplateId\":%d,\"userId\":%s,\"ipAddress\":\"%s\",\"anonymousId\":\"%s\",\"viewedAt\":\"%s\"}",
                viewRecord.getId(),
                viewRecord.getPromptTemplateId(),
                viewRecord.getUserId() != null ? viewRecord.getUserId().toString() : "null",
                viewRecord.getIpAddress(),
                viewRecord.getAnonymousId() != null ? viewRecord.getAnonymousId() : "",
                viewRecord.getViewedAt().format(DATE_TIME_FORMATTER));
        } catch (Exception e) {
            log.error("Failed to convert ViewRecord to Redis value: {}", viewRecord, e);
            throw ViewOperationException.redisSerializationFailed("ViewRecord", e.getMessage());
        }
    }

    /**
     * Redis 값을 ViewCount 도메인 객체로 변환합니다.
     */
    public ViewCount fromRedisValue(String redisValue) {
        Assert.hasText(redisValue, "Redis value must not be empty");

        try {
            // JSON 파싱
            Long promptTemplateId = Long.parseLong(extractValue(redisValue, "promptTemplateId"));
            Long totalViewCount = Long.parseLong(extractValue(redisValue, "totalViewCount"));
            LocalDateTime createdAt = LocalDateTime.parse(extractValue(redisValue, "createdAt"), DATE_TIME_FORMATTER);
            LocalDateTime updatedAt = LocalDateTime.parse(extractValue(redisValue, "updatedAt"), DATE_TIME_FORMATTER);

            return ViewCount.builder()
                .promptTemplateId(promptTemplateId)
                .totalViewCount(totalViewCount)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
        } catch (Exception e) {
            log.error("Failed to convert Redis value to ViewCount: {}", redisValue, e);
            throw ViewOperationException.redisDeserializationFailed("ViewCount", e.getMessage());
        }
    }

    /**
     * Redis 값을 ViewRecord 도메인 객체로 변환합니다.
     */
    public ViewRecord fromRedisValueToViewRecord(String redisValue) {
        Assert.hasText(redisValue, "Redis value must not be empty");

        try {
            // JSON 파싱
            String id = extractValue(redisValue, "id");
            Long promptTemplateId = Long.parseLong(extractValue(redisValue, "promptTemplateId"));
            String userIdStr = extractValue(redisValue, "userId");
            Long userId = "null".equals(userIdStr) ? null : Long.parseLong(userIdStr);
            String ipAddress = extractValue(redisValue, "ipAddress");
            String anonymousId = extractValue(redisValue, "anonymousId");
            LocalDateTime viewedAt = LocalDateTime.parse(extractValue(redisValue, "viewedAt"), DATE_TIME_FORMATTER);

            // ViewIdentifier 생성
            ViewIdentifier viewIdentifier = ViewIdentifier.builder()
                .promptTemplateId(promptTemplateId)
                .userId(userId)
                .ipAddress(ipAddress)
                .anonymousId(anonymousId.isEmpty() ? null : anonymousId)
                .build();

            return ViewRecord.builder()
                .id(id)
                .viewIdentifier(viewIdentifier)
                .viewedAt(viewedAt)
                .build();
        } catch (Exception e) {
            log.error("Failed to convert Redis value to ViewRecord: {}", redisValue, e);
            throw ViewOperationException.redisDeserializationFailed("ViewRecord", e.getMessage());
        }
    }

    /**
     * JSON 문자열에서 특정 필드 값을 추출합니다.
     * 간단한 JSON 파싱 (성능상 Jackson 대신 사용)
     */
    private String extractValue(String json, String fieldName) {
        String pattern = "\"" + fieldName + "\":";
        int startIndex = json.indexOf(pattern);
        if (startIndex == -1) {
            throw new IllegalArgumentException("Field not found: " + fieldName);
        }

        startIndex += pattern.length();

        // 값이 문자열인 경우 ("로 감싸짐)
        if (json.charAt(startIndex) == '"') {
            startIndex++;
            int endIndex = json.indexOf('"', startIndex);
            return json.substring(startIndex, endIndex);
        }
        // 값이 숫자나 null인 경우
        else {
            int endIndex = json.indexOf(',', startIndex);
            if (endIndex == -1) {
                endIndex = json.indexOf('}', startIndex);
            }
            return json.substring(startIndex, endIndex).trim();
        }
    }
}
