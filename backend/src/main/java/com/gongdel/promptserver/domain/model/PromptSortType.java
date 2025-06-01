package com.gongdel.promptserver.domain.model;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum PromptSortType {
    /**
     * 최근 수정순 정렬
     */
    LATEST_MODIFIED("최근 수정순", "updatedAt"),
    /**
     * 프롬프트 이름순 정렬
     */
    TITLE("프롬프트 이름순", "title");

    private final String description;
    private final String field;

    /**
     * PromptSortType 생성자
     *
     * @param description 정렬 설명
     * @param field       정렬 기준 필드명
     */
    PromptSortType(String description, String field) {
        this.description = description;
        this.field = field;
    }

    /**
     * 정렬 타입의 설명을 반환합니다.
     *
     * @return 정렬 설명
     */
    public String getDescription() {
        return description;
    }

    /**
     * 정렬 기준 필드명을 반환합니다.
     *
     * @return 정렬 기준 필드명
     */
    public String getField() {
        return field;
    }

    /**
     * 문자열을 PromptSortType으로 안전하게 변환합니다. 잘못된 값이면 기본값을 반환합니다.
     *
     * @param value        파싱할 문자열
     * @param defaultValue 기본값
     * @return 파싱된 PromptSortType 또는 기본값
     */
    public static PromptSortType fromString(String value, PromptSortType defaultValue) {
        try {
            return PromptSortType.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException e) {
            log.debug("Invalid PromptSortType value: '{}', returning default: '{}'", value, defaultValue, e);
            return defaultValue;
        }
    }
}
