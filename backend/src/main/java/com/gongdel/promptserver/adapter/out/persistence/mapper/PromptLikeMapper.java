package com.gongdel.promptserver.adapter.out.persistence.mapper;

import com.gongdel.promptserver.adapter.out.persistence.entity.like.PromptLikeEntity;
import com.gongdel.promptserver.domain.like.LikedPromptResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * PromptLikeEntity를 LikedPromptResult로 변환하는 매퍼입니다.
 */
@Mapper(componentModel = "spring")
public interface PromptLikeMapper {
    /**
     * PromptLikeEntity → LikedPromptResult 변환
     *
     * @param entity PromptLikeEntity
     * @return LikedPromptResult
     */
    @Mappings({
        @Mapping(source = "promptTemplate.id", target = "id"),
        @Mapping(source = "promptTemplate.uuid", target = "uuid"),
        @Mapping(source = "promptTemplate.title", target = "title"),
        @Mapping(source = "promptTemplate.description", target = "description"),
        @Mapping(source = "promptTemplate.createdBy.id", target = "createdById"),
        @Mapping(source = "promptTemplate.createdBy.name", target = "createdByName"),
        @Mapping(source = "createdAt", target = "likedAt")
    })
    LikedPromptResult toDomain(PromptLikeEntity entity);
}
