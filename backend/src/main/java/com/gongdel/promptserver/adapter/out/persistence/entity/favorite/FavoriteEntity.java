package com.gongdel.promptserver.adapter.out.persistence.entity.favorite;

import com.gongdel.promptserver.adapter.out.persistence.entity.BaseJpaEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.PromptTemplateEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

/**
 * 프롬프트 즐겨찾기 정보를 저장하는 JPA 엔티티입니다.
 */
@Entity
@Table(name = "favorites", uniqueConstraints = {@UniqueConstraint(name = "uk_favorite_user_prompt", columnNames = {
    "user_id", "prompt_template_id"})}, indexes = {
    @Index(name = "idx_favorite_user", columnList = "user_id"),
    @Index(name = "idx_favorite_prompt", columnList = "prompt_template_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prompt_template_id", nullable = false)
    private PromptTemplateEntity promptTemplate;

    @Builder
    public FavoriteEntity(UserEntity user, PromptTemplateEntity promptTemplate) {
        Assert.notNull(user, "User must not be null");
        Assert.notNull(promptTemplate, "PromptTemplate must not be null");
        this.user = user;
        this.promptTemplate = promptTemplate;
    }
}
