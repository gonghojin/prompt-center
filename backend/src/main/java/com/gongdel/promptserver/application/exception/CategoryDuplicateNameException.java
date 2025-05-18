package com.gongdel.promptserver.application.exception;

import com.gongdel.promptserver.domain.exception.CategoryErrorType;
import com.gongdel.promptserver.domain.exception.CategoryPersistenceException;

/**
 * 동일한 이름의 카테고리가 이미 존재할 때 발생하는 예외입니다.
 */
public class CategoryDuplicateNameException extends CategoryPersistenceException {

    /**
     * 동일한 이름의 카테고리가 이미 존재할 때 예외를 생성합니다.
     *
     * @param name 중복된 카테고리 이름
     */
    public CategoryDuplicateNameException(String name) {
        super(CategoryErrorType.DUPLICATE_NAME, "동일한 이름('" + name + "')의 카테고리가 이미 존재합니다.");
    }
}
