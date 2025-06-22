package com.gongdel.promptserver.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {
    RefreshTokenMapper INSTANCE = Mappers.getMapper(RefreshTokenMapper.class);

    // RefreshToken 도메인 모델이 필요하다면 여기에 추가
    // RefreshTokenEntity toEntity(RefreshToken domain);
    // RefreshToken toDomain(RefreshTokenEntity entity);
}
