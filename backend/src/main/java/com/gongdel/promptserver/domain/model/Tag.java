package com.gongdel.promptserver.domain.model;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 태그 정보를 나타내는 도메인 모델 클래스입니다. 프롬프트 분류에 사용됩니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(of = {"id", "name"})
public class Tag extends BaseTimeEntity {

    private Long id;
    private String name;

    /**
     * 태그를 생성합니다. 직접 호출하지 말고 정적 팩토리 메서드를 사용하세요.
     *
     * @param id        태그 ID
     * @param name      태그 이름
     * @param createdAt 생성 일시 (null인 경우 현재 시간 사용)
     * @param updatedAt 수정 일시 (null인 경우 현재 시간 사용)
     */
    @Builder(access = AccessLevel.PRIVATE)
    private Tag(Long id, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        validateName(name);
        this.id = id;
        this.name = name;
    }

    /**
     * 신규 태그를 생성합니다.
     *
     * @param name 태그 이름
     * @return 태그 객체
     * @throws IllegalArgumentException 태그 이름이 null이거나 빈 문자열인 경우
     */
    public static Tag create(String name) {
        LocalDateTime now = LocalDateTime.now();
        return Tag.builder()
            .name(name)
            .createdAt(now)
            .updatedAt(now)
            .build();
    }

    /**
     * 기존 태그 정보로 태그 객체를 생성합니다.
     *
     * @param id        태그 ID (필수)
     * @param name      태그 이름 (필수)
     * @param createdAt 생성 일시 (필수)
     * @param updatedAt 수정 일시 (필수)
     * @return 태그 객체
     * @throws IllegalArgumentException 필수 정보가 누락된 경우
     */
    public static Tag of(Long id, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (id == null) {
            throw new IllegalArgumentException("태그 ID는 필수입니다");
        }
        if (createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("생성/수정 일시는 필수입니다");
        }

        return Tag.builder()
            .id(id)
            .name(name)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();
    }

    /**
     * 태그 정보를 업데이트합니다.
     *
     * @param name 변경할 태그 이름
     * @throws IllegalArgumentException 태그 이름이 null이거나 빈 문자열인 경우
     */
    public void update(String name) {
        validateName(name);
        this.name = name;
        updateModifiedTime();
    }

    /**
     * 태그 이름의 유효성을 검증합니다.
     *
     * @param name 검증할 태그 이름
     * @throws IllegalArgumentException 태그 이름이 null이거나 빈 문자열인 경우
     */
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("태그 이름은 필수입니다");
        }
    }
}
