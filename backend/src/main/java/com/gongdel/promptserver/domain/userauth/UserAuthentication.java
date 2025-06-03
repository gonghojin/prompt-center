package com.gongdel.promptserver.domain.userauth;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import org.springframework.util.Assert;
import com.gongdel.promptserver.domain.model.BaseTimeEntity;

/**
 * 사용자 인증 정보를 나타내는 도메인 모델입니다.
 *
 * @author AI
 */
@Getter
@ToString
@EqualsAndHashCode
public class UserAuthentication extends BaseTimeEntity {
    private final Long id;
    private final Long userId;
    private final String passwordHash;
    private final LocalDateTime lastPasswordChangeAt;

    @Builder
    public UserAuthentication(Long id, Long userId, String passwordHash, LocalDateTime lastPasswordChangeAt,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        Assert.notNull(userId, "userId must not be null");
        this.id = id;
        this.userId = userId;
        this.passwordHash = passwordHash;
        this.lastPasswordChangeAt = lastPasswordChangeAt;
    }

    /**
     * UserAuthentication 최초 등록을 위한 정적 팩토리 메서드
     */
    public static UserAuthentication register(Long userId, String passwordHash, LocalDateTime lastPasswordChangeAt) {
        return new UserAuthentication(
                null, // id (DB에서 생성)
                userId,
                passwordHash,
                lastPasswordChangeAt,
                LocalDateTime.now(),
                LocalDateTime.now());
    }
}
