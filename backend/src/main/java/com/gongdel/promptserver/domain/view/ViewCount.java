package com.gongdel.promptserver.domain.view;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 프롬프트 조회수 정보를 나타내는 불변 값 객체입니다.
 */
@Getter
@ToString
@EqualsAndHashCode
@Builder
public class ViewCount {
    /**
     * 프롬프트 템플릿 ID (내부 DB PK)
     */
    private final Long promptTemplateId;

    /**
     * 총 조회수
     */
    private final Long totalViewCount;

    /**
     * 생성일시
     */
    private final LocalDateTime createdAt;

    /**
     * 수정일시
     */
    private final LocalDateTime updatedAt;

    /**
     * ViewCount 객체를 생성합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID (필수)
     * @param totalViewCount   총 조회수 (0 이상)
     * @param createdAt        생성일시 (필수)
     * @param updatedAt        수정일시 (필수)
     */
    @Builder
    public ViewCount(Long promptTemplateId, Long totalViewCount,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");
        Assert.notNull(totalViewCount, "totalViewCount must not be null");
        Assert.isTrue(totalViewCount >= 0, "totalViewCount must be non-negative");
        Assert.notNull(createdAt, "createdAt must not be null");
        Assert.notNull(updatedAt, "updatedAt must not be null");

        this.promptTemplateId = promptTemplateId;
        this.totalViewCount = totalViewCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 초기 조회수 1로 ViewCount 객체를 생성합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 초기 ViewCount 객체 (조회수 1)
     */
    public static ViewCount createInitial(Long promptTemplateId) {
        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");

        LocalDateTime now = LocalDateTime.now();
        return ViewCount.builder()
            .promptTemplateId(promptTemplateId)
            .totalViewCount(1L)
            .createdAt(now)
            .updatedAt(now)
            .build();
    }

    /**
     * 조회수 0으로 ViewCount 객체를 생성합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 조회수 0인 ViewCount 객체
     */
    public static ViewCount createEmpty(Long promptTemplateId) {
        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");

        LocalDateTime now = LocalDateTime.now();
        return ViewCount.builder()
            .promptTemplateId(promptTemplateId)
            .totalViewCount(0L)
            .createdAt(now)
            .updatedAt(now)
            .build();
    }

    /**
     * 조회수를 1 증가시킨 새로운 ViewCount 객체를 반환합니다.
     *
     * @return 조회수가 1 증가된 새로운 ViewCount 객체
     */
    public ViewCount increment() {
        return ViewCount.builder()
            .promptTemplateId(promptTemplateId)
            .totalViewCount(totalViewCount + 1)
            .createdAt(createdAt)
            .updatedAt(LocalDateTime.now())
            .build();
    }

    /**
     * 조회수를 지정된 값만큼 증가시킨 새로운 ViewCount 객체를 반환합니다.
     *
     * @param incrementBy 증가시킬 조회수 (1 이상)
     * @return 조회수가 증가된 새로운 ViewCount 객체
     */
    public ViewCount incrementBy(long incrementBy) {
        Assert.isTrue(incrementBy > 0, "incrementBy must be positive");

        return ViewCount.builder()
            .promptTemplateId(promptTemplateId)
            .totalViewCount(totalViewCount + incrementBy)
            .createdAt(createdAt)
            .updatedAt(LocalDateTime.now())
            .build();
    }
}
