package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.user.User;
import com.gongdel.promptserver.domain.user.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 사용자 검색 포트
 */
public interface SearchUsersPort {
    Page<User> searchUsersByKeyword(String keyword, Pageable pageable);

    Page<User> searchUsersByRole(UserRole role, Pageable pageable);
}
