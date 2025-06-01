package com.gongdel.promptserver.domain.model;

import lombok.Getter;

/**
 * 프롬프트 템플릿의 상태를 정의하는 열거형입니다. 프롬프트의 라이프사이클 상태를 나타냅니다.
 */
@Getter
public enum PromptStatus {
    /**
     * 초안 상태입니다. 아직 완성되지 않은 프롬프트입니다.
     */
    DRAFT("초안"),

    /**
     * 게시됨 상태입니다. 사용 가능한 완성된 프롬프트입니다.
     */
    PUBLISHED("게시됨"),

    /**
     * 보관됨 상태입니다. 더 이상 활발히 사용되지 않는 프롬프트입니다.
     */
    ARCHIVED("보관됨");

    private final String displayName;

    /**
     * 프롬프트 상태 열거형 생성자입니다.
     *
     * @param displayName 화면에 표시될 상태 이름
     */
    PromptStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 문자열을 PromptStatus로 안전하게 변환합니다. 잘못된 값이면 기본값을 반환합니다.
     *
     * @param value        파싱할 문자열
     * @param defaultValue 기본값
     * @return 파싱된 PromptStatus 또는 기본값
     */
    public static PromptStatus fromString(String value, PromptStatus defaultValue) {
        try {
            return PromptStatus.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException e) {
            return defaultValue;
        }
    }

    /**
     * 문자열 표현을 반환합니다.
     *
     * @return 상태의 표시 이름
     */
    @Override
    public String toString() {
        return displayName;
    }
}
