package com.gongdel.promptserver.application.port.out.command;

import com.gongdel.promptserver.domain.model.Category;

/**
 * 새 카테고리 생성을 위한 아웃바운드 포트 인터페이스입니다.
 */
public interface SaveCategoryPort {

    /**
     * 새 카테고리를 생성합니다.
     *
     * @param category 저장할 카테고리
     * @return 저장된 카테고리
     */
    Category saveCategory(Category category);
}
