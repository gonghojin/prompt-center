package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.entity.PromptVersionEntity;
import com.gongdel.promptserver.adapter.out.persistence.mapper.PromptVersionMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.PromptVersionRepository;
import com.gongdel.promptserver.application.port.out.query.FindPromptVersionsPort;
import com.gongdel.promptserver.application.port.out.query.LoadPromptVersionPort;
import com.gongdel.promptserver.domain.model.PromptVersion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 프롬프트 버전 조회 포트 구현체입니다. PromptVersionRepository를 사용하여 프롬프트 버전 조회 작업을 수행합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromptVersionQueryAdapter implements FindPromptVersionsPort, LoadPromptVersionPort {

    private final PromptVersionRepository promptVersionRepository;
    private final PromptVersionMapper promptVersionMapper;

    /**
     * 프롬프트 템플릿 ID로 프롬프트 버전 목록을 조회합니다.
     *
     * @param promptTemplateId 조회할 프롬프트 템플릿의 ID
     * @return 프롬프트 버전 도메인 목록
     */
    @Override
    public List<PromptVersion> findPromptVersionsByPromptTemplateId(Long promptTemplateId) {
        Assert.notNull(promptTemplateId, "PromptTemplateId must not be null");
        try {
            log.debug("Finding prompt versions by template id: {}", promptTemplateId);
            List<PromptVersionEntity> entities = promptVersionRepository.findAllByPromptTemplateId(promptTemplateId);
            log.debug("Found {} prompt versions for template id: {}", entities.size(), promptTemplateId);
            return entities.stream()
                .map(promptVersionMapper::toDomain)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to find prompt versions by template id: {}. Error: {}", promptTemplateId,
                e.getMessage(),
                e);
            throw e;
        }
    }

    /**
     * UUID로 프롬프트 버전을 단건 조회합니다.
     *
     * @param uuid 조회할 프롬프트 버전의 UUID
     * @return 프롬프트 버전 도메인 Optional
     */
    @Override
    public Optional<PromptVersion> loadPromptVersionByUuid(UUID uuid) {
        Assert.notNull(uuid, "PromptVersion UUID must not be null");
        try {
            log.debug("Loading prompt version by uuid: {}", uuid);
            Optional<PromptVersionEntity> entityOpt = promptVersionRepository.findByUuid(uuid);
            if (entityOpt.isEmpty()) {
                log.info("Prompt version not found with uuid: {}", uuid);
                return Optional.empty();
            }
            log.info("Prompt version found with uuid: {}", uuid);
            return entityOpt.map(versionEntity -> promptVersionMapper.toDomain(versionEntity));
        } catch (Exception e) {
            log.error("Failed to load prompt version by uuid: {}. Error: {}", uuid, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<PromptVersion> loadPromptVersionById(Long id) {
        Assert.notNull(id, "PromptVersion ID must not be null");
        try {
            log.debug("Loading prompt version by id: {}", id);
            Optional<PromptVersionEntity> entityOpt = promptVersionRepository.findById(id);
            if (entityOpt.isEmpty()) {
                log.info("Prompt version not found with id: {}", id);
                return Optional.empty();
            }
            log.info("Prompt version found with id: {}", id);
            return entityOpt.map(promptVersionMapper::toDomain);
        } catch (Exception e) {
            log.error("Failed to load prompt version by id: {}. Error: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}
