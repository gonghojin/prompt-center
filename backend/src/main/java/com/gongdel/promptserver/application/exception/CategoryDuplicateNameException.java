package com.gongdel.promptserver.application.exception;

import com.gongdel.promptserver.domain.exception.CategoryDuplicateNameDomainException;
import org.springframework.http.HttpStatus;

/**
 * 동일한 이름의 카테고리가 이미 존재할 때 발생하는 애플리케이션 계층의 예외입니다.
 */
public class CategoryDuplicateNameException extends ApplicationException {

    private final CategoryDuplicateNameDomainException domainException;

    /**
     * 동일한 이름의 카테고리가 이미 존재할 때 예외를 생성합니다.
     *
     * @param name 중복된 카테고리 이름
     */
    public CategoryDuplicateNameException(String name) {
        super("동일한 이름('" + name + "')의 카테고리가 이미 존재합니다.",
                ApplicationErrorCode.CATEGORY_DUPLICATE_NAME,
                HttpStatus.CONFLICT);
        this.domainException = new CategoryDuplicateNameDomainException(name);
    }

    /**
     * 도메인 예외를 반환합니다.
     *
     * @return 카테고리 이름 중복 도메인 예외
     */
    public CategoryDuplicateNameDomainException getDomainException() {
        return domainException;
    }
}
