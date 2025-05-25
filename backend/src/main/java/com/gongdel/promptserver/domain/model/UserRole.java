package com.gongdel.promptserver.domain.model;

import lombok.Getter;

/**
 * 사용자의 시스템 내 역할을 정의하는 열거형입니다. 각 역할은 시스템 내에서 다른 권한을 가집니다.
 */
@Getter
public enum UserRole {
    /**
     * 관리자 역할입니다. 시스템의 모든 기능과 관리 기능에 접근할 수 있습니다.
     */
    ROLE_ADMIN("관리자"),

    /**
     * 개발자 역할입니다. 프롬프트 생성 및 코드 관련 기능에 접근할 수 있습니다.
     */
    ROLE_DEVELOPER("개발자"),

    /**
     * 데이터 과학자 역할입니다. 데이터 분석 관련 프롬프트에 특화된 접근 권한을 가집니다.
     */
    ROLE_DATA_SCIENTIST("데이터 과학자"),

    /**
     * 디자이너 역할입니다. UI/UX 관련 프롬프트에 특화된 접근 권한을 가집니다.
     */
    ROLE_DESIGNER("디자이너"),

    /**
     * 일반 사용자 역할입니다. 기본적인 시스템 기능에 접근할 수 있습니다.
     */
    ROLE_USER("사용자");

    private final String displayName;

    /**
     * 사용자 역할 열거형 생성자입니다.
     *
     * @param displayName 화면에 표시될 역할 이름
     */
    UserRole(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 문자열 표현을 반환합니다.
     *
     * @return 역할의 표시 이름
     */
    @Override
    public String toString() {
        return displayName;
    }
}
