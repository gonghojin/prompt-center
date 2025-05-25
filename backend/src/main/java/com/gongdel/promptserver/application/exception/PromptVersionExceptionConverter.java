package com.gongdel.promptserver.application.exception;

import com.gongdel.promptserver.domain.exception.PromptVersionDomainException;
import com.gongdel.promptserver.domain.exception.PromptVersionNotFoundDomainException;
import com.gongdel.promptserver.domain.exception.PromptVersionValidationDomainException;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * 프롬프트 버전 도메인 예외를 애플리케이션 예외로 변환하는 유틸리티 클래스입니다.
 * 헥사고날 아키텍처에서 도메인 계층의 예외를 애플리케이션 계층의 예외로 변환하는 책임을 담당합니다.
 */
@Slf4j
public final class PromptVersionExceptionConverter {

    private PromptVersionExceptionConverter() {
        // 유틸리티 클래스이므로 인스턴스화 방지
    }

    /**
     * 프롬프트 버전 도메인 예외를 애플리케이션 예외로 변환합니다.
     *
     * @param e          도메인 예외
     * @param identifier 프롬프트 버전 식별자
     * @return 변환된 애플리케이션 예외
     */
    public static RuntimeException convertToApplicationException(PromptVersionDomainException e, Object identifier) {
        log.debug("Converting domain exception to application exception for: {}", identifier);

        if (e instanceof PromptVersionNotFoundDomainException) {
            if (identifier instanceof Long) {
                return new PromptVersionNotFoundException((Long) identifier);
            } else {
                return new PromptVersionNotFoundException((UUID) identifier);
            }
        } else if (e instanceof PromptVersionValidationDomainException) {
            return new PromptVersionOperationFailedException(e.getMessage(), e);
        } else {
            // 기타 도메인 예외는 PromptVersionOperationFailedException으로 변환
            return new PromptVersionOperationFailedException(
                "An error occurred during prompt version operation: " + e.getMessage(),
                e);
        }
    }
}
