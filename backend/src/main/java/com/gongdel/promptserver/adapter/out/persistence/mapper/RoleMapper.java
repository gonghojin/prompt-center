package com.gongdel.promptserver.adapter.out.persistence.mapper;

import com.gongdel.promptserver.adapter.out.persistence.entity.RoleEntity;
import com.gongdel.promptserver.domain.role.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Role 도메인 모델과 JPA 엔티티 간의 변환을 담당하는 매퍼
 */
@Mapper(componentModel = "spring")
public interface RoleMapper {

    /**
     * JPA 엔티티를 도메인 모델로 변환합니다.
     *
     * @param entity JPA 엔티티
     * @return 도메인 모델
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    Role toDomain(RoleEntity entity);

    /**
     * 도메인 모델을 JPA 엔티티로 변환합니다.
     *
     * @param domain 도메인 모델
     * @return JPA 엔티티
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    RoleEntity toEntity(Role domain);
}
