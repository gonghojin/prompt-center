package com.gongdel.promptserver.domain.model;

import lombok.Getter;

/**
 * 프롬프트 템플릿의 공개 범위를 정의하는 열거형입니다. 공개(PUBLIC), 팀(TEAM), 비공개(PRIVATE) 상태를 나타냅니다.
 */
@Getter
public enum Visibility {
    /**
     * 공개 상태입니다. 모든 사용자가 접근 가능합니다.
     */
    PUBLIC("공개"),

    /**
     * 팀 공개 상태입니다. 같은 팀 구성원만 접근 가능합니다.
     */
    TEAM("팀"),

    /**
     * 비공개 상태입니다. 작성자만 접근 가능합니다.
     */
    PRIVATE("비공개");

    private final String displayName;

    /**
     * 공개 범위 열거형 생성자입니다.
     *
     * @param displayName 화면에 표시될 공개 범위 이름
     */
    Visibility(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 문자열 표현을 반환합니다.
     *
     * @return 공개 범위의 표시 이름
     */
    @Override
    public String toString() {
        return displayName;
    }

    /**
     * 공개 상태인지 확인합니다.
     *
     * @return 공개 상태이면 true, 아니면 false
     */
    public boolean isPublic() {
        return this == PUBLIC;
    }

    /**
     * 팀 공개 상태인지 확인합니다.
     *
     * @return 팀 공개 상태이면 true, 아니면 false
     */
    public boolean isTeam() {
        return this == TEAM;
    }

    /**
     * 비공개 상태인지 확인합니다.
     *
     * @return 비공개 상태이면 true, 아니면 false
     */
    public boolean isPrivate() {
        return this == PRIVATE;
    }
}
