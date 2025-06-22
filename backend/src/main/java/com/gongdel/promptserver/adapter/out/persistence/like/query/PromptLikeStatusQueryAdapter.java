package com.gongdel.promptserver.adapter.out.persistence.like.query;

import com.gongdel.promptserver.adapter.out.persistence.repository.PromptLikeJpaRepository;
import com.gongdel.promptserver.application.port.out.like.query.LoadPromptLikeStatus;
import com.gongdel.promptserver.application.port.out.like.query.LoadPromptLikeStatusPort;
import com.gongdel.promptserver.domain.exception.LikeOperationException;
import com.gongdel.promptserver.domain.like.LikeStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 프롬프트 좋아요 상태 및 카운트 조회 Query 어댑터입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromptLikeStatusQueryAdapter implements LoadPromptLikeStatusPort {
    private final PromptLikeJpaRepository promptLikeJpaRepository;

    /**
     * 프롬프트 좋아요 상태 및 카운트를 조회합니다.
     *
     * @param query 조회 요청 값
     * @return 좋아요 상태 및 카운트 도메인 객체
     * @throws LikeOperationException   조회 실패 시 발생
     * @throws IllegalArgumentException 파라미터가 null인 경우 발생
     */
    @Override
    public LikeStatus loadStatus(LoadPromptLikeStatus query) {
        Assert.notNull(query, "query must not be null");
        Assert.notNull(query.getUserId(), "userId must not be null");
        Assert.notNull(query.getPromptTemplateId(), "promptTemplateId must not be null");
        try {
            boolean liked = promptLikeJpaRepository.existsByUserIdAndPromptTemplateId(
                query.getUserId(), query.getPromptTemplateId());
            long likeCount = promptLikeJpaRepository.countByPromptTemplateId(query.getPromptTemplateId());
            log.debug("Loaded like status for userId={}, promptTemplateId={}, liked={}, likeCount={}",
                query.getUserId(), query.getPromptTemplateId(), liked, likeCount);
            return LikeStatus.builder()
                .liked(liked)
                .likeCount(likeCount)
                .build();
        } catch (DataAccessException e) {
            log.error("Failed to load like status for userId={}, promptTemplateId={}", query.getUserId(),
                query.getPromptTemplateId(), e);
            throw new LikeOperationException("Failed to load like status for userId: " + query.getUserId(), e);
        } catch (Exception e) {
            log.error("Unexpected error while loading like status for userId={}, promptTemplateId={}",
                query.getUserId(), query.getPromptTemplateId(), e);
            throw new LikeOperationException(
                "Unexpected error while loading like status for userId: " + query.getUserId(), e);
        }
    }
}
