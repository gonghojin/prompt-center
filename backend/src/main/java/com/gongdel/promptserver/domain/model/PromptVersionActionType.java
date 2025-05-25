package com.gongdel.promptserver.domain.model;

import lombok.Getter;

/**
 * 프롬프트 버전 작업 유형을 정의하는 열거형입니다. 각 작업 유형은 프롬프트 버전의 변경 이력을 추적하는 데 사용됩니다.
 */
@Getter
public enum PromptVersionActionType {
    /**
     * 새로운 프롬프트 생성 작업
     */
    CREATE("생성"),

    /**
     * 프롬프트 내용 수정 작업
     */
    EDIT("수정"),

    /**
     * 프롬프트 게시(공개) 작업
     */
    PUBLISH("게시"),

    /**
     * 프롬프트 보관 작업
     */
    ARCHIVE("보관");

    private final String displayName;

    /**
     * 작업 유형 열거형 생성자
     *
     * @param displayName 화면에 표시될 작업 이름
     */
    PromptVersionActionType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 문자열 표현을 반환합니다.
     *
     * @return 작업 유형의 표시 이름
     */
    @Override
    public String toString() {
        return displayName;
    }
}
