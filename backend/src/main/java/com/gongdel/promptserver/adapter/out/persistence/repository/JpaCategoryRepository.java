package com.gongdel.promptserver.adapter.out.persistence.repository;

import com.gongdel.promptserver.adapter.out.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 엔티티에 대한 JPA 리포지토리입니다.
 */
@Repository
public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, Long> {

    /**
     * 카테고리 이름으로 카테고리를 조회합니다.
     *
     * @param name 카테고리 이름
     * @return 카테고리 Optional
     */
    Optional<CategoryEntity> findByName(String name);

    /**
     * 시스템 카테고리 여부로 카테고리 목록을 조회합니다.
     *
     * @param isSystem 시스템 카테고리 여부
     * @return 카테고리 목록
     */
    List<CategoryEntity> findByIsSystem(boolean isSystem);

    /**
     * 상위 카테고리 ID로 하위 카테고리 목록을 조회합니다.
     *
     * @param parentCategoryId 상위 카테고리 ID
     * @return 하위 카테고리 목록
     */
    List<CategoryEntity> findByParentCategoryId(Long parentCategoryId);

    /**
     * 상위 카테고리가 없는 카테고리 목록을 조회합니다.
     *
     * @return 최상위 카테고리 목록
     */
    List<CategoryEntity> findByParentCategoryIsNull();
}
