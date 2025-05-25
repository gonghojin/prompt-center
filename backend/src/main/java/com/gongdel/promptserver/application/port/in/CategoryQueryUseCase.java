package com.gongdel.promptserver.application.port.in;

import com.gongdel.promptserver.domain.model.Category;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 조회(Query) 작업을 처리하는 유스케이스 인터페이스입니다.
 * 이 인터페이스는 헥사고널 아키텍처의 인바운드 포트로,
 * 카테고리 조회 및 검색 기능을 정의합니다.
 */
public interface CategoryQueryUseCase {

    /**
     * 카테고리 ID로 카테고리를 조회합니다.
     *
     * @param id 카테고리 ID
     * @return 카테고리 Optional
     */
    Optional<Category> getCategoryById(Long id);

    /**
     * 카테고리 이름으로 카테고리를 조회합니다.
     *
     * @param name 카테고리 이름
     * @return 카테고리 Optional
     */
    Optional<Category> getCategoryByName(String name);

    /**
     * 모든 카테고리를 조회합니다.
     *
     * @return 카테고리 목록
     */
    List<Category> getAllCategories();

    /**
     * 시스템 카테고리 여부로 카테고리 목록을 조회합니다.
     *
     * @param isSystem 시스템 카테고리 여부
     * @return 카테고리 목록
     */
    List<Category> getCategoriesBySystemFlag(boolean isSystem);

    /**
     * 최상위 카테고리(상위 카테고리가 없는)를 조회합니다.
     *
     * @return 최상위 카테고리 목록
     */
    List<Category> getRootCategories();

    /**
     * 특정 카테고리의 하위 카테고리를 조회합니다.
     *
     * @param parentId 상위 카테고리 ID
     * @return 하위 카테고리 목록
     */
    List<Category> getSubCategories(Long parentId);
}
