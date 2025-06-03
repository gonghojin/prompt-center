package com.gongdel.promptserver.domain.user;

import com.gongdel.promptserver.domain.model.BaseTimeEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 사용자-역할 매핑 도메인 모델
 *
 * <p>
 * 사용자와 역할 간의 다대다 관계를 표현합니다. 한 사용자는 여러 역할을 가질 수 있으며, 한 역할도 여러 사용자에게 할당될 수 있습니다.
 * </p>
 *
 * @author AI
 */
@Getter
@ToString
@EqualsAndHashCode
public class UserRole extends BaseTimeEntity {
    /**
     * 매핑 고유 식별자 (PK)
     */
    private final Long id;
    /**
     * 매핑 UUID (글로벌 유일)
     */
    private final UserRoleId uuid;
    /**
     * 사용자 ID (User FK)
     */
    private final Long userId;
    /**
     * 역할 ID (Role FK)
     */
    private final Long roleId;

    @Builder
    public UserRole(Long id, UserRoleId uuid, Long userId, Long roleId, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        if (userId == null)
            throw new IllegalArgumentException("userId must not be null");
        if (roleId == null)
            throw new IllegalArgumentException("roleId must not be null");
        this.id = id;
        this.uuid = uuid != null ? uuid : UserRoleId.randomId();
        this.userId = userId;
        this.roleId = roleId;
    }

    public static UserRole register(Long userId, Long roleId) {
        return new UserRole(null, null, userId, roleId, LocalDateTime.now(), LocalDateTime.now());
    }
}
