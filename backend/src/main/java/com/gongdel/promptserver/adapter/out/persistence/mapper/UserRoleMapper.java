package com.gongdel.promptserver.adapter.out.persistence.mapper;

import com.gongdel.promptserver.adapter.out.persistence.entity.UserRoleEntity;
import com.gongdel.promptserver.domain.user.UserRole;
import com.gongdel.promptserver.domain.user.UserRoleId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserRoleMapper {
    UserRoleMapper INSTANCE = Mappers.getMapper(UserRoleMapper.class);

    @Named("userRoleIdToUuid")
    static UUID userRoleIdToUuid(UserRoleId id) {
        return id != null ? id.getValue() : null;
    }

    @Named("uuidToUserRoleId")
    static UserRoleId uuidToUserRoleId(UUID uuid) {
        return uuid != null ? new UserRoleId(uuid) : null;
    }

    @Mapping(target = "uuid", source = "uuid", qualifiedByName = "uuidToUserRoleId")
    UserRole toDomain(UserRoleEntity entity);

    @Mapping(target = "uuid", source = "uuid", qualifiedByName = "userRoleIdToUuid")
    UserRoleEntity toEntity(UserRole domain);

}
