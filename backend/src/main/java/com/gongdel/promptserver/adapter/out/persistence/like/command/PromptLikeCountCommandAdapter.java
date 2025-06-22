package com.gongdel.promptserver.adapter.out.persistence.like.command;

import com.gongdel.promptserver.adapter.out.persistence.entity.like.PromptLikeCountEntity;
import com.gongdel.promptserver.adapter.out.persistence.repository.PromptLikeCountRepository;
import com.gongdel.promptserver.application.port.out.like.command.UpdatePromptLikeCount;
import com.gongdel.promptserver.application.port.out.like.command.UpdatePromptLikeCountPort;
import com.gongdel.promptserver.domain.exception.LikeOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 프롬프트별 좋아요 수 Projection을 갱신하는 Command 어댑터입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class PromptLikeCountCommandAdapter implements UpdatePromptLikeCountPort {
    private final PromptLikeCountRepository promptLikeCountRepository;

    /**
     * 좋아요 수를 갱신합니다.
     *
     * @param update Projection 갱신 요청 값
     * @return 갱신된 좋아요 수
     * @throws LikeOperationException   갱신 실패 시 발생
     * @throws IllegalArgumentException 파라미터가 null/음수인 경우 발생
     */
    @Override
    public long updateLikeCount(UpdatePromptLikeCount update) {
        Assert.notNull(update, "update must not be null");
        try {
            PromptLikeCountEntity entity = promptLikeCountRepository
                .findByPromptTemplateId(update.getPromptTemplateId())
                .orElse(PromptLikeCountEntity.builder()
                    .promptTemplateId(update.getPromptTemplateId())
                    .likeCount(0L)
                    .updatedAt(LocalDateTime.now())
                    .build());
            entity.applyLikeCountChange(update.getLikeCount());
            promptLikeCountRepository.save(entity);
            log.info("Updated like count for promptTemplateId={}, likeCount={}", update.getPromptTemplateId(),
                entity.getLikeCount());
            return entity.getLikeCount();
        } catch (DataAccessException e) {
            log.error("Failed to update like count for promptTemplateId={}", update.getPromptTemplateId(), e);
            throw new LikeOperationException(
                "Failed to update like count for promptTemplateId: " + update.getPromptTemplateId(), e);
        }
    }
}
