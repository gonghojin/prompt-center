package com.gongdel.promptserver.adapter.out.persistence.like.query;

import com.gongdel.promptserver.adapter.out.persistence.repository.PromptLikeCountRepository;
import com.gongdel.promptserver.application.port.out.like.query.LoadPromptLikeCountPort;
import com.gongdel.promptserver.domain.exception.LikeOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 프롬프트별 좋아요 수 Projection을 조회하는 Query 어댑터입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromptLikeCountQueryAdapter implements LoadPromptLikeCountPort {
    private final PromptLikeCountRepository promptLikeCountRepository;

    /**
     * 좋아요 수를 조회합니다.
     *
     * @param promptTemplateId 프롬프트 ID
     * @return 좋아요 수
     * @throws LikeOperationException   조회 실패 시 발생
     * @throws IllegalArgumentException 파라미터가 null인 경우 발생
     */
    @Override
    public long loadLikeCount(Long promptTemplateId) {
        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");
        try {
            return promptLikeCountRepository
                .findByPromptTemplateId(promptTemplateId)
                .map(e -> e.getLikeCount() != null ? e.getLikeCount() : 0L)
                .orElse(0L);
        } catch (DataAccessException e) {
            log.error("Failed to load like count for promptTemplateId={}", promptTemplateId, e);
            throw new LikeOperationException("Failed to load like count for promptTemplateId: " + promptTemplateId, e);
        }
    }
}
