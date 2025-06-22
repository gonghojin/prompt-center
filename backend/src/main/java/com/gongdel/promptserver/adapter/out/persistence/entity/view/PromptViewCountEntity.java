package com.gongdel.promptserver.adapter.out.persistence.entity.view;

import com.gongdel.promptserver.adapter.out.persistence.entity.BaseJpaEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

/**
 * 프롬프트 조회수 집계 엔티티
 * 프롬프트별 총 조회수를 관리합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode(of = "promptTemplateId")
@Entity
@Table(name = "prompt_view_counts", indexes = {
    @Index(name = "idx_prompt_view_counts_total_view_count", columnList = "total_view_count DESC"),
    @Index(name = "idx_prompt_view_counts_updated_at", columnList = "updated_at DESC")
})
public class PromptViewCountEntity extends BaseJpaEntity {

    @Id
    @Column(name = "prompt_template_id")
    private Long promptTemplateId;

    @Column(name = "total_view_count", nullable = false)
    private Long totalViewCount;

    @Builder
    public PromptViewCountEntity(Long promptTemplateId, Long totalViewCount) {
        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");

        this.promptTemplateId = promptTemplateId;
        this.totalViewCount = totalViewCount == null ? 0L : totalViewCount;
    }

    /**
     * 초기 조회수 엔티티를 생성합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 초기 조회수 엔티티 (조회수 1)
     */
    public static PromptViewCountEntity createInitial(Long promptTemplateId) {
        return PromptViewCountEntity.builder()
            .promptTemplateId(promptTemplateId)
            .totalViewCount(1L)
            .build();
    }

    /**
     * 조회수를 1 증가시킵니다.
     */
    public void incrementViewCount() {
        this.totalViewCount++;
    }

    /**
     * 조회수를 지정된 값만큼 증가시킵니다.
     *
     * @param count 증가시킬 조회수
     */
    public void incrementViewCount(long count) {
        Assert.isTrue(count > 0, "count must be positive");
        this.totalViewCount += count;
    }
}
