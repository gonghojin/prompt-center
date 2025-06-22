package com.gongdel.promptserver.adapter.out.persistence.mapper;

import com.gongdel.promptserver.adapter.out.persistence.entity.UserAuthenticationEntity;
import com.gongdel.promptserver.domain.userauth.UserAuthentication;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserAuthenticationMapper {
    UserAuthenticationMapper INSTANCE = Mappers.getMapper(UserAuthenticationMapper.class);

    UserAuthenticationEntity toEntity(UserAuthentication domain);

    UserAuthentication toDomain(UserAuthenticationEntity entity);
}
