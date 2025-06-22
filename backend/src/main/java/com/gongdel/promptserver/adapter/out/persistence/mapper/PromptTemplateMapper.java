package com.gongdel.promptserver.adapter.out.persistence.mapper;

import com.gongdel.promptserver.adapter.out.persistence.entity.PromptTemplateEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.TagEntity;
import com.gongdel.promptserver.domain.model.PromptTemplate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.ArrayList;

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

    @org.mapstruct.AfterMapping
    default void mapTagsToDomain(PromptTemplateEntity entity, @org.mapstruct.MappingTarget PromptTemplate domain) {
        if (entity.getTagRelations() != null && !entity.getTagRelations().isEmpty()) {
            for (var rel : entity.getTagRelations()) {
                if (rel.getTag() != null) {
                    domain.addTag(rel.getTag().toDomain());
                }
            }
        }
    }
}
