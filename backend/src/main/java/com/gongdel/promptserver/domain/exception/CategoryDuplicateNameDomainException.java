package com.gongdel.promptserver.domain.exception;

/**
 * 동일한 이름의 카테고리가 이미 존재할 때 발생하는 도메인 예외입니다.
 */
public class CategoryDuplicateNameDomainException extends CategoryDomainException {

    /**
     * 동일한 이름의 카테고리가 이미 존재할 때 예외를 생성합니다.
     *
     * @param name 중복된 카테고리 이름
     */
    public CategoryDuplicateNameDomainException(String name) {
        super(CategoryErrorType.DUPLICATE_NAME, "A category with the same name ('" + name + "') already exists.");
    }
}
