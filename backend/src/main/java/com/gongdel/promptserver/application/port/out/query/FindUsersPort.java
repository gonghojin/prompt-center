package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.team.TeamId;
import com.gongdel.promptserver.domain.user.User;
import com.gongdel.promptserver.domain.user.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 사용자 목록 조회 포트
 */
public interface FindUsersPort {
    Page<User> findUsersByTeam(TeamId teamId, Pageable pageable);

    Page<User> findUsersByStatus(UserStatus status, Pageable pageable);
}
