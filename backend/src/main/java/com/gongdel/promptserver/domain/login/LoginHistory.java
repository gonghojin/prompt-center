package com.gongdel.promptserver.domain.login;

import com.gongdel.promptserver.domain.model.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자의 로그인 이력을 관리하는 도메인 모델입니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginHistory extends BaseTimeEntity {
    private Long id;
    private LoginHistoryId loginHistoryId;
    private Long userId;
    private LocalDateTime loginAt;
    private String ipAddress;
    private String userAgent;
    private LoginStatus status;

    @Builder
    public LoginHistory(
            Long id,
            LoginHistoryId loginHistoryId,
            Long userId,
            LocalDateTime loginAt,
            String ipAddress,
            String userAgent,
            LoginStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.loginHistoryId = loginHistoryId != null ? loginHistoryId : LoginHistoryId.randomId();
        this.userId = userId;
        this.loginAt = loginAt != null ? loginAt : LocalDateTime.now();
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.status = status != null ? status : LoginStatus.SUCCESS;
    }

    /**
     * 로그인 이력을 생성하는 정적 팩토리 메서드
     */
    public static LoginHistory create(
            Long userId,
            String ipAddress,
            String userAgent,
            LoginStatus status) {
        return new LoginHistory(
                null,
                null,
                userId,
                null,
                ipAddress,
                userAgent,
                status,
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    /**
     * 로그인 상태를 정의하는 열거형
     */
    public enum LoginStatus {
        SUCCESS, FAILED
    }
}
