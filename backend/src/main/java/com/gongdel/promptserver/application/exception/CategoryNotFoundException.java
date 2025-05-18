package com.gongdel.promptserver.application.exception;

import com.gongdel.promptserver.domain.exception.CategoryErrorType;
import com.gongdel.promptserver.domain.exception.CategoryPersistenceException;

/**
 * 카테고리를 찾을 수 없을 때 발생하는 예외입니다.
 */
public class CategoryNotFoundException extends CategoryPersistenceException {

    /**
     * ID로 카테고리를 찾을 수 없을 때 발생하는 예외를 생성합니다.
     *
     * @param id 찾을 수 없는 카테고리 ID
     */
    public CategoryNotFoundException(Long id) {
        super(CategoryErrorType.NOT_FOUND, "ID가 " + id + "인 카테고리를 찾을 수 없습니다.");
    }

    /**
     * 이름으로 카테고리를 찾을 수 없을 때 발생하는 예외를 생성합니다.
     *
     * @param name 찾을 수 없는 카테고리 이름
     */
    public CategoryNotFoundException(String name) {
        super(CategoryErrorType.NOT_FOUND, "이름이 '" + name + "'인 카테고리를 찾을 수 없습니다.");
    }
}
