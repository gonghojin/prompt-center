package com.gongdel.promptserver.domain.role;

import com.gongdel.promptserver.domain.model.BaseTimeEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 시스템 역할 도메인 모델
 */
@Getter
@ToString(of = { "roleId", "name", "description" })
@EqualsAndHashCode(of = { "roleId" })
public class Role extends BaseTimeEntity {
    /**
     * DB PK
     */
    private final Long id;
    /**
     * 비즈니스 식별자 (UUID)
     */
    private final RoleId roleId;
    private final String name;
    private final String description;

    @Builder
    public Role(Long id, RoleId roleId, String name, String description, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Role name is required");
        }
        this.id = id;
        this.roleId = roleId != null ? roleId : RoleId.randomId();
        this.name = name;
        this.description = description;
    }

    public static Role register(String name, String description) {
        return new Role(null, null, name, description, LocalDateTime.now(), LocalDateTime.now());
    }
}
