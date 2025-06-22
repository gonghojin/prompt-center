package com.gongdel.promptserver.adapter.out.persistence;

import com.gongdel.promptserver.adapter.out.persistence.entity.TeamMemberEntity;
import com.gongdel.promptserver.adapter.out.persistence.repository.TeamMemberRepository;
import com.gongdel.promptserver.application.port.out.TeamMemberStatisticsQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 팀멤버 통계 조회용 어댑터 구현체입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TeamMemberStatisticsQueryAdapter implements TeamMemberStatisticsQueryPort {

    private final TeamMemberRepository teamMemberRepository;

    /**
     * 전체 활성 팀멤버 수를 조회합니다.
     *
     * @return 전체 활성 팀멤버 수
     */
    @Override
    public long countActiveMember() {
        log.debug("Counting active team members from database");
        return teamMemberRepository.countByStatus(TeamMemberEntity.TeamMemberStatus.ACTIVE);
    }
}
