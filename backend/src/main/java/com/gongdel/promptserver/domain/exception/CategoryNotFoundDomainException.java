package com.gongdel.promptserver.domain.exception;

/**
 * 카테고리를 찾을 수 없을 때 발생하는 도메인 예외입니다.
 */
public class CategoryNotFoundDomainException extends CategoryDomainException {

    /**
     * ID로 카테고리를 찾을 수 없을 때 발생하는 예외를 생성합니다.
     *
     * @param id 찾을 수 없는 카테고리 ID
     */
    public CategoryNotFoundDomainException(Long id) {
        super(CategoryErrorType.NOT_FOUND, "Category with ID " + id + " cannot be found.");
    }

    /**
     * 이름으로 카테고리를 찾을 수 없을 때 발생하는 예외를 생성합니다.
     *
     * @param name 찾을 수 없는 카테고리 이름
     */
    public CategoryNotFoundDomainException(String name) {
        super(CategoryErrorType.NOT_FOUND, "Category with name '" + name + "' cannot be found.");
    }
}
