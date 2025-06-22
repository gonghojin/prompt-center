package com.gongdel.promptserver.adapter.out.persistence.mapper;

import com.gongdel.promptserver.adapter.out.persistence.entity.TagEntity;
import com.gongdel.promptserver.domain.model.Tag;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Tag 도메인 모델과 JPA 엔티티 간의 변환을 담당하는 매퍼
 */
@Component
public interface TagMapper {

    /**
     * JPA 엔티티를 도메인 모델로 변환합니다.
     *
     * @param entity JPA 엔티티
     * @return 도메인 모델
     */
    Tag toDomain(TagEntity entity);

    /**
     * 도메인 모델을 JPA 엔티티로 변환합니다.
     *
     * @param domain 도메인 모델
     * @return JPA 엔티티
     */
    TagEntity toEntity(Tag domain);

    /**
     * JPA 엔티티 리스트를 도메인 모델 세트로 변환합니다.
     *
     * @param entities JPA 엔티티 리스트
     * @return 도메인 모델 세트
     */
    Set<Tag> toDomainSet(List<TagEntity> entities);
}
