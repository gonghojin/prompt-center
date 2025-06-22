package com.gongdel.promptserver.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 카테고리 정보를 저장하는 JPA 엔티티입니다.
 */
@Entity
@Table(name = "categories", indexes = {
    @Index(name = "idx_category_parent", columnList = "parent_category_id"),
    @Index(name = "idx_category_name", columnList = "name", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
public class CategoryEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_system", nullable = false)
    private boolean isSystem = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private CategoryEntity parentCategory;

    // 매퍼/테스트용 ID 기반 생성자
    public CategoryEntity(Long id) {
        this.id = id;
    }

    /**
     * 도메인 모델로부터 엔티티를 생성합니다.
     *
     * @param domainModel 도메인 모델
     * @return CategoryEntity 인스턴스
     */
    public static CategoryEntity fromDomain(com.gongdel.promptserver.domain.model.Category domainModel) {
        if (domainModel == null) {
            return null;
        }

        CategoryEntity entity = new CategoryEntity();
        entity.setId(domainModel.getId());
        entity.setName(domainModel.getName());
        entity.setDisplayName(domainModel.getDisplayName());
        entity.setDescription(domainModel.getDescription());
        entity.setSystem(domainModel.isSystem());

        if (domainModel.getParentCategory() != null) {
            entity.setParentCategory(fromDomain(domainModel.getParentCategory()));
        }

        entity.setCreatedAt(domainModel.getCreatedAt());
        entity.setUpdatedAt(domainModel.getUpdatedAt());

        return entity;
    }

    /**
     * 엔티티로부터 도메인 모델을 생성합니다.
     *
     * @return Category 도메인 모델
     */
    public com.gongdel.promptserver.domain.model.Category toDomain() {
        com.gongdel.promptserver.domain.model.Category parentCategoryDomain = null;
        if (parentCategory != null) {
            parentCategoryDomain = new com.gongdel.promptserver.domain.model.Category(
                parentCategory.getId(),
                parentCategory.getName(),
                parentCategory.getDisplayName(),
                parentCategory.getDescription(),
                null,
                parentCategory.isSystem(),
                parentCategory.getCreatedAt(),
                parentCategory.getUpdatedAt());
        }

        com.gongdel.promptserver.domain.model.Category domainModel = new com.gongdel.promptserver.domain.model.Category(
            id,
            name,
            displayName,
            description,
            parentCategoryDomain,
            isSystem,
            getCreatedAt(),
            getUpdatedAt());

        return domainModel;
    }
}
