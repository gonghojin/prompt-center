package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.model.Category;

import java.util.Optional;

/**
 * 카테고리 단일 엔티티 조회를 위한 포트입니다.
 */
public interface LoadCategoryPort {

    /**
     * ID로 카테고리를 조회합니다.
     *
     * @param id 카테고리 ID
     * @return 카테고리 Optional
     */
    Optional<Category> loadCategoryById(Long id);

    /**
     * 이름으로 카테고리를 조회합니다.
     *
     * @param name 카테고리 이름
     * @return 카테고리 Optional
     */
    Optional<Category> loadCategoryByName(String name);
}
