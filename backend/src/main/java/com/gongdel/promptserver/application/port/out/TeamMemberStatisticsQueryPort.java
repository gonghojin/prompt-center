package com.gongdel.promptserver.application.port.out;

/**
 * 팀멤버 통계 조회용 Port 인터페이스입니다.
 */
public interface TeamMemberStatisticsQueryPort {

    /**
     * 전체 활성 팀멤버 수를 조회합니다.
     *
     * @return 전체 활성 팀멤버 수
     */
    long countActiveMember();
}
