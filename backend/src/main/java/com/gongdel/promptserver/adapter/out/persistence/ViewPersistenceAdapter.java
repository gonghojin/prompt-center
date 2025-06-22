package com.gongdel.promptserver.adapter.out.persistence;

import com.gongdel.promptserver.adapter.out.persistence.entity.view.PromptViewCountEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.view.PromptViewLogEntity;
import com.gongdel.promptserver.adapter.out.persistence.mapper.ViewPersistenceMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.PromptViewCountJpaRepository;
import com.gongdel.promptserver.adapter.out.persistence.repository.PromptViewLogJpaRepository;
import com.gongdel.promptserver.application.port.out.LoadViewCountFromStoragePort;
import com.gongdel.promptserver.application.port.out.SaveViewLogPort;
import com.gongdel.promptserver.application.port.out.UpdateViewCountPort;
import com.gongdel.promptserver.domain.view.LoadViewCountQuery;
import com.gongdel.promptserver.domain.view.ViewCount;
import com.gongdel.promptserver.domain.view.ViewOperationException;
import com.gongdel.promptserver.domain.view.ViewRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * 데이터베이스 기반 조회수 처리를 담당하는 어댑터
 * 조회 로그 저장, 중복 체크, 조회수 업데이트 등의 기능을 제공합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ViewPersistenceAdapter implements
    SaveViewLogPort,
    UpdateViewCountPort,
    LoadViewCountFromStoragePort {

    private final PromptViewLogJpaRepository viewLogRepository;
    private final PromptViewCountJpaRepository viewCountRepository;
    private final ViewPersistenceMapper mapper;

    /**
     * 조회 기록을 데이터베이스에 저장합니다.
     *
     * @param viewRecord 저장할 조회 기록
     * @return 저장된 조회 기록
     */
    @Override
    @Transactional
    public ViewRecord save(ViewRecord viewRecord) {
        try {
            Assert.notNull(viewRecord, "ViewRecord must not be null");

            log.debug("Saving view record for prompt: {}", viewRecord.getPromptTemplateId());

            PromptViewLogEntity entity = mapper.toEntity(viewRecord);
            PromptViewLogEntity savedEntity = viewLogRepository.save(entity);

            log.debug("Successfully saved view record with ID: {}", savedEntity.getId());
            return mapper.toDomain(savedEntity);

        } catch (Exception e) {
            log.error("Failed to save view record for prompt: {}", viewRecord.getPromptTemplateId(), e);
            throw ViewOperationException.viewLogSaveFailed(viewRecord.getPromptTemplateId(), e);
        }
    }

    /**
     * 조회수를 1 증가시킵니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 업데이트된 조회수 정보
     */
    @Override
    @Transactional
    public ViewCount incrementViewCount(Long promptTemplateId) {
        return incrementViewCount(promptTemplateId, 1L);
    }

    /**
     * 조회수를 지정된 값만큼 증가시킵니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @param count            증가시킬 조회수
     * @return 업데이트된 조회수 정보
     */
    @Override
    @Transactional
    public ViewCount incrementViewCount(Long promptTemplateId, long count) {
        try {
            Assert.notNull(promptTemplateId, "promptTemplateId must not be null");
            Assert.isTrue(count > 0, "count must be positive");

            log.debug("Incrementing view count for prompt {} by {}", promptTemplateId, count);

            // 원자적 업데이트 시도
            int updatedRows = viewCountRepository.incrementViewCount(promptTemplateId, count);

            if (updatedRows > 0) {
                // 업데이트 성공 - 최신 데이터 조회
                Optional<PromptViewCountEntity> updated = viewCountRepository.findByPromptTemplateId(promptTemplateId);
                if (updated.isPresent()) {
                    log.debug("Successfully incremented view count for prompt {}", promptTemplateId);
                    return mapper.toDomain(updated.get());
                }
            }

            // 업데이트 실패 - 새로운 레코드 생성
            log.debug("Creating new view count record for prompt {}", promptTemplateId);
            PromptViewCountEntity newEntity = PromptViewCountEntity.createInitial(promptTemplateId);
            if (count > 1) {
                newEntity.incrementViewCount(count - 1); // 이미 1로 초기화되어 있으므로 count-1만 추가
            }

            PromptViewCountEntity savedEntity = viewCountRepository.save(newEntity);
            return mapper.toDomain(savedEntity);

        } catch (Exception e) {
            log.error("Failed to increment view count for prompt {}", promptTemplateId, e);
            throw ViewOperationException.viewCountUpdateFailed(promptTemplateId, e);
        }
    }

    /**
     * 조회수 정보를 저장하거나 업데이트합니다.
     *
     * @param viewCount 저장할 조회수 정보
     * @return 저장된 조회수 정보
     */
    @Override
    @Transactional
    public ViewCount saveOrUpdate(ViewCount viewCount) {
        try {
            Assert.notNull(viewCount, "ViewCount must not be null");

            log.debug("Saving or updating view cㅇount for prompt: {}", viewCount.getPromptTemplateId());

            PromptViewCountEntity entity = mapper.toEntity(viewCount);
            PromptViewCountEntity savedEntity = viewCountRepository.save(entity);

            log.debug("Successfully saved view count for prompt {}", viewCount.getPromptTemplateId());
            return mapper.toDomain(savedEntity);

        } catch (Exception e) {
            log.error("Failed to save view count for prompt {}", viewCount.getPromptTemplateId(), e);
            throw ViewOperationException.viewCountSaveFailed(viewCount.getPromptTemplateId(), e);
        }
    }

    /**
     * 데이터베이스에서 조회수를 조회합니다.
     *
     * @param query 조회수 조회 쿼리
     * @return 저장된 조회수 정보 (없으면 Optional.empty())
     */
    @Override
    public Optional<ViewCount> loadViewCountFromStorage(LoadViewCountQuery query) {
        try {
            Assert.notNull(query, "LoadViewCountQuery must not be null");

            log.debug("Loading view count from storage for prompt: {}", query.getPromptTemplateId());

            return loadViewCountFromStorage(query.getPromptTemplateId());

        } catch (Exception e) {
            log.error("Failed to load view count from storage for prompt: {}", query.getPromptTemplateId(), e);
            throw ViewOperationException.viewCountLoadFailed(query.getPromptTemplateId(), e);
        }
    }

    /**
     * 프롬프트 템플릿 ID로 데이터베이스에서 조회수를 조회합니다.
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 저장된 조회수 정보 (없으면 Optional.empty())
     */
    @Override
    public Optional<ViewCount> loadViewCountFromStorage(Long promptTemplateId) {
        try {
            Assert.notNull(promptTemplateId, "promptTemplateId must not be null");

            log.debug("Loading view count from storage for prompt: {}", promptTemplateId);

            Optional<PromptViewCountEntity> entity = viewCountRepository.findByPromptTemplateId(promptTemplateId);

            if (entity.isPresent()) {
                ViewCount viewCount = mapper.toDomain(entity.get());
                log.debug("Found view count in storage for prompt: {}, count: {}",
                    promptTemplateId, viewCount.getTotalViewCount());
                return Optional.of(viewCount);
            } else {
                log.debug("No view count found in storage for prompt: {}", promptTemplateId);
                return Optional.empty();
            }

        } catch (Exception e) {
            log.error("Failed to load view count from storage for prompt: {}", promptTemplateId, e);
            throw ViewOperationException.viewCountLoadFailed(promptTemplateId, e);
        }
    }

    /**
     * UpdateViewCountPort를 위한 조회수 정보 조회 메서드
     * (내부적으로 loadViewCountFromStorage를 호출)
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 조회수 정보 (없는 경우 Optional.empty())
     */
    @Override
    public Optional<ViewCount> findByPromptTemplateId(Long promptTemplateId) {
        return loadViewCountFromStorage(promptTemplateId);
    }
}
