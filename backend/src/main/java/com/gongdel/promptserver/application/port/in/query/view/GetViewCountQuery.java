package com.gongdel.promptserver.application.port.in.query.view;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * 프롬프트 조회수 조회 쿼리 객체입니다.
 * 외부 UUID와 내부 Long ID 모두 지원합니다.
 */
@Getter
public class GetViewCountQuery {
    /**
     * 프롬프트 템플릿 UUID (외부 노출용)
     */
    private final UUID promptTemplateUuid;

    /**
     * 프롬프트 템플릿 ID (내부 처리용)
     */
    private final Long promptTemplateId;

    @Builder
    private GetViewCountQuery(UUID promptTemplateUuid, Long promptTemplateId) {
        // UUID 또는 ID 중 하나는 반드시 있어야 함
        if (promptTemplateUuid == null && promptTemplateId == null) {
            throw new IllegalArgumentException("Either promptTemplateUuid or promptTemplateId must be provided");
        }

        this.promptTemplateUuid = promptTemplateUuid;
        this.promptTemplateId = promptTemplateId;
    }

    /**
     * UUID 기반 쿼리 생성 팩토리 메서드 (외부 API용)
     */
    public static GetViewCountQuery byUuid(UUID promptTemplateUuid) {
        Assert.notNull(promptTemplateUuid, "promptTemplateUuid must not be null");
        return GetViewCountQuery.builder()
            .promptTemplateUuid(promptTemplateUuid)
            .build();
    }

    /**
     * ID 기반 쿼리 생성 팩토리 메서드 (내부 처리용)
     */
    public static GetViewCountQuery byId(Long promptTemplateId) {
        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");
        return GetViewCountQuery.builder()
            .promptTemplateId(promptTemplateId)
            .build();
    }

    /**
     * UUID 기반 쿼리인지 확인합니다.
     */
    public boolean isUuidBased() {
        return promptTemplateUuid != null;
    }

    /**
     * ID 기반 쿼리인지 확인합니다.
     */
    public boolean isIdBased() {
        return promptTemplateId != null;
    }
}
