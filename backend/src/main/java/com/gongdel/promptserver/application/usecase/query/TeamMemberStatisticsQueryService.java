package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.port.in.TeamMemberStatisticsQueryUseCase;
import com.gongdel.promptserver.application.port.out.TeamMemberStatisticsQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 팀멤버 통계 조회 서비스 구현체입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeamMemberStatisticsQueryService implements TeamMemberStatisticsQueryUseCase {

    private final TeamMemberStatisticsQueryPort teamMemberStatisticsQueryPort;

    /**
     * 전체 활성 팀멤버 수를 조회합니다.
     *
     * @return 전체 활성 팀멤버 수
     */
    @Override
    public long getTotalActiveMemberCount() {
        log.debug("Fetching total active team member count");
        long count = teamMemberStatisticsQueryPort.countActiveMember();
        log.info("Total active team member count: {}", count);
        return count;
    }
}
