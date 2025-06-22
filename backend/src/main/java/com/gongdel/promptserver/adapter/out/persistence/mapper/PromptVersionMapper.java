package com.gongdel.promptserver.adapter.out.persistence.mapper;

import com.gongdel.promptserver.adapter.out.persistence.entity.PromptVersionEntity;
import com.gongdel.promptserver.domain.model.PromptVersion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface PromptVersionMapper {

    @Mappings({
        @Mapping(target = "promptTemplateId", source = "promptTemplate.id"),
        @Mapping(target = "createdById", source = "createdBy.id")
    })
    PromptVersion toDomain(PromptVersionEntity entity);

    @Mappings({
        @Mapping(target = "promptTemplate", expression = "java(domain.getPromptTemplateId() != null ? new PromptTemplateEntity(domain.getPromptTemplateId()) : null)"),
        @Mapping(target = "createdBy", expression = "java(domain.getCreatedById() != null ? new UserEntity(domain.getCreatedById()) : null)")
    })
    PromptVersionEntity toEntity(PromptVersion domain);
}
