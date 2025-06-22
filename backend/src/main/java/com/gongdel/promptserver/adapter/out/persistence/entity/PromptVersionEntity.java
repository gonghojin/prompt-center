package com.gongdel.promptserver.adapter.out.persistence.entity;

import com.gongdel.promptserver.domain.model.InputVariable;
import com.gongdel.promptserver.domain.model.PromptVersionActionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 프롬프트 템플릿의 버전 이력을 관리하는 JPA 엔티티입니다.
 */
@Entity
@Table(name = "prompt_versions", indexes = {
    @Index(name = "idx_prompt_version_template", columnList = "prompt_template_id"),
    @Index(name = "idx_prompt_version_uuid", columnList = "uuid", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
public class PromptVersionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_template_id", nullable = false)
    private PromptTemplateEntity promptTemplate;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT")
    private String changes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private UserEntity createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "input_variables", columnDefinition = "jsonb")
    private List<InputVariable> inputVariables;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private PromptVersionActionType actionType;

    // 매퍼/테스트용 ID 기반 생성자
    public PromptVersionEntity(Long id) {
        this.id = id;
    }

}
