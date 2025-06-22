package com.gongdel.promptserver.domain.view;

import com.gongdel.promptserver.domain.exception.ErrorCode;
import lombok.Getter;

/**
 * 조회수 기능 관련 에러 타입을 정의하는 열거형입니다.
 * <p>
 * ErrorCode 인터페이스를 구현하여 기존 예외 처리 구조와 일관성을 유지합니다.
 */
@Getter
public enum ViewErrorType implements ErrorCode {
    // 조회 기록 관련 에러 (5000번대)
    PROMPT_NOT_FOUND(5001, "프롬프트를 찾을 수 없습니다"),
    INVALID_USER_INFO(5002, "사용자 정보가 유효하지 않습니다"),
    DUPLICATE_VIEW_DETECTED(5003, "중복 조회가 감지되었습니다"),

    // Redis 관련 에러 (5100번대)
    REDIS_CONNECTION_FAILED(5101, "Redis 연결에 실패했습니다"),
    CACHE_OPERATION_FAILED(5102, "캐시 작업에 실패했습니다"),
    DUPLICATION_CHECK_FAILED(5103, "중복 체크에 실패했습니다"),

    // 데이터베이스 관련 에러 (5200번대)
    VIEW_SAVE_FAILED(5201, "조회 기록 저장에 실패했습니다"),
    VIEW_COUNT_UPDATE_FAILED(5202, "조회수 업데이트에 실패했습니다"),
    STATISTICS_LOAD_FAILED(5203, "통계 데이터 조회에 실패했습니다"),

    // 통계 관련 에러 (5300번대)
    INVALID_STATISTICS_PERIOD(5301, "통계 기간이 유효하지 않습니다"),
    STATISTICS_CALCULATION_FAILED(5302, "통계 계산에 실패했습니다"),
    TOP_VIEWED_LOAD_FAILED(5303, "인기 프롬프트 조회에 실패했습니다"),

    // 배치 작업 관련 에러 (5400번대)
    BATCH_FLUSH_FAILED(5401, "배치 플러시에 실패했습니다"),
    BATCH_SYNC_FAILED(5402, "배치 동기화에 실패했습니다"),

    // 일반적인 에러 (5999번대)
    UNKNOWN_ERROR(5999, "알 수 없는 오류가 발생했습니다");

    /**
     * 에러 코드
     */
    private final int code;

    /**
     * 에러 메시지
     */
    private final String message;

    /**
     * ViewErrorType 생성자
     *
     * @param code    에러 코드
     * @param message 에러 메시지
     */
    ViewErrorType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 에러 코드로 ViewErrorType을 찾습니다.
     *
     * @param code 에러 코드
     * @return ViewErrorType (없으면 UNKNOWN_ERROR)
     */
    public static ViewErrorType fromCode(int code) {
        for (ViewErrorType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return UNKNOWN_ERROR;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
