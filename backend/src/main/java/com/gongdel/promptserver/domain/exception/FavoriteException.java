package com.gongdel.promptserver.domain.exception;

import java.util.UUID;

/**
 * 즐겨찾기 관련 예외를 처리하는 클래스입니다.
 */
public class FavoriteException extends FavoriteDomainException {
    public FavoriteException(PromptErrorType errorType, String message) {
        super(errorType, message);
    }

    public FavoriteException(PromptErrorType errorType, String message, Throwable cause) {
        super(errorType, message, cause);
    }

    public static FavoriteException notFound(UUID promptTemplateId) {
        return new FavoriteException(
            PromptErrorType.NOT_FOUND,
            String.format("존재하지 않는 프롬프트 UUID: %s", promptTemplateId));
    }

    public static FavoriteException internalError(String message, Throwable cause) {
        return new FavoriteException(
            PromptErrorType.UNKNOWN_ERROR,
            message,
            cause);
    }
}
