package com.gongdel.promptserver.adapter.out.persistence.entity.like;

import com.gongdel.promptserver.adapter.out.persistence.entity.BaseJpaEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.PromptTemplateEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"user", "promptTemplate"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "prompt_like", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id",
    "prompt_template_id"}), indexes = {
    @Index(name = "idx_prompt_like_prompt_template_id", columnList = "prompt_template_id"),
    @Index(name = "idx_prompt_like_user_id_created_at", columnList = "user_id, created_at")
})
public class PromptLikeEntity extends BaseJpaEntity {

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
    public PromptLikeEntity(UserEntity user, PromptTemplateEntity promptTemplate) {
        Assert.notNull(user, "User must not be null");
        Assert.notNull(promptTemplate, "PromptTemplate must not be null");
        this.user = user;
        this.promptTemplate = promptTemplate;
    }
}
