package com.gongdel.promptserver.adapter.out.persistence.entity;

import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.model.Visibility;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 프롬프트 템플릿을 저장하는 JPA 엔티티입니다.
 */
@Entity
@Table(name = "prompt_templates", indexes = {
    @Index(name = "idx_prompt_category", columnList = "category_id"),
    @Index(name = "idx_prompt_created_by", columnList = "created_by_id"),
    @Index(name = "idx_prompt_status", columnList = "status"),
    @Index(name = "idx_prompt_visibility", columnList = "visibility"),
    @Index(name = "idx_prompt_uuid", columnList = "uuid", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
public class PromptTemplateEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private UserEntity createdBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromptStatus status;

    @Column(name = "current_version_id")
    private Long currentVersionId;

    @OneToMany(mappedBy = "promptTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PromptTemplateTagEntity> tagRelations = new ArrayList<>();

    // 매퍼/테스트용 ID 기반 생성자
    public PromptTemplateEntity(Long id) {
        this.id = id;
    }

    /**
     * 태그를 추가합니다.
     *
     * @param tag 추가할 태그
     */
    public void addTag(TagEntity tag) {
        PromptTemplateTagEntity relation = new PromptTemplateTagEntity(this, tag);
        tagRelations.add(relation);
    }

    /**
     * 태그를 제거합니다.
     *
     * @param tag 제거할 태그
     */
    public void removeTag(TagEntity tag) {
        tagRelations.removeIf(relation -> relation.getTag().equals(tag));
    }

    /**
     * 모든 태그를 제거합니다.
     */
    public void clearTags() {
        tagRelations.clear();
    }

    /**
     * 태그 목록을 가져옵니다.
     *
     * @return 태그 엔티티 목록
     */
    public Set<TagEntity> getTags() {
        return tagRelations.stream()
            .map(PromptTemplateTagEntity::getTag)
            .collect(Collectors.toSet());
    }
}
