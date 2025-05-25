package com.gongdel.promptserver.domain.exception;

import lombok.Getter;

/**
 * 프롬프트 관련 작업 중 발생할 수 있는 오류 유형을 정의합니다.
 */
@Getter
public enum PromptErrorType implements ErrorCode {
    VALIDATION_ERROR(3000, "프롬프트 데이터가 유효하지 않습니다"),
    NOT_FOUND(3001, "프롬프트를 찾을 수 없습니다"),
    DUPLICATE_TITLE(3002, "동일한 제목의 프롬프트가 이미 존재합니다"),
    TEMPLATE_PARSE_ERROR(3003, "프롬프트 템플릿 파싱 오류가 발생했습니다"),
    PERSISTENCE_ERROR(3004, "프롬프트 저장 중 오류가 발생했습니다"),
    TAG_PROCESSING_ERROR(3005, "태그 처리 중 오류가 발생했습니다"),
    INSUFFICIENT_PERMISSION(3006, "권한이 부족합니다"),
    PROMPT_VERSION_NOT_FOUND(3007, "프롬프트 버전을 찾을 수 없습니다"),
    UNKNOWN_ERROR(3999, "알 수 없는 오류가 발생했습니다"),
    OPERATION_FAILED(3003, "프롬프트 작업 중 오류가 발생했습니다"),
    VERSION_NOT_FOUND(3004, "프롬프트 버전을 찾을 수 없습니다"),
    VERSION_VALIDATION_ERROR(3005, "프롬프트 버전 유효성 검증에 실패했습니다");

    private final int code;
    private final String message;

    PromptErrorType(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
