package com.gongdel.promptserver.application.exception;

import com.gongdel.promptserver.domain.exception.CategoryNotFoundDomainException;
import org.springframework.http.HttpStatus;

/**
 * 카테고리를 찾을 수 없을 때 발생하는 애플리케이션 계층의 예외입니다.
 */
public class CategoryNotFoundException extends ApplicationException {

    private final CategoryNotFoundDomainException domainException;

    /**
     * ID로 카테고리를 찾을 수 없을 때 발생하는 예외를 생성합니다.
     *
     * @param id 찾을 수 없는 카테고리 ID
     */
    public CategoryNotFoundException(Long id) {
        super("ID가 " + id + "인 카테고리를 찾을 수 없습니다.",
            ApplicationErrorCode.CATEGORY_NOT_FOUND,
            HttpStatus.NOT_FOUND);
        this.domainException = new CategoryNotFoundDomainException(id);
    }

    /**
     * 이름으로 카테고리를 찾을 수 없을 때 발생하는 예외를 생성합니다.
     *
     * @param name 찾을 수 없는 카테고리 이름
     */
    public CategoryNotFoundException(String name) {
        super("이름이 '" + name + "'인 카테고리를 찾을 수 없습니다.",
            ApplicationErrorCode.CATEGORY_NOT_FOUND,
            HttpStatus.NOT_FOUND);
        this.domainException = new CategoryNotFoundDomainException(name);
    }

    /**
     * 도메인 예외를 반환합니다.
     *
     * @return 카테고리를 찾을 수 없는 도메인 예외
     */
    public CategoryNotFoundDomainException getDomainException() {
        return domainException;
    }
}
