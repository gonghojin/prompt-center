package com.gongdel.promptserver.adapter.out.persistence.mapper.impl;

import com.gongdel.promptserver.adapter.out.persistence.entity.TagEntity;
import com.gongdel.promptserver.adapter.out.persistence.mapper.TagMapper;
import com.gongdel.promptserver.domain.model.Tag;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TagMapper 인터페이스의 구현체
 */
@Component
public class TagMapperImpl implements TagMapper {

    /**
     * JPA 엔티티를 도메인 모델로 변환합니다.
     *
     * @param entity JPA 엔티티
     * @return 도메인 모델
     */
    @Override
    public Tag toDomain(TagEntity entity) {
        if (entity == null) {
            return null;
        }

        return Tag.of(
            entity.getId(),
            entity.getName(),
            entity.getCreatedAt(),
            entity.getUpdatedAt());
    }

    /**
     * 도메인 모델을 JPA 엔티티로 변환합니다.
     *
     * @param domain 도메인 모델
     * @return JPA 엔티티
     */
    @Override
    public TagEntity toEntity(Tag domain) {
        if (domain == null) {
            return null;
        }
        return TagEntity.create(
            domain.getId(),
            domain.getName(),
            domain.getCreatedAt(),
            domain.getUpdatedAt());
    }

    /**
     * JPA 엔티티 리스트를 도메인 모델 세트로 변환합니다.
     *
     * @param entities JPA 엔티티 리스트
     * @return 도메인 모델 세트
     */
    @Override
    public Set<Tag> toDomainSet(List<TagEntity> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
            .map(this::toDomain)
            .collect(Collectors.toSet());
    }
}
