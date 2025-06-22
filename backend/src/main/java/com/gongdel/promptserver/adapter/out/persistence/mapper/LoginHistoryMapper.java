package com.gongdel.promptserver.adapter.out.persistence.mapper;

import com.gongdel.promptserver.adapter.out.persistence.entity.LoginHistoryEntity;
import com.gongdel.promptserver.domain.login.LoginHistory;
import com.gongdel.promptserver.domain.login.LoginHistoryId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface LoginHistoryMapper {
    LoginHistoryMapper INSTANCE = Mappers.getMapper(LoginHistoryMapper.class);

    @Named("loginHistoryIdToUuid")
    static UUID loginHistoryIdToUuid(LoginHistoryId id) {
        return id != null ? id.getValue() : null;
    }

    @Named("uuidToLoginHistoryId")
    static LoginHistoryId uuidToLoginHistoryId(UUID uuid) {
        return uuid != null ? new LoginHistoryId(uuid) : null;
    }

    @Mapping(target = "uuid", source = "loginHistoryId", qualifiedByName = "loginHistoryIdToUuid")
    LoginHistoryEntity toEntity(LoginHistory domain);

    @Mapping(target = "loginHistoryId", source = "uuid", qualifiedByName = "uuidToLoginHistoryId")
    LoginHistory toDomain(LoginHistoryEntity entity);
}
