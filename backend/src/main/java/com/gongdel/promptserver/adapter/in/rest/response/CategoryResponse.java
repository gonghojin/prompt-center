package com.gongdel.promptserver.adapter.in.rest.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gongdel.promptserver.domain.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 카테고리 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    /**
     * 카테고리 ID
     */
    private Long id;

    /**
     * 카테고리 고유 이름
     */
    private String name;

    /**
     * 화면에 표시될 카테고리 이름
     */
    private String displayName;

    /**
     * 카테고리 설명
     */
    private String description;

    /**
     * 상위 카테고리 ID
     */
    private Long parentCategoryId;

    /**
     * 상위 카테고리 이름
     */
    private String parentCategoryName;

    /**
     * 시스템 카테고리 여부
     */
    @JsonProperty("isSystem")
    private boolean isSystem;

    /**
     * 생성 일시
     */
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     */
    private LocalDateTime updatedAt;

    /**
     * 도메인 객체로부터 응답 DTO를 생성합니다.
     *
     * @param category 카테고리 도메인 객체
     * @return 생성된 응답 DTO
     */
    public static CategoryResponse from(Category category) {
        if (category == null) {
            return null;
        }

        CategoryResponseBuilder builder = CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .displayName(category.getDisplayName())
                .description(category.getDescription())
                .isSystem(category.isSystem())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt());

        // 상위 카테고리가 있는 경우 ID와 이름을 설정
        if (category.getParentCategory() != null) {
            builder.parentCategoryId(category.getParentCategory().getId())
                    .parentCategoryName(category.getParentCategory().getDisplayName());
        }

        return builder.build();
    }

    @JsonIgnore
    public boolean isSystem() {
        return isSystem;
    }
}
