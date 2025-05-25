package com.gongdel.promptserver.application.exception;

import com.gongdel.promptserver.domain.exception.CategoryDomainException;
import com.gongdel.promptserver.domain.exception.CategoryDuplicateNameDomainException;
import com.gongdel.promptserver.domain.exception.CategoryNotFoundDomainException;
import lombok.extern.slf4j.Slf4j;

/**
 * 카테고리 도메인 예외를 애플리케이션 예외로 변환하는 유틸리티 클래스입니다.
 * 헥사고날 아키텍처에서 도메인 계층의 예외를 애플리케이션 계층의 예외로 변환하는 책임을 담당합니다.
 */
@Slf4j
public final class CategoryExceptionConverter {

    private CategoryExceptionConverter() {
        // 유틸리티 클래스이므로 인스턴스화 방지
    }

    /**
     * 카테고리 도메인 예외를 애플리케이션 예외로 변환합니다.
     *
     * @param e          도메인 예외
     * @param identifier 카테고리 식별자(ID 또는 이름)
     * @return 변환된 애플리케이션 예외
     */
    public static RuntimeException convertToApplicationException(CategoryDomainException e, Object identifier) {
        log.debug("Converting domain exception to application exception for: {}", identifier);

        if (e instanceof CategoryNotFoundDomainException) {
            if (identifier instanceof Long) {
                return new CategoryNotFoundException((Long) identifier);
            } else {
                return new CategoryNotFoundException(identifier.toString());
            }
        } else if (e instanceof CategoryDuplicateNameDomainException) {
            return new CategoryDuplicateNameException(identifier.toString());
        } else {
            // 기타 도메인 예외는 CategoryOperationFailedException으로 변환
            return new CategoryOperationFailedException(
                "Error occurred during category operation: " + e.getMessage(),
                e);
        }
    }
}
