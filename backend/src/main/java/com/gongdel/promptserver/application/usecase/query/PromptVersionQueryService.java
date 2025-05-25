package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.exception.PromptVersionExceptionConverter;
import com.gongdel.promptserver.application.exception.PromptVersionNotFoundException;
import com.gongdel.promptserver.application.exception.PromptVersionOperationFailedException;
import com.gongdel.promptserver.application.port.in.query.FindPromptVersionsUseCase;
import com.gongdel.promptserver.application.port.in.query.GetPromptVersionUseCase;
import com.gongdel.promptserver.application.port.out.query.FindPromptVersionsPort;
import com.gongdel.promptserver.application.port.out.query.LoadPromptVersionPort;
import com.gongdel.promptserver.domain.model.PromptVersion;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 프롬프트 버전 조회 유즈케이스 구현체입니다. 이 서비스는 프롬프트 버전의 조회 작업을 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromptVersionQueryService implements FindPromptVersionsUseCase, GetPromptVersionUseCase {

    private final FindPromptVersionsPort findPromptVersionsPort;
    private final LoadPromptVersionPort loadPromptVersionPort;

    /**
     * 프롬프트 템플릿 ID로 프롬프트 버전 목록을 조회합니다.
     *
     * @param promptTemplateId 조회할 프롬프트 템플릿의 ID
     * @return 프롬프트 버전 도메인 목록
     * @throws PromptVersionOperationFailedException 조회 중 오류가 발생한 경우
     */
    @Override
    public List<PromptVersion> findByPromptTemplateId(Long promptTemplateId) {
        Assert.notNull(promptTemplateId, "PromptTemplateId must not be null");
        log.debug("Querying prompt versions by template ID: {}", promptTemplateId);
        try {
            List<PromptVersion> result = findPromptVersionsPort.findPromptVersionsByPromptTemplateId(promptTemplateId);
            log.info("Prompt versions found: templateId={}, count={}", promptTemplateId, result.size());
            return result;
        } catch (com.gongdel.promptserver.domain.exception.PromptVersionDomainException e) {
            // 도메인 예외를 애플리케이션 예외로 변환
            throw PromptVersionExceptionConverter.convertToApplicationException(e, promptTemplateId);
        } catch (Exception e) {
            log.error("Unexpected error while querying prompt versions by template ID: {}", promptTemplateId, e);
            throw new com.gongdel.promptserver.application.exception.PromptVersionOperationFailedException(
                "프롬프트 버전 목록 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * UUID로 프롬프트 버전을 조회합니다.
     *
     * @param uuid 조회할 프롬프트 버전의 UUID
     * @return 프롬프트 버전 도메인
     * @throws PromptVersionNotFoundException        해당 UUID의 프롬프트 버전이 없을 때
     * @throws PromptVersionOperationFailedException 조회 중 오류가 발생한 경우
     */
    @Override
    public PromptVersion getByUuid(UUID uuid) {
        Assert.notNull(uuid, "PromptVersion UUID must not be null");
        log.debug("Querying prompt version by UUID: {}", uuid);
        try {
            return loadPromptVersionPort.loadPromptVersionByUuid(uuid)
                .map(version -> {
                    log.info("Prompt version found: uuid={}", uuid);
                    return version;
                })
                .orElseThrow(() -> {
                    log.warn("Prompt version not found: uuid={}", uuid);
                    return new com.gongdel.promptserver.application.exception.PromptVersionNotFoundException(uuid);
                });
        } catch (com.gongdel.promptserver.domain.exception.PromptVersionDomainException e) {
            // 도메인 예외를 애플리케이션 예외로 변환
            throw PromptVersionExceptionConverter
                .convertToApplicationException(e, uuid);
        } catch (Exception e) {
            log.error("Unexpected error while querying prompt version by UUID: {}", uuid, e);
            throw new com.gongdel.promptserver.application.exception.PromptVersionOperationFailedException(
                "프롬프트 버전 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}
