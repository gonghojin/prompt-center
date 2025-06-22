package com.gongdel.promptserver.adapter.out.persistence.mapper;

import com.gongdel.promptserver.adapter.out.persistence.entity.RefreshTokenEntity;
import com.gongdel.promptserver.domain.refreshtoken.RefreshToken;
import com.gongdel.promptserver.domain.user.UserId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

/**
 * RefreshToken 도메인 모델과 JPA 엔티티 간의 변환을 담당하는 매퍼
 */
@Mapper(componentModel = "spring")
public interface TokenMapper {

    /**
     * JPA 엔티티를 도메인 모델로 변환합니다.
     *
     * @param entity JPA 엔티티
     * @return 도메인 모델
     */
    @Mapping(target = "userId", source = "userId", qualifiedByName = "stringToUserId")
    RefreshToken toDomain(RefreshTokenEntity entity);

    /**
     * 도메인 모델을 JPA 엔티티로 변환합니다.
     *
     * @param domain 도메인 모델
     * @return JPA 엔티티
     */
    @Mapping(target = "userId", source = "userId", qualifiedByName = "userIdToString")
    RefreshTokenEntity toEntity(RefreshToken domain);

    // String <-> UserId
    @Named("stringToUserId")
    default UserId stringToUserId(String userId) {
        return userId == null ? null : new UserId(UUID.fromString(userId));
    }

    @Named("userIdToString")
    default String userIdToString(UserId userId) {
        return userId == null ? null : userId.getValue().toString();
    }
}
