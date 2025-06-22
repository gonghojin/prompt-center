package com.gongdel.promptserver.adapter.out.persistence.repository;

import com.gongdel.promptserver.adapter.out.persistence.entity.TeamMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMemberEntity, Long> {
    List<TeamMemberEntity> findByTeamId(Long teamId);

    List<TeamMemberEntity> findByUserId(Long userId);

    Optional<TeamMemberEntity> findByTeamIdAndUserId(Long teamId, Long userId);

    /**
     * 특정 상태의 팀멤버 수를 조회합니다.
     *
     * @param status 팀멤버 상태
     * @return 해당 상태의 팀멤버 수
     */
    long countByStatus(TeamMemberEntity.TeamMemberStatus status);
}
