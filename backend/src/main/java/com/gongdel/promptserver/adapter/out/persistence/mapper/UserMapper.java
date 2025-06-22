package com.gongdel.promptserver.adapter.out.persistence.mapper;

import com.gongdel.promptserver.adapter.out.persistence.entity.UserEntity;
import com.gongdel.promptserver.domain.team.Team;
import com.gongdel.promptserver.domain.user.Email;
import com.gongdel.promptserver.domain.user.User;
import com.gongdel.promptserver.domain.user.UserId;
import com.gongdel.promptserver.domain.user.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

/**
 * User 도메인 모델과 JPA 엔티티 간의 변환을 담당하는 매퍼
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * JPA 엔티티를 도메인 모델로 변환합니다.
     *
     * @param entity JPA 엔티티
     * @return 도메인 모델
     */
    @Mapping(target = "uuid", source = "uuid", qualifiedByName = "uuidToUserId")
    @Mapping(target = "email", source = "email", qualifiedByName = "stringToEmail")
    @Mapping(target = "team", source = "teamId", qualifiedByName = "teamIdToTeam")
    @Mapping(target = "status", source = "status", qualifiedByName = "userStatusToDomain")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    User toDomain(UserEntity entity);

    /**
     * 도메인 모델을 JPA 엔티티로 변환합니다.
     *
     * @param domain 도메인 모델
     * @return JPA 엔티티
     */
    @Mapping(target = "uuid", source = "uuid", qualifiedByName = "userIdToUuid")
    @Mapping(target = "email", source = "email", qualifiedByName = "emailToString")
    @Mapping(target = "teamId", source = "team", qualifiedByName = "teamToTeamId")
    @Mapping(target = "status", source = "status", qualifiedByName = "userStatusToEntity")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    UserEntity toEntity(User domain);

    // UUID <-> UserId
    @Named("uuidToUserId")
    default UserId uuidToUserId(UUID uuid) {
        return uuid == null ? null : new UserId(uuid);
    }

    @Named("userIdToUuid")
    default UUID userIdToUuid(UserId userId) {
        return userId == null ? null : userId.getValue();
    }

    // String <-> Email
    @Named("stringToEmail")
    default Email stringToEmail(String email) {
        return email == null ? null : new Email(email);
    }

    @Named("emailToString")
    default String emailToString(Email email) {
        return email == null ? null : email.getValue();
    }

    // teamId <-> Team (간단화, 실제 TeamRepository 필요시 별도 처리)
    @Named("teamIdToTeam")
    default Team teamIdToTeam(Long teamId) {
        if (teamId == null) {
            return null;
        }
        return new Team(teamId, null, "Unknown Team", null, Team.TeamStatus.ACTIVE);
    }

    @Named("teamToTeamId")
    default Long teamToTeamId(Team team) {
        return team == null ? null : team.getId();
    }

    // Enum 매핑 (DELETED → null 또는 예외처리)
    @Named("userStatusToDomain")
    default UserStatus userStatusToDomain(UserEntity.UserStatus status) {
        if (status == null)
            return null;
        if (status == UserEntity.UserStatus.DELETED)
            return null; // 또는 UserStatus.INACTIVE 등
        return UserStatus.valueOf(status.name());
    }

    @Named("userStatusToEntity")
    default UserEntity.UserStatus userStatusToEntity(UserStatus status) {
        if (status == null)
            return null;
        return UserEntity.UserStatus.valueOf(status.name());
    }
}
