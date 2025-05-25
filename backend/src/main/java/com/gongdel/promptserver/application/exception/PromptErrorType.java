package com.gongdel.promptserver.application.exception;

import com.gongdel.promptserver.domain.exception.ErrorCode;
import lombok.Getter;

/**
 * 프롬프트 작업 중 발생할 수 있는 오류 유형을 정의합니다.
 */
@Getter
public enum PromptErrorType implements ErrorCode {
    VALIDATION_ERROR(1000, "프롬프트 유효성 검증 실패"),
    PERSISTENCE_ERROR(1001, "프롬프트 저장 실패"),
    TAG_PROCESSING_ERROR(1002, "프롬프트 태그 처리 실패"),
    INSUFFICIENT_PERMISSION(1003, "프롬프트 관련 권한 부족"),
    NOT_FOUND(1004, "프롬프트를 찾을 수 없습니다"),
    DUPLICATE_NAME(1005, "동일한 이름의 프롬프트가 이미 존재합니다"),
    VERSION_ERROR(1006, "프롬프트 버전 관련 오류가 발생했습니다"),
    UNKNOWN_ERROR(1999, "알 수 없는 프롬프트 관련 오류");

    private final int code;
    private final String message;

    PromptErrorType(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
