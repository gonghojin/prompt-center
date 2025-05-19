package com.gongdel.promptserver.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 프롬프트 템플릿의 카테고리를 관리하는 도메인 모델입니다.
 */
@Getter
@NoArgsConstructor
public class Category extends BaseTimeEntity {

    private Long id;
    private String name;
    private String displayName;
    private String description;
    private boolean isSystem;
    private Category parentCategory;

    /**
     * 기본 생성자입니다.
     *
     * @param name        카테고리 고유 이름
     * @param displayName 화면에 표시될 카테고리 이름
     * @param description 카테고리 설명
     */
    public Category(String name, String displayName, String description) {
        this(null, name, displayName, description, null, false, null, null);
    }

    /**
     * 시스템 카테고리를 생성하는 생성자입니다.
     *
     * @param name        카테고리 고유 이름
     * @param displayName 화면에 표시될 카테고리 이름
     * @param description 카테고리 설명
     * @param isSystem    시스템 카테고리 여부
     */
    public Category(String name, String displayName, String description, boolean isSystem) {
        this(null, name, displayName, description, null, isSystem, null, null);
    }

    /**
     * 하위 카테고리를 생성하는 생성자입니다.
     *
     * @param name           카테고리 고유 이름
     * @param displayName    화면에 표시될 카테고리 이름
     * @param description    카테고리 설명
     * @param parentCategory 상위 카테고리
     */
    public Category(String name, String displayName, String description, Category parentCategory) {
        this(null, name, displayName, description, parentCategory, false, null, null);
    }

    /**
     * 전체 생성자입니다.
     *
     * @param id             카테고리 ID
     * @param name           카테고리 고유 이름
     * @param displayName    화면에 표시될 카테고리 이름
     * @param description    카테고리 설명
     * @param parentCategory 상위 카테고리
     * @param isSystem       시스템 카테고리 여부
     * @param createdAt      생성 일시
     * @param updatedAt      수정 일시
     */
    public Category(Long id, String name, String displayName, String description, Category parentCategory,
            boolean isSystem, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        validateName(name);
        validateDisplayName(displayName);

        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.parentCategory = parentCategory;
        this.isSystem = isSystem;
    }

    /**
     * 이름 유효성 검사
     *
     * @param name 검사할 이름
     */
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("카테고리 이름은 필수입니다.");
        }
    }

    /**
     * 표시 이름 유효성 검사
     *
     * @param displayName 검사할 표시 이름
     */
    private void validateDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("카테고리 표시 이름은 필수입니다.");
        }
    }

    /**
     * 카테고리 정보를 업데이트합니다.
     *
     * @param displayName    새 표시 이름
     * @param description    새 설명
     * @param parentCategory 새 상위 카테고리
     */
    public void update(String displayName, String description, Category parentCategory) {
        validateDisplayName(displayName);

        this.displayName = displayName;
        this.description = description;
        this.parentCategory = parentCategory;
        updateModifiedTime();
    }

    /**
     * 문자열 표현을 반환합니다.
     *
     * @return 카테고리의 표시 이름
     */
    @Override
    public String toString() {
        return displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
