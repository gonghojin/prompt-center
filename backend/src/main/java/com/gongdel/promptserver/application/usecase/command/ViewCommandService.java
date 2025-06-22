package com.gongdel.promptserver.application.usecase.command;

import com.gongdel.promptserver.application.port.in.ViewCommandUseCase;
import com.gongdel.promptserver.application.port.in.command.view.RecordViewCommand;
import com.gongdel.promptserver.application.port.out.LoadViewCountFromCachePort;
import com.gongdel.promptserver.application.port.out.RecordViewPort;
import com.gongdel.promptserver.application.port.out.SaveViewLogPort;
import com.gongdel.promptserver.application.port.out.query.LoadPromptTemplateIdPort;
import com.gongdel.promptserver.domain.view.LoadViewCountQuery;
import com.gongdel.promptserver.domain.view.ViewCount;
import com.gongdel.promptserver.domain.view.ViewOperationException;
import com.gongdel.promptserver.domain.view.ViewRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 조회수 기록 관련 명령 작업을 처리하는 서비스 구현체입니다.
 * Redis 기반 실시간 중복 체크와 캐시 증가를 수행하며, 조회 로그는 비동기로 저장합니다.
 * 실제 DB 조회수 동기화는 배치 처리로 수행됩니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ViewCommandService implements ViewCommandUseCase {

    private final RecordViewPort recordViewPort;
    private final LoadViewCountFromCachePort loadViewCountFromCachePort;
    private final SaveViewLogPort saveViewLogPort;
    private final LoadPromptTemplateIdPort loadPromptTemplateIdPort;

    /**
     * 프롬프트 조회를 기록합니다.
     * 1. UUID를 Long ID로 변환
     * 2. Redis 기반 중복 체크 (1시간 TTL)
     * 3. 새로운 조회인 경우 Redis 캐시에서 조회수 증가
     * 4. 조회 로그 비동기 저장 (DB 부하 분산)
     * 5. 현재 캐시된 조회수 반환
     *
     * @param command 조회 기록 명령 객체
     * @return 업데이트된 총 조회수
     * @throws ViewOperationException 처리 중 오류 발생 시
     */
    @Override
    public long recordView(RecordViewCommand command) {
        Assert.notNull(command, "RecordViewCommand must not be null");

        log.debug("Recording view: promptUuid={}, userId={}, ipAddress={}, anonymousId={}",
            command.getPromptTemplateUuid(), command.getUserId(),
            command.getIpAddress(), command.getAnonymousId());

        try {
            // 1. UUID를 Long ID로 변환
            Long promptTemplateId = findPromptIdOrThrow(command.getPromptTemplateUuid());

            // 2. 내부 ID가 포함된 도메인 커맨드 생성
            com.gongdel.promptserver.domain.view.RecordViewCommand domainCommand = createDomainCommand(command,
                promptTemplateId);

            // 3. Redis 기반 중복 체크 및 캐시 증가 (원자적 처리)
            boolean isNewView = recordViewPort.recordView(domainCommand);

            if (isNewView) {
                // 4. 새로운 조회인 경우 조회 로그 비동기 저장 (DB 부하 분산)
                saveViewLogAsync(command, promptTemplateId);

                log.info("New view recorded in cache: promptId={}, userId={}",
                    promptTemplateId, command.getUserId());
            } else {
                log.debug("Duplicate view detected (within 1 hour): promptId={}, userId={}",
                    promptTemplateId, command.getUserId());
            }

            // 5. 현재 캐시된 조회수 조회 및 반환
            long currentCount = getCurrentViewCountFromCache(promptTemplateId);

            log.info("View processed successfully: promptId={}, userId={}, newView={}, currentCount={}",
                promptTemplateId, command.getUserId(), isNewView, currentCount);

            return currentCount;

        } catch (ViewOperationException e) {
            log.error("View recording failed: promptUuid={}, error={}",
                command.getPromptTemplateUuid(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while recording view: promptUuid={}",
                command.getPromptTemplateUuid(), e);
            throw ViewOperationException.viewSaveFailed(command.getPromptTemplateUuid(), e);
        }
    }

    /**
     * 조회 로그를 비동기로 저장합니다.
     * DB 부하를 분산하기 위해 별도 스레드에서 처리됩니다.
     *
     * @param command          조회 기록 명령
     * @param promptTemplateId 프롬프트 내부 ID
     */
    @Async
    private void saveViewLogAsync(RecordViewCommand command, Long promptTemplateId) {
        try {
            ViewRecord viewRecord = createViewRecord(command, promptTemplateId);
            // TODO: 비동기 구조로 바꾸기
            saveViewLogPort.save(viewRecord);

            log.debug("View log saved asynchronously: promptId={}, userId={}",
                promptTemplateId, command.getUserId());
        } catch (Exception e) {
            log.error("Failed to save view log asynchronously: promptId={}, userId={}",
                promptTemplateId, command.getUserId(), e);
            // 비동기 저장 실패는 전체 처리를 실패시키지 않음
        }
    }

    /**
     * 현재 캐시된 조회수를 조회합니다.
     * 캐시에 없는 경우 0을 반환합니다. (배치 처리가 DB와 동기화 담당)
     *
     * @param promptTemplateId 프롬프트 내부 ID
     * @return 현재 캐시된 조회수
     */
    private long getCurrentViewCountFromCache(Long promptTemplateId) {
        try {
            LoadViewCountQuery query = LoadViewCountQuery.of(promptTemplateId);
            return loadViewCountFromCachePort.loadViewCountFromCache(query)
                .map(ViewCount::getTotalViewCount)
                .orElse(0L);
        } catch (Exception e) {
            log.warn("Failed to get cached view count for prompt: {}, returning 0", promptTemplateId, e);
            return 0L;
        }
    }

    /**
     * 조회 기록을 생성합니다.
     *
     * @param command          조회 기록 명령
     * @param promptTemplateId 프롬프트 내부 ID
     * @return 조회 기록 객체
     */
    private ViewRecord createViewRecord(RecordViewCommand command, Long promptTemplateId) {
        LocalDateTime now = LocalDateTime.now();

        if (command.isLoggedInUser()) {
            return ViewRecord.forUser(promptTemplateId, command.getUserId(),
                command.getIpAddress(), now);
        } else {
            return ViewRecord.forGuest(promptTemplateId, command.getIpAddress(),
                command.getAnonymousId(), now);
        }
    }

    /**
     * UUID로 프롬프트 내부 ID를 조회합니다.
     *
     * @param promptTemplateUuid 프롬프트 템플릿 UUID
     * @return 프롬프트 내부 ID
     * @throws ViewOperationException 프롬프트가 존재하지 않을 때
     */
    private Long findPromptIdOrThrow(UUID promptTemplateUuid) {
        return loadPromptTemplateIdPort.findIdByUuid(promptTemplateUuid)
            .orElseThrow(() -> {
                log.error("Prompt not found for UUID: {}", promptTemplateUuid);
                return ViewOperationException.promptNotFound(promptTemplateUuid);
            });
    }

    /**
     * 애플리케이션 커맨드를 도메인 커맨드로 변환합니다.
     *
     * @param command          애플리케이션 커맨드
     * @param promptTemplateId 프롬프트 내부 ID
     * @return 도메인 커맨드
     */
    private com.gongdel.promptserver.domain.view.RecordViewCommand createDomainCommand(
        RecordViewCommand command, Long promptTemplateId) {

        if (command.isLoggedInUser()) {
            return com.gongdel.promptserver.domain.view.RecordViewCommand.forUser(
                command.getUserId(),
                command.getPromptTemplateUuid(),
                promptTemplateId,
                command.getIpAddress());
        } else {
            return com.gongdel.promptserver.domain.view.RecordViewCommand.forGuest(
                command.getPromptTemplateUuid(),
                promptTemplateId,
                command.getIpAddress(),
                command.getAnonymousId());
        }
    }
}
