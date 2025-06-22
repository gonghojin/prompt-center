package com.gongdel.promptserver.domain.view;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 프롬프트 조회 기록을 나타내는 불변 도메인 객체입니다.
 * <p>
 * 인프라스트럭처 관심사(Redis 키 등)를 제거하고
 * 순수한 비즈니스 데이터만을 관리합니다.
 */
@Getter
@ToString
@Builder
public class ViewRecord {

    /**
     * 조회 기록 고유 식별자
     */
    private final String id;

    /**
     * 조회 식별 정보
     */
    private final ViewIdentifier viewIdentifier;

    /**
     * 조회 일시
     */
    private final LocalDateTime viewedAt;

    @Builder
    public ViewRecord(String id, ViewIdentifier viewIdentifier, LocalDateTime viewedAt) {
        Assert.notNull(viewIdentifier, "viewIdentifier must not be null");
        Assert.notNull(viewedAt, "viewedAt must not be null");

        this.id = id == null ? UUID.randomUUID().toString() : id;
        this.viewIdentifier = viewIdentifier;
        this.viewedAt = viewedAt;
    }

    /**
     * 로그인 사용자용 조회 기록 생성 팩토리 메서드
     */
    public static ViewRecord forUser(Long promptTemplateId, Long userId,
                                     String ipAddress, LocalDateTime viewedAt) {
        ViewIdentifier identifier = ViewIdentifier.builder()
            .promptTemplateId(promptTemplateId)
            .userId(userId)
            .ipAddress(ipAddress)
            .build();

        return ViewRecord.builder()
            .viewIdentifier(identifier)
            .viewedAt(viewedAt)
            .build();
    }

    /**
     * 비로그인 사용자용 조회 기록 생성 팩토리 메서드
     */
    public static ViewRecord forGuest(Long promptTemplateId, String ipAddress,
                                      String anonymousId, LocalDateTime viewedAt) {
        ViewIdentifier identifier = ViewIdentifier.builder()
            .promptTemplateId(promptTemplateId)
            .ipAddress(ipAddress)
            .anonymousId(anonymousId)
            .build();

        return ViewRecord.builder()
            .viewIdentifier(identifier)
            .viewedAt(viewedAt)
            .build();
    }

    /**
     * 로그인 사용자 여부를 확인합니다.
     */
    public boolean isLoggedInUser() {
        return viewIdentifier.isLoggedInUser();
    }

    /**
     * 사용자 식별 타입을 반환합니다.
     */
    public ViewIdentifier.ViewerType getViewerType() {
        return viewIdentifier.getViewerType();
    }

    /**
     * 프롬프트 템플릿 ID를 반환합니다. (편의 메서드)
     */
    public Long getPromptTemplateId() {
        return viewIdentifier.getPromptTemplateId();
    }

    /**
     * 사용자 ID를 반환합니다. (편의 메서드)
     */
    public Long getUserId() {
        return viewIdentifier.getUserId();
    }

    /**
     * IP 주소를 반환합니다. (편의 메서드)
     */
    public String getIpAddress() {
        return viewIdentifier.getIpAddress();
    }

    /**
     * 익명 사용자 ID를 반환합니다. (편의 메서드)
     */
    public String getAnonymousId() {
        return viewIdentifier.getAnonymousId();
    }
}
