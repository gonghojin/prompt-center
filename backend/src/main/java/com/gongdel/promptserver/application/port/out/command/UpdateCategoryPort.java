package com.gongdel.promptserver.application.port.out.command;

import com.gongdel.promptserver.domain.model.Category;

/**
 * 카테고리 업데이트를 위한 아웃바운드 포트 인터페이스입니다.
 */
public interface UpdateCategoryPort {

    /**
     * 기존 카테고리를 업데이트합니다.
     *
     * @param category 업데이트할 카테고리 객체
     * @return 업데이트된 카테고리 객체
     */
    Category updateCategory(Category category);
}
