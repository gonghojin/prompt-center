package com.gongdel.promptserver.domain.team;

import com.gongdel.promptserver.domain.user.User;

/**
 * 팀 관련 도메인 서비스입니다. 도메인 객체만으로 표현하기 어려운 비즈니스 규칙을 담당합니다.
 */
public class TeamDomainService {

    /**
     * 팀에 멤버를 추가합니다.
     *
     * @param team 팀
     * @param user 추가할 사용자
     * @throws TeamDomainException 이미 팀에 속한 경우
     */
    public void addMember(Team team, User user) {
        if (!team.addMember(user)) {
            throw new TeamDomainException("이미 팀에 속한 사용자입니다.");
        }
    }

    /**
     * 팀에서 멤버를 제거합니다.
     *
     * @param team 팀
     * @param user 제거할 사용자
     * @throws TeamDomainException 팀에 속하지 않은 경우
     */
    public void removeMember(Team team, User user) {
        if (!team.removeMember(user)) {
            throw new TeamDomainException("팀에 속하지 않은 사용자입니다.");
        }
    }

    /**
     * 팀을 활성화/비활성화합니다.
     *
     * @param team   대상 팀
     * @param active 활성화 여부
     */
    public void setActive(Team team, boolean active) {
        team.setActive(active);
    }

}
