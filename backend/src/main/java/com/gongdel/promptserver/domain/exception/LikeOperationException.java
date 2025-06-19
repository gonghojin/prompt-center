package com.gongdel.promptserver.domain.exception;

import lombok.Getter;

import java.util.UUID;

/**
 * 프롬프트 좋아요 관련 비즈니스 예외입니다.
 */
@Getter
public class LikeOperationException extends BaseException {

    public LikeOperationException(String message) {
        super(LikeErrorType.INTERNAL_SERVER_ERROR, message);
    }

    public LikeOperationException(PromptErrorType errorType, String message) {
        super(errorType, message);
    }

    public LikeOperationException(String message, Throwable cause) {
        super(LikeErrorType.INTERNAL_SERVER_ERROR, message, cause);
    }

    public static LikeOperationException notFound(UUID promptTemplateId) {
        return new LikeOperationException(
            PromptErrorType.NOT_FOUND,
            String.format("존재하지 않는 프롬프트 UUID: %s", promptTemplateId));
    }
}
