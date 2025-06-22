package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.model.Category;

import java.util.List;

/**
 * 카테고리 컬렉션 조회를 위한 포트입니다.
 */
public interface FindCategoriesPort {

    /**
     * 모든 카테고리를 조회합니다.
     *
     * @return 카테고리 목록
     */
    List<Category> findAllCategories();

    /**
     * 시스템 카테고리 여부로 카테고리 목록을 조회합니다.
     *
     * @param isSystem 시스템 카테고리 여부
     * @return 카테고리 목록
     */
    List<Category> findCategoriesByIsSystem(boolean isSystem);

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
    List<Category> findCategoriesByParentId(Long parentId);
}
