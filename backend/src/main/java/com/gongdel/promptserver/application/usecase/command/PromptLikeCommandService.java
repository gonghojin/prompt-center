package com.gongdel.promptserver.application.usecase.command;

import com.gongdel.promptserver.application.port.in.command.like.PromptLikeCommandUseCase;
import com.gongdel.promptserver.application.port.out.like.command.*;
import com.gongdel.promptserver.application.port.out.query.LoadPromptTemplateIdPort;
import com.gongdel.promptserver.domain.exception.BaseException;
import com.gongdel.promptserver.domain.exception.LikeOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * 프롬프트 좋아요 명령 유즈케이스 구현체입니다.
 * 좋아요 추가/취소 및 Projection 갱신을 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PromptLikeCommandService implements PromptLikeCommandUseCase {

    private final AddPromptLikePort addPromptLikePort;
    private final RemovePromptLikePort removePromptLikePort;
    private final UpdatePromptLikeCountPort updatePromptLikeCountPort;
    private final LoadPromptTemplateIdPort loadPromptTemplateIdPort;

    /**
     * 프롬프트에 좋아요를 추가합니다.
     *
     * @param userId             사용자 ID
     * @param promptTemplateUuid 프롬프트 템플릿 ID
     * @return 갱신된 좋아요 수
     * @throws BaseException 비즈니스 예외
     */
    public long addLike(Long userId, UUID promptTemplateUuid) {
        Assert.notNull(userId, "User ID must not be null");
        Assert.notNull(promptTemplateUuid, "PromptTemplate ID must not be null");
        log.debug("Adding like: userId={}, promptTemplateId={}", userId, promptTemplateUuid);
        try {
            Long promptId = findPromptIdOrThrow(promptTemplateUuid);
            AddPromptLike addPromptLike = AddPromptLike.of(userId, promptId);
            addPromptLikePort.addLike(addPromptLike);
            long likeCount = updatePromptLikeCountPort.updateLikeCount(
                UpdatePromptLikeCount.increment(promptId));
            log.info("Like added successfully. userId={}, promptTemplateId={}, likeCount={}",
                addPromptLike.getUserId(), addPromptLike.getPromptTemplateId(), likeCount);
            return likeCount;
        } catch (Exception e) {
            log.error("Unexpected error while adding like. userId={}, promptTemplateId={}",
                userId, promptTemplateUuid, e);
            throw new LikeOperationException("Failed to add like", e);
        }
    }

    /**
     * 프롬프트 좋아요를 취소합니다.
     *
     * @param userId             사용자 ID
     * @param promptTemplateUuid 프롬프트 템플릿 UUID
     * @return 갱신된 좋아요 수
     * @throws BaseException 비즈니스 예외
     */
    public long removeLike(Long userId, UUID promptTemplateUuid) {
        Assert.notNull(userId, "User ID must not be null");
        Assert.notNull(promptTemplateUuid, "PromptTemplate ID must not be null");
        log.debug("Removing like: userId={}, promptTemplateId={}", userId, promptTemplateUuid);
        try {
            Long promptId = findPromptIdOrThrow(promptTemplateUuid);
            RemovePromptLike removePromptLike = RemovePromptLike.of(userId, promptId);
            removePromptLikePort.removeLike(removePromptLike);
            long likeCount = updatePromptLikeCountPort.updateLikeCount(
                UpdatePromptLikeCount.decrement(promptId));
            log.info("Like removed successfully. userId={}, promptTemplateId={}, likeCount={}",
                removePromptLike.getUserId(), removePromptLike.getPromptTemplateId(), likeCount);
            return likeCount;
        } catch (Exception e) {
            log.error("Unexpected error while removing like. userId={}, promptTemplateId={}",
                userId, promptTemplateUuid, e);
            throw new LikeOperationException("Failed to remove like", e);
        }
    }

    /**
     * 프롬프트 UUID로 내부 ID를 조회합니다. 없으면 LikeOperationException.notFound 예외를 발생시킵니다.
     *
     * @param promptTemplateUuid 프롬프트 템플릿 UUID
     * @return 프롬프트 내부 ID
     * @throws LikeOperationException 프롬프트가 존재하지 않을 때 발생
     */
    private Long findPromptIdOrThrow(UUID promptTemplateUuid) {
        return loadPromptTemplateIdPort.findIdByUuid(promptTemplateUuid)
            .orElseThrow(() -> {
                log.error("Prompt not found for UUID: {}", promptTemplateUuid);
                throw LikeOperationException.notFound(promptTemplateUuid);
            });
    }
}
