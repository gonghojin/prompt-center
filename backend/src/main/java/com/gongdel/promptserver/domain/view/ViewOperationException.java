package com.gongdel.promptserver.domain.view;

import java.util.UUID;

/**
 * 조회수 기능 관련 비즈니스 예외 클래스입니다.
 * <p>
 * 기존 AuthException 패턴을 참조하여 설계되었습니다.
 */
public class ViewOperationException extends ViewDomainException {

    /**
     * ViewOperationException을 생성합니다.
     *
     * @param errorType 에러 타입
     * @param message   예외 메시지
     */
    public ViewOperationException(ViewErrorType errorType, String message) {
        super(errorType, message);
    }

    /**
     * ViewOperationException을 생성합니다.
     *
     * @param errorType 에러 타입
     * @param message   예외 메시지
     * @param cause     원인 예외
     */
    public ViewOperationException(ViewErrorType errorType, String message, Throwable cause) {
        super(errorType, message, cause);
    }

    // === 정적 팩토리 메서드 ===

    /**
     * 프롬프트를 찾을 수 없는 예외를 생성합니다.
     *
     * @param promptUuid 프롬프트 UUID
     * @return ViewOperationException
     */
    public static ViewOperationException promptNotFound(UUID promptUuid) {
        return new ViewOperationException(
            ViewErrorType.PROMPT_NOT_FOUND,
            String.format("프롬프트를 찾을 수 없습니다: %s", promptUuid));
    }

    /**
     * 중복 조회 감지 예외를 생성합니다.
     *
     * @param duplicationKey 중복 체크 키
     * @return ViewOperationException
     */
    public static ViewOperationException duplicateViewDetected(String duplicationKey) {
        return new ViewOperationException(
            ViewErrorType.DUPLICATE_VIEW_DETECTED,
            String.format("중복 조회가 감지되었습니다: %s", duplicationKey));
    }

    /**
     * Redis 연결 실패 예외를 생성합니다.
     *
     * @return ViewOperationException
     */
    public static ViewOperationException redisConnectionFailed() {
        return new ViewOperationException(
            ViewErrorType.REDIS_CONNECTION_FAILED,
            "Redis 서버에 연결할 수 없습니다");
    }

    /**
     * Redis 연결 실패 예외를 생성합니다.
     *
     * @param cause 원인 예외
     * @return ViewOperationException
     */
    public static ViewOperationException redisConnectionFailed(Throwable cause) {
        return new ViewOperationException(
            ViewErrorType.REDIS_CONNECTION_FAILED,
            "Redis 서버에 연결할 수 없습니다",
            cause);
    }

    /**
     * 중복 체크 실패 예외를 생성합니다.
     *
     * @param duplicationKey 중복 체크 키
     * @param cause          원인 예외
     * @return ViewOperationException
     */
    public static ViewOperationException duplicationCheckFailed(String duplicationKey, Throwable cause) {
        return new ViewOperationException(
            ViewErrorType.DUPLICATION_CHECK_FAILED,
            String.format("중복 체크에 실패했습니다: %s", duplicationKey),
            cause);
    }

    /**
     * 조회 기록 저장 실패 예외를 생성합니다.
     *
     * @param promptUuid 프롬프트 UUID
     * @return ViewOperationException
     */
    public static ViewOperationException viewSaveFailed(UUID promptUuid) {
        return new ViewOperationException(
            ViewErrorType.VIEW_SAVE_FAILED,
            String.format("조회 기록 저장에 실패했습니다: %s", promptUuid));
    }

    /**
     * 조회 기록 저장 실패 예외를 생성합니다.
     *
     * @param promptUuid 프롬프트 UUID
     * @param cause      원인 예외
     * @return ViewOperationException
     */
    public static ViewOperationException viewSaveFailed(UUID promptUuid, Throwable cause) {
        return new ViewOperationException(
            ViewErrorType.VIEW_SAVE_FAILED,
            String.format("조회 기록 저장에 실패했습니다: %s", promptUuid),
            cause);
    }

    /**
     * 조회수 업데이트 실패 예외를 생성합니다.
     *
     * @param promptId 프롬프트 ID
     * @return ViewOperationException
     */
    public static ViewOperationException viewCountUpdateFailed(Long promptId) {
        return new ViewOperationException(
            ViewErrorType.VIEW_COUNT_UPDATE_FAILED,
            String.format("조회수 업데이트에 실패했습니다: %d", promptId));
    }

    /**
     * 조회수 업데이트 실패 예외를 생성합니다.
     *
     * @param promptId 프롬프트 ID
     * @param cause    원인 예외
     * @return ViewOperationException
     */
    public static ViewOperationException viewCountUpdateFailed(Long promptId, Throwable cause) {
        return new ViewOperationException(
            ViewErrorType.VIEW_COUNT_UPDATE_FAILED,
            String.format("조회수 업데이트에 실패했습니다: %d", promptId),
            cause);
    }

    /**
     * 통계 데이터 조회 실패 예외를 생성합니다.
     *
     * @return ViewOperationException
     */
    public static ViewOperationException statisticsLoadFailed() {
        return new ViewOperationException(
            ViewErrorType.STATISTICS_LOAD_FAILED,
            "통계 데이터 조회에 실패했습니다");
    }

    /**
     * 통계 데이터 조회 실패 예외를 생성합니다.
     *
     * @param cause 원인 예외
     * @return ViewOperationException
     */
    public static ViewOperationException statisticsLoadFailed(Throwable cause) {
        return new ViewOperationException(
            ViewErrorType.STATISTICS_LOAD_FAILED,
            "통계 데이터 조회에 실패했습니다",
            cause);
    }

    /**
     * 통계 기간이 유효하지 않은 예외를 생성합니다.
     *
     * @param period 잘못된 기간 정보
     * @return ViewOperationException
     */
    public static ViewOperationException invalidStatisticsPeriod(String period) {
        return new ViewOperationException(
            ViewErrorType.INVALID_STATISTICS_PERIOD,
            String.format("통계 기간이 유효하지 않습니다: %s", period));
    }

    /**
     * 인기 프롬프트 조회 실패 예외를 생성합니다.
     *
     * @return ViewOperationException
     */
    public static ViewOperationException topViewedLoadFailed() {
        return new ViewOperationException(
            ViewErrorType.TOP_VIEWED_LOAD_FAILED,
            "인기 프롬프트 조회에 실패했습니다");
    }

    /**
     * 인기 프롬프트 조회 실패 예외를 생성합니다.
     *
     * @param cause 원인 예외
     * @return ViewOperationException
     */
    public static ViewOperationException topViewedLoadFailed(Throwable cause) {
        return new ViewOperationException(
            ViewErrorType.TOP_VIEWED_LOAD_FAILED,
            "인기 프롬프트 조회에 실패했습니다",
            cause);
    }

    /**
     * 배치 플러시 실패 예외를 생성합니다.
     *
     * @param batchSize 배치 크기
     * @return ViewOperationException
     */
    public static ViewOperationException batchFlushFailed(int batchSize) {
        return new ViewOperationException(
            ViewErrorType.BATCH_FLUSH_FAILED,
            String.format("배치 플러시에 실패했습니다 (크기: %d)", batchSize));
    }

    /**
     * 배치 플러시 실패 예외를 생성합니다.
     *
     * @param batchSize 배치 크기
     * @param cause     원인 예외
     * @return ViewOperationException
     */
    public static ViewOperationException batchFlushFailed(int batchSize, Throwable cause) {
        return new ViewOperationException(
            ViewErrorType.BATCH_FLUSH_FAILED,
            String.format("배치 플러시에 실패했습니다 (크기: %d)", batchSize),
            cause);
    }

    /**
     * Redis 작업 실패 예외를 생성합니다.
     *
     * @param message 예외 메시지
     * @param cause   원인 예외
     * @return ViewOperationException
     */
    public static ViewOperationException redisOperationFailed(String message, Throwable cause) {
        return new ViewOperationException(
            ViewErrorType.CACHE_OPERATION_FAILED,
            message,
            cause);
    }

    /**
     * Redis 직렬화 실패 예외 생성
     */
    public static ViewOperationException redisSerializationFailed(String objectType, String message) {
        return new ViewOperationException(
            ViewErrorType.CACHE_OPERATION_FAILED,
            String.format("Failed to serialize %s to Redis format: %s", objectType, message));
    }

    /**
     * Redis 역직렬화 실패 예외를 생성합니다.
     *
     * @param objectType 객체 타입
     * @param message    메시지
     * @return ViewOperationException
     */
    public static ViewOperationException redisDeserializationFailed(String objectType, String message) {
        return new ViewOperationException(
            ViewErrorType.CACHE_OPERATION_FAILED,
            String.format("Redis 역직렬화에 실패했습니다 [%s]: %s", objectType, message));
    }

    /**
     * 조회 로그 저장 실패 예외를 생성합니다.
     *
     * @param promptId 프롬프트 ID
     * @param cause    원인 예외
     * @return ViewOperationException
     */
    public static ViewOperationException viewLogSaveFailed(Long promptId, Throwable cause) {
        return new ViewOperationException(
            ViewErrorType.VIEW_SAVE_FAILED,
            String.format("조회 로그 저장에 실패했습니다: %d", promptId),
            cause);
    }

    /**
     * 중복 체크 실패 예외를 생성합니다.
     *
     * @param promptId 프롬프트 ID
     * @param cause    원인 예외
     * @return ViewOperationException
     */
    public static ViewOperationException duplicateCheckFailed(Long promptId, Throwable cause) {
        return new ViewOperationException(
            ViewErrorType.DUPLICATION_CHECK_FAILED,
            String.format("중복 체크에 실패했습니다 (프롬프트 ID: %d)", promptId),
            cause);
    }

    /**
     * 조회수 저장 실패 예외를 생성합니다.
     *
     * @param promptId 프롬프트 ID
     * @param cause    원인 예외
     * @return ViewOperationException
     */
    public static ViewOperationException viewCountSaveFailed(Long promptId, Throwable cause) {
        return new ViewOperationException(
            ViewErrorType.VIEW_COUNT_UPDATE_FAILED,
            String.format("조회수 저장에 실패했습니다: %d", promptId),
            cause);
    }

    /**
     * 조회수 조회 실패 예외를 생성합니다.
     *
     * @param promptId 프롬프트 ID
     * @param cause    원인 예외
     * @return ViewOperationException
     */
    public static ViewOperationException viewCountLoadFailed(Long promptId, Throwable cause) {
        return new ViewOperationException(
            ViewErrorType.STATISTICS_LOAD_FAILED,
            String.format("조회수 조회에 실패했습니다: %d", promptId),
            cause);
    }
}
