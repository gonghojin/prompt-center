package com.gongdel.promptserver.domain.model;

import lombok.*;

import java.util.Map;

/**
 * 입력 변수 도메인 모델
 *
 * <p>
 * 프롬프트 입력 변수의 이름, 타입, 설명, 필수 여부, 기본값 정보를 담는 불변 객체입니다.
 * </p>
 */
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder
public class InputVariable {
    /**
     * 변수명
     */
    private final String name;
    /**
     * 변수 타입
     */
    private final String type;
    /**
     * 변수 설명
     */
    private final String description;
    /**
     * 필수 여부
     */
    private final boolean required;
    /**
     * 기본값
     */
    private final String defaultValue;

    /**
     * 입력 변수를 JSON 스키마 형태의 Map으로 변환합니다.
     *
     * @return JSON 스키마 Map
     */
    public Map<String, Object> toSchema() {
        return Map.of(
            Schema.TYPE, this.type,
            Schema.DESCRIPTION, this.description,
            Schema.REQUIRED, this.required,
            Schema.DEFAULT_VALUE, this.defaultValue);
    }

    /**
     * JSON 스키마 변환 시 사용할 필드명 상수
     */
    public static final class Schema {
        public static final String TYPE = "type";
        public static final String DESCRIPTION = "description";
        public static final String REQUIRED = "required";
        public static final String DEFAULT_VALUE = "defaultValue";
    }
}
