package com.gongdel.promptserver.adapter.out.persistence.command;

import com.gongdel.promptserver.adapter.out.persistence.entity.PromptVersionEntity;
import com.gongdel.promptserver.adapter.out.persistence.repository.PromptVersionRepository;
import com.gongdel.promptserver.application.port.out.command.DeletePromptVersionPort;
import com.gongdel.promptserver.application.port.out.command.SavePromptVersionPort;
import com.gongdel.promptserver.application.port.out.command.UpdatePromptVersionPort;
import com.gongdel.promptserver.domain.model.PromptVersion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 프롬프트 버전 명령 포트 구현체입니다. PromptVersionRepository를 사용하여 프롬프트 버전 저장 작업을 수행합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PromptVersionCommandAdapter
    implements SavePromptVersionPort, UpdatePromptVersionPort, DeletePromptVersionPort {

    private final PromptVersionRepository promptVersionRepository;
    private final com.gongdel.promptserver.adapter.out.persistence.mapper.PromptVersionMapper promptVersionMapper;

    /**
     * 프롬프트 버전을 저장합니다.
     *
     * @param promptVersion 저장할 프롬프트 버전 도메인 객체
     * @return 저장된 프롬프트 버전 도메인 객체
     */
    @Override
    @Transactional
    public PromptVersion savePromptVersion(PromptVersion promptVersion) {
        Assert.notNull(promptVersion, "PromptVersion must not be null");
        log.debug("Saving prompt version: {}", promptVersion);
        try {
            PromptVersionEntity entity = promptVersionMapper.toEntity(promptVersion);
            PromptVersionEntity savedEntity = promptVersionRepository.save(entity);
            PromptVersion saved = promptVersionMapper.toDomain(savedEntity);
            log.info("Prompt version saved: id={}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("Failed to save prompt version. Error: {}", e.getMessage(), e);
            throw new RuntimeException("프롬프트 버전 저장 실패", e);
        }
    }

    /**
     * 프롬프트 버전을 업데이트합니다.
     *
     * @param promptVersion 업데이트할 프롬프트 버전 도메인 객체
     * @return 업데이트된 프롬프트 버전 도메인 객체
     */
    @Override
    @Transactional
    public PromptVersion updatePromptVersion(PromptVersion promptVersion) {
        Assert.notNull(promptVersion, "PromptVersion must not be null");
        log.debug("Updating prompt version: {}", promptVersion);
        try {
            PromptVersionEntity entity = promptVersionMapper.toEntity(promptVersion);
            PromptVersionEntity updatedEntity = promptVersionRepository.save(entity);
            PromptVersion updated = promptVersionMapper.toDomain(updatedEntity);
            log.info("Prompt version updated: id={}", updated.getId());
            return updated;
        } catch (Exception e) {
            log.error("Failed to update prompt version. Error: {}", e.getMessage(), e);
            throw new RuntimeException("프롬프트 버전 업데이트 실패", e);
        }
    }

    /**
     * 프롬프트 버전을 삭제합니다.
     *
     * @param id 삭제할 프롬프트 버전의 ID
     */
    @Override
    @Transactional
    public void deletePromptVersion(Long id) {
        Assert.notNull(id, "PromptVersion id must not be null");
        log.debug("Deleting prompt version with id: {}", id);
        try {
            promptVersionRepository.deleteById(id);
            log.info("Prompt version with id: {} successfully deleted", id);
        } catch (Exception e) {
            log.error("Failed to delete prompt version with id: {}. Error: {}", id, e.getMessage(), e);
            throw new RuntimeException("프롬프트 버전 삭제 실패", e);
        }
    }
}
