package com.gongdel.promptserver.adapter.out.persistence.mapper;

import com.gongdel.promptserver.adapter.out.persistence.entity.PromptTemplateEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.TagEntity;
import com.gongdel.promptserver.domain.model.PromptTemplate;
import com.gongdel.promptserver.domain.model.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * PromptTemplate 도메인 모델과 JPA 엔티티 간의 변환을 담당하는 매퍼
 */
@Mapper(componentModel = "spring")
public interface PromptTemplateMapper {

    /**
     * JPA 엔티티를 도메인 모델로 변환합니다.
     *
     * @param entity JPA 엔티티
     * @return 도메인 모델
     */
    @Mappings({
        @Mapping(target = "categoryId", source = "category.id"),
        @Mapping(target = "createdById", source = "createdBy.id"),
        @Mapping(target = "createdAt", source = "createdAt"),
        @Mapping(target = "updatedAt", source = "updatedAt"),
        @Mapping(target = "tags", ignore = true)
    })
    PromptTemplate toDomain(PromptTemplateEntity entity);

    /**
     * 패치 조인된 관계를 포함하여 엔티티를 도메인 모델로 변환합니다.
     * 태그 관계가 이미 로딩되어 있을 때 사용합니다.
     *
     * @param entity 변환할 엔티티 (태그 관계 포함)
     * @return 태그가 포함된 도메인 모델
     */
    default PromptTemplate toDomainWithTags(PromptTemplateEntity entity) {
        if (entity == null) {
            return null;
        }

        Set<Tag> tags = mapTagsFromEntity(entity.getTags());

        return PromptTemplate.builder()
            .id(entity.getId())
            .uuid(entity.getUuid())
            .title(entity.getTitle())
            .currentVersionId(entity.getCurrentVersionId())
            .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
            .createdById(entity.getCreatedBy() != null ? entity.getCreatedBy().getId() : null)
            .visibility(entity.getVisibility())
            .status(entity.getStatus())
            .description(entity.getDescription())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .tags(tags)
            .build();
    }

    /**
     * TagEntity 컬렉션을 Tag 도메인 모델 Set으로 변환합니다.
     *
     * @param tagEntities 변환할 TagEntity 컬렉션
     * @return Tag 도메인 모델 Set
     */
    default Set<Tag> mapTagsFromEntity(Set<TagEntity> tagEntities) {
        if (tagEntities == null || tagEntities.isEmpty()) {
            return Set.of();
        }

        return tagEntities.stream()
            .filter(Objects::nonNull)
            .map(tagEntity -> Tag.of(
                tagEntity.getId(),
                tagEntity.getName(),
                tagEntity.getCreatedAt(),
                tagEntity.getUpdatedAt()))
            .collect(Collectors.toSet());
    }

    /**
     * 도메인 모델을 JPA 엔티티로 변환합니다.
     *
     * @param domain 도메인 모델
     * @return JPA 엔티티
     */
    @Mappings({
        @Mapping(target = "category", expression = "java(domain.getCategoryId() != null ? new CategoryEntity(domain.getCategoryId()) : null)"),
        @Mapping(target = "createdBy", expression = "java(domain.getCreatedById() != null ? new UserEntity(domain.getCreatedById()) : null)"),
        @Mapping(target = "createdAt", source = "createdAt"),
        @Mapping(target = "updatedAt", source = "updatedAt"),
        @Mapping(target = "tagRelations", ignore = true),
        @Mapping(target = "tags", ignore = true)
    })
    PromptTemplateEntity toEntity(PromptTemplate domain);

    // after-mapping으로 tags → tagRelations 매핑
    default PromptTemplateEntity toEntityWithTags(PromptTemplate domain) {
        return toEntity(domain); // deprecated, 아래 after-mapping 사용
    }

    @org.mapstruct.AfterMapping
    default void mapTags(PromptTemplate domain, @org.mapstruct.MappingTarget PromptTemplateEntity entity) {
        if (domain.getTags() != null && !domain.getTags().isEmpty()) {
            entity.setTagRelations(new ArrayList<>());
            for (var tag : domain.getTags()) {
                TagEntity tagEntity = TagEntity.create(tag.getId(), tag.getName(), tag.getCreatedAt(),
                    tag.getUpdatedAt());
                entity.addTag(tagEntity);
            }
        }
    }
}
