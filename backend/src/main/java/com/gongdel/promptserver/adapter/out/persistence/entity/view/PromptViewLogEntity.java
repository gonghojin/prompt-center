package com.gongdel.promptserver.adapter.out.persistence.entity.view;

import com.gongdel.promptserver.adapter.out.persistence.entity.BaseJpaEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 프롬프트 조회 로그 엔티티
 * 조회 기록을 저장하여 중복 체크 및 통계 분석에 활용합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "prompt_view_logs", indexes = {
    @Index(name = "idx_prompt_view_logs_prompt_template_id", columnList = "prompt_template_id"),
    @Index(name = "idx_prompt_view_logs_user_id_viewed_at", columnList = "user_id, viewed_at"),
    @Index(name = "idx_prompt_view_logs_ip_address_viewed_at", columnList = "ip_address, viewed_at"),
    @Index(name = "idx_prompt_view_logs_viewed_at", columnList = "viewed_at"),
    @Index(name = "idx_prompt_view_logs_duplicate_check_user", columnList = "prompt_template_id, user_id, viewed_at"),
    @Index(name = "idx_prompt_view_logs_duplicate_check_ip", columnList = "prompt_template_id, ip_address, viewed_at"),
    @Index(name = "idx_prompt_view_logs_duplicate_check_anonymous", columnList = "prompt_template_id, anonymous_id, viewed_at")
})
public class PromptViewLogEntity extends BaseJpaEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "prompt_template_id", nullable = false)
    private Long promptTemplateId;

    @Column(name = "user_id")
    private Long userId; // nullable for anonymous users

    @Column(name = "ip_address", length = 45)
    private String ipAddress; // IPv6 support

    @Column(name = "anonymous_id", length = 36)
    private String anonymousId; // for anonymous user tracking

    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;

    @Builder
    public PromptViewLogEntity(String id, Long promptTemplateId, Long userId,
                               String ipAddress, String anonymousId, LocalDateTime viewedAt) {
        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");
        Assert.notNull(viewedAt, "viewedAt must not be null");

        this.id = id;
        this.promptTemplateId = promptTemplateId;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.anonymousId = anonymousId;
        this.viewedAt = viewedAt;
    }

    /**
     * 로그인 사용자 여부를 확인합니다.
     *
     * @return 로그인 사용자인 경우 true
     */
    public boolean isLoggedInUser() {
        return userId != null;
    }
}
