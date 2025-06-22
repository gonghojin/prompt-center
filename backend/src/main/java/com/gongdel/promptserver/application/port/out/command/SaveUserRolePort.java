package com.gongdel.promptserver.application.port.out.command;

import com.gongdel.promptserver.domain.user.UserRole;

/**
 * 사용자 역할을 저장하는 포트 인터페이스
 * 헥사고날 아키텍처의 출력 포트로, 사용자 역할 정보를 영속화하는 역할을 담당
 */
public interface SaveUserRolePort {
    /**
     * 사용자 역할 정보를 저장하고 저장된 사용자 역할을 반환합니다.
     *
     * @param userRole 저장할 사용자 역할 정보
     * @return 저장된 사용자 역할 정보
     */
    UserRole saveUserRole(UserRole userRole);
}
