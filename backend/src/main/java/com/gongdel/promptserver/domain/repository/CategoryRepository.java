package com.gongdel.promptserver.domain.repository;

import com.gongdel.promptserver.domain.model.Category;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 도메인 모델을 위한 리포지토리 인터페이스입니다.
 */
public interface CategoryRepository {

    /**
     * 카테고리를 저장합니다.
     *
     * @param category 저장할 카테고리
     * @return 저장된 카테고리
     */
    Category save(Category category);

    /**
     * ID로 카테고리를 조회합니다.
     *
     * @param id 카테고리 ID
     * @return 카테고리 Optional
     */
    Optional<Category> findById(Long id);

    /**
     * 이름으로 카테고리를 조회합니다.
     *
     * @param name 카테고리 이름
     * @return 카테고리 Optional
     */
    Optional<Category> findByName(String name);

    /**
     * 모든 카테고리를 조회합니다.
     *
     * @return 카테고리 목록
     */
    List<Category> findAll();

    /**
     * 시스템 카테고리 여부로 카테고리 목록을 조회합니다.
     *
     * @param isSystem 시스템 카테고리 여부
     * @return 카테고리 목록
     */
    List<Category> findByIsSystem(boolean isSystem);

    /**
     * 상위 카테고리가 없는 카테고리 목록을 조회합니다.
     *
     * @return 최상위 카테고리 목록
     */
    List<Category> findRootCategories();

    /**
     * 상위 카테고리 ID로 하위 카테고리 목록을 조회합니다.
     *
     * @param parentId 상위 카테고리 ID
     * @return 하위 카테고리 목록
     */
    List<Category> findByParentId(Long parentId);

    /**
     * 카테고리를 삭제합니다.
     *
     * @param id 삭제할 카테고리 ID
     */
    void deleteById(Long id);
}
