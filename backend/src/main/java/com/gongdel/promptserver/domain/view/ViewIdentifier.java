package com.gongdel.promptserver.domain.view;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 조회 기록 식별을 위한 도메인 객체입니다.
 * <p>
 * Redis 키 생성 로직을 도메인에서 분리하고,
 * 순수한 비즈니스 식별 정보만을 담당합니다.
 */
@Getter
@ToString
@Builder
public class ViewIdentifier {

    private final Long promptTemplateId;
    private final Long userId;
    private final String ipAddress;
    private final String anonymousId;

    @Builder
    public ViewIdentifier(Long promptTemplateId, Long userId, String ipAddress, String anonymousId) {
        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");
        Assert.hasText(ipAddress, "ipAddress must not be blank");

        this.promptTemplateId = promptTemplateId;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.anonymousId = anonymousId;
    }

    /**
     * 로그인 사용자 여부를 확인합니다.
     */
    public boolean isLoggedInUser() {
        return userId != null;
    }

    /**
     * 익명 사용자 식별자 존재 여부를 확인합니다.
     */
    public boolean hasAnonymousId() {
        return StringUtils.hasText(anonymousId);
    }

    /**
     * 사용자 식별 타입을 반환합니다.
     */
    public ViewerType getViewerType() {
        if (isLoggedInUser()) {
            return ViewerType.AUTHENTICATED_USER;
        } else if (hasAnonymousId()) {
            return ViewerType.ANONYMOUS_USER;
        } else {
            return ViewerType.IP_BASED_USER;
        }
    }

    /**
     * 사용자 식별자를 반환합니다. (로그 및 디버깅용)
     */
    public String getUserIdentifier() {
        if (isLoggedInUser()) {
            return "user:" + userId;
        } else if (hasAnonymousId()) {
            return "anon:" + anonymousId;
        } else {
            return "ip:" + ipAddress;
        }
    }

    public enum ViewerType {
        AUTHENTICATED_USER,
        ANONYMOUS_USER,
        IP_BASED_USER
    }
}
