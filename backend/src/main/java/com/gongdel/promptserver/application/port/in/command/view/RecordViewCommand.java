package com.gongdel.promptserver.application.port.in.command.view;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * 프롬프트 조회 기록 요청 커맨드 객체입니다.
 * 로그인 사용자와 비로그인 사용자 모두 지원합니다.
 */
@Getter
public class RecordViewCommand {
    /**
     * 프롬프트 템플릿 UUID (외부 노출용)
     */
    private final UUID promptTemplateUuid;

    /**
     * 사용자 ID (로그인 사용자인 경우, null 가능)
     */
    private final Long userId;

    /**
     * IP 주소 (비로그인 사용자 식별용)
     */
    private final String ipAddress;

    /**
     * 익명 사용자 ID (쿠키 기반 식별용)
     */
    private final String anonymousId;

    @Builder
    private RecordViewCommand(UUID promptTemplateUuid, Long userId, String ipAddress, String anonymousId) {
        Assert.notNull(promptTemplateUuid, "promptTemplateUuid must not be null");
        Assert.hasText(ipAddress, "ipAddress must not be blank");

        // 로그인 사용자 또는 익명 사용자 중 하나는 반드시 있어야 함
        if (userId == null && (anonymousId == null || anonymousId.trim().isEmpty())) {
            throw new IllegalArgumentException("Either userId or anonymousId must be provided");
        }

        this.promptTemplateUuid = promptTemplateUuid;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.anonymousId = anonymousId;
    }

    /**
     * 로그인 사용자용 커맨드 생성 팩토리 메서드
     */
    public static RecordViewCommand forUser(UUID promptTemplateUuid, Long userId, String ipAddress) {
        return RecordViewCommand.builder()
            .promptTemplateUuid(promptTemplateUuid)
            .userId(userId)
            .ipAddress(ipAddress)
            .build();
    }

    /**
     * 비로그인 사용자용 커맨드 생성 팩토리 메서드
     */
    public static RecordViewCommand forGuest(UUID promptTemplateUuid, String ipAddress, String anonymousId) {
        return RecordViewCommand.builder()
            .promptTemplateUuid(promptTemplateUuid)
            .ipAddress(ipAddress)
            .anonymousId(anonymousId)
            .build();
    }

    /**
     * 로그인 사용자인지 확인합니다.
     */
    public boolean isLoggedInUser() {
        return userId != null;
    }

    /**
     * 익명 사용자인지 확인합니다.
     */
    public boolean isAnonymousUser() {
        return userId == null && anonymousId != null && !anonymousId.trim().isEmpty();
    }
}
