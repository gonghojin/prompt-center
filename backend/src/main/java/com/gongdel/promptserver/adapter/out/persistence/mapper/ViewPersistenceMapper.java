package com.gongdel.promptserver.adapter.out.persistence.mapper;

import com.gongdel.promptserver.adapter.out.persistence.entity.view.PromptViewCountEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.view.PromptViewLogEntity;
import com.gongdel.promptserver.domain.view.ViewCount;
import com.gongdel.promptserver.domain.view.ViewIdentifier;
import com.gongdel.promptserver.domain.view.ViewRecord;
import org.springframework.stereotype.Component;

/**
 * 조회수 관련 도메인 객체와 엔티티 간 변환을 담당하는 매퍼
 */
@Component
public class ViewPersistenceMapper {

    /**
     * ViewRecord 도메인 객체를 PromptViewLogEntity로 변환합니다.
     *
     * @param viewRecord 도메인 객체
     * @return 엔티티 객체
     */
    public PromptViewLogEntity toEntity(ViewRecord viewRecord) {
        if (viewRecord == null) {
            return null;
        }

        return PromptViewLogEntity.builder()
            .id(viewRecord.getId())
            .promptTemplateId(viewRecord.getPromptTemplateId())
            .userId(viewRecord.getUserId())
            .ipAddress(viewRecord.getIpAddress())
            .anonymousId(viewRecord.getAnonymousId())
            .viewedAt(viewRecord.getViewedAt())
            .build();
    }

    /**
     * PromptViewLogEntity를 ViewRecord 도메인 객체로 변환합니다.
     *
     * @param entity 엔티티 객체
     * @return 도메인 객체
     */
    public ViewRecord toDomain(PromptViewLogEntity entity) {
        if (entity == null) {
            return null;
        }

        ViewIdentifier viewIdentifier = ViewIdentifier.builder()
            .promptTemplateId(entity.getPromptTemplateId())
            .userId(entity.getUserId())
            .ipAddress(entity.getIpAddress())
            .anonymousId(entity.getAnonymousId())
            .build();

        return ViewRecord.builder()
            .id(entity.getId())
            .viewIdentifier(viewIdentifier)
            .viewedAt(entity.getViewedAt())
            .build();
    }

    /**
     * ViewCount 도메인 객체를 PromptViewCountEntity로 변환합니다.
     *
     * @param viewCount 도메인 객체
     * @return 엔티티 객체
     */
    public PromptViewCountEntity toEntity(ViewCount viewCount) {
        if (viewCount == null) {
            return null;
        }

        PromptViewCountEntity entity = PromptViewCountEntity.builder()
            .promptTemplateId(viewCount.getPromptTemplateId())
            .totalViewCount(viewCount.getTotalViewCount())
            .build();

        // BaseJpaEntity의 생성/수정 시간 설정
        if (viewCount.getCreatedAt() != null) {
            entity.setCreatedAt(viewCount.getCreatedAt());
        }
        if (viewCount.getUpdatedAt() != null) {
            entity.setUpdatedAt(viewCount.getUpdatedAt());
        }

        return entity;
    }

    /**
     * PromptViewCountEntity를 ViewCount 도메인 객체로 변환합니다.
     *
     * @param entity 엔티티 객체
     * @return 도메인 객체
     */
    public ViewCount toDomain(PromptViewCountEntity entity) {
        if (entity == null) {
            return null;
        }

        return ViewCount.builder()
            .promptTemplateId(entity.getPromptTemplateId())
            .totalViewCount(entity.getTotalViewCount())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}
