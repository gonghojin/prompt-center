package com.gongdel.promptserver.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 프롬프트 템플릿과 태그 간의 다대다 관계를 관리하는 중간 테이블 엔티티입니다. 이 엔티티는 다대다 관계의 주인으로, 양방향 관계를 명시적으로 관리합니다.
 */
@Entity
@Table(name = "prompt_template_tags", uniqueConstraints = @UniqueConstraint(columnNames = {"prompt_template_id",
    "tag_id"}, name = "uq_prompt_template_tag"))
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class PromptTemplateTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 프롬프트 템플릿과의 다대일 관계 이 엔티티가 관계의 주인입니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_template_id", nullable = false)
    private PromptTemplateEntity promptTemplate;

    /**
     * 태그와의 다대일 관계 이 엔티티가 관계의 주인입니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private TagEntity tag;

    /**
     * 생성 시간
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 생성자
     *
     * @param promptTemplate 프롬프트 템플릿
     * @param tag            태그
     */
    public PromptTemplateTagEntity(PromptTemplateEntity promptTemplate, TagEntity tag) {
        this.promptTemplate = promptTemplate;
        this.tag = tag;
        this.createdAt = LocalDateTime.now();
    }
}
