package com.gongdel.promptserver.application.port.in;

/**
 * 팀멤버 통계 관련 Query 유스케이스 인터페이스입니다.
 */
public interface TeamMemberStatisticsQueryUseCase {

    /**
     * 전체 활성 팀멤버 수를 조회합니다.
     *
     * @return 전체 활성 팀멤버 수
     */
    long getTotalActiveMemberCount();
}
