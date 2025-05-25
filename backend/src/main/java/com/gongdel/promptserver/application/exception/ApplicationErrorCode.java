package com.gongdel.promptserver.application.exception;

import com.gongdel.promptserver.domain.exception.ErrorCode;
import lombok.Getter;

/**
 * 애플리케이션 계층에서 발생할 수 있는 오류 유형을 정의합니다.
 */
@Getter
public enum ApplicationErrorCode implements ErrorCode {
    // 프롬프트 관련 오류 (1000-1999)
    PROMPT_VALIDATION_ERROR(1000, "프롬프트 유효성 검증 실패"),
    PROMPT_PERSISTENCE_ERROR(1001, "프롬프트 저장 실패"),
    PROMPT_TAG_PROCESSING_ERROR(1002, "프롬프트 태그 처리 실패"),
    PROMPT_INSUFFICIENT_PERMISSION(1003, "프롬프트 관련 권한 부족"),
    PROMPT_VERSION_NOT_FOUND(1004, "프롬프트 버전을 찾을 수 없습니다"),
    PROMPT_VERSION_OPERATION_FAILED(1005, "프롬프트 버전 작업을 수행할 수 없습니다"),
    PROMPT_UNKNOWN_ERROR(1999, "알 수 없는 프롬프트 관련 오류"),

    // 카테고리 관련 오류 (2000-2999)
    CATEGORY_NOT_FOUND(2000, "카테고리를 찾을 수 없습니다"),
    CATEGORY_DUPLICATE_NAME(2001, "동일한 이름의 카테고리가 이미 존재합니다"),
    CATEGORY_INVALID(2002, "유효하지 않은 카테고리 정보입니다"),
    CATEGORY_CIRCULAR_REFERENCE(2003, "카테고리 순환 참조가 발생했습니다"),
    CATEGORY_OPERATION_FAILED(2004, "카테고리 작업을 수행할 수 없습니다"),
    CATEGORY_UNKNOWN_ERROR(2999, "카테고리 관련 알 수 없는 오류가 발생했습니다"),

    // 일반 오류 (9000-9999)
    UNKNOWN_ERROR(9999, "알 수 없는 오류");

    private final int code;
    private final String message;

    ApplicationErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
