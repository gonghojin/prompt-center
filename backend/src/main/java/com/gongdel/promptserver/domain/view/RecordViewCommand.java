package com.gongdel.promptserver.domain.view;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 프롬프트 조회 기록 요청 커맨드 객체입니다.
 * <p>
 * 로그인/비로그인 사용자 구분하여 조회 기록을 요청합니다.
 * 외부 API에서 받은 UUID를 내부 Long ID로 변환하는 책임을 가집니다.
 */
@Getter
@ToString
public class RecordViewCommand {
    /**
     * 사용자 ID (로그인 사용자만, nullable)
     */
    private final Long userId;

    /**
     * 프롬프트 UUID (외부 API에서 받은 값)
     */
    private final UUID promptTemplateUuid;

    /**
     * 프롬프트 템플릿 ID (내부 DB PK, UUID 변환 후 설정)
     */
    private final Long promptTemplateId;

    /**
     * IP 주소
     */
    private final String ipAddress;

    /**
     * 익명 사용자 식별자 (비로그인 사용자용, nullable)
     */
    private final String anonymousId;

    /**
     * RecordViewCommand 객체를 생성합니다.
     *
     * @param userId             사용자 ID (로그인 사용자만)
     * @param promptTemplateUuid 프롬프트 UUID (필수)
     * @param promptTemplateId   프롬프트 템플릿 ID (필수)
     * @param ipAddress          IP 주소 (필수)
     * @param anonymousId        익명 사용자 식별자 (비로그인 사용자용)
     */
    @Builder
    private RecordViewCommand(Long userId, UUID promptTemplateUuid, Long promptTemplateId,
                              String ipAddress, String anonymousId) {
        Assert.notNull(promptTemplateUuid, "promptTemplateUuid must not be null");
        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");
        Assert.hasText(ipAddress, "ipAddress must not be blank");

        this.userId = userId;
        this.promptTemplateUuid = promptTemplateUuid;
        this.promptTemplateId = promptTemplateId;
        this.ipAddress = ipAddress;
        this.anonymousId = anonymousId;
    }

    /**
     * 로그인 사용자용 조회 기록 커맨드 생성 팩토리 메서드
     *
     * @param userId             사용자 ID (필수)
     * @param promptTemplateUuid 프롬프트 UUID (필수)
     * @param promptTemplateId   프롬프트 템플릿 ID (필수)
     * @param ipAddress          IP 주소 (필수)
     * @return RecordViewCommand 객체
     */
    public static RecordViewCommand forUser(Long userId, UUID promptTemplateUuid,
                                            Long promptTemplateId, String ipAddress) {
        Assert.notNull(userId, "userId must not be null");

        return RecordViewCommand.builder()
            .userId(userId)
            .promptTemplateUuid(promptTemplateUuid)
            .promptTemplateId(promptTemplateId)
            .ipAddress(ipAddress)
            .build();
    }

    /**
     * 비로그인 사용자용 조회 기록 커맨드 생성 팩토리 메서드
     *
     * @param promptTemplateUuid 프롬프트 UUID (필수)
     * @param promptTemplateId   프롬프트 템플릿 ID (필수)
     * @param ipAddress          IP 주소 (필수)
     * @param anonymousId        익명 사용자 식별자 (필수)
     * @return RecordViewCommand 객체
     */
    public static RecordViewCommand forGuest(UUID promptTemplateUuid, Long promptTemplateId,
                                             String ipAddress, String anonymousId) {
        Assert.hasText(anonymousId, "anonymousId must not be blank");

        return RecordViewCommand.builder()
            .promptTemplateUuid(promptTemplateUuid)
            .promptTemplateId(promptTemplateId)
            .ipAddress(ipAddress)
            .anonymousId(anonymousId)
            .build();
    }

    /**
     * 프롬프트 템플릿 ID를 반환합니다. (어댑터 호환성을 위한 메서드)
     *
     * @return 프롬프트 템플릿 ID (Long)
     */
    public Long getPromptTemplateId() {
        return promptTemplateId;
    }

    /**
     * ViewRecord 도메인 객체로 변환합니다.
     */
    public ViewRecord toViewRecord() {
        ViewIdentifier identifier = ViewIdentifier.builder()
            .promptTemplateId(promptTemplateId)
            .userId(userId)
            .ipAddress(ipAddress)
            .anonymousId(anonymousId)
            .build();

        return ViewRecord.builder()
            .viewIdentifier(identifier)
            .viewedAt(LocalDateTime.now())
            .build();
    }

    /**
     * 로그인 사용자인지 확인합니다.
     *
     * @return 로그인 사용자인 경우 true
     */
    public boolean isLoggedInUser() {
        return userId != null;
    }

    /**
     * 사용자 식별자를 반환합니다. (로그 및 디버깅용)
     *
     * @return 사용자 식별자 문자열
     */
    public String getUserIdentifier() {
        if (isLoggedInUser()) {
            return "user:" + userId;
        } else if (StringUtils.hasText(anonymousId)) {
            return "anon:" + anonymousId;
        } else {
            return "ip:" + ipAddress;
        }
    }
}
