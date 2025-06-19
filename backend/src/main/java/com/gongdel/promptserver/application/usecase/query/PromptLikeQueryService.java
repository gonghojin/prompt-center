package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.port.in.query.like.PromptLikeQueryUseCase;
import com.gongdel.promptserver.application.port.out.like.query.*;
import com.gongdel.promptserver.domain.exception.BaseException;
import com.gongdel.promptserver.domain.exception.LikeOperationException;
import com.gongdel.promptserver.domain.like.LikeStatus;
import com.gongdel.promptserver.domain.like.LikedPromptResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 프롬프트 좋아요 조회 유즈케이스 구현체입니다.
 * 좋아요 상태, 카운트, 내가 좋아요한 프롬프트 목록을 조회합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromptLikeQueryService implements PromptLikeQueryUseCase {

    private final LoadPromptLikeStatusPort loadPromptLikeStatusPort;
    private final LoadPromptLikeCountPort loadPromptLikeCountPort;
    private final FindLikedPromptsPort findLikedPromptsPort;

    /**
     * 프롬프트 좋아요 상태 및 카운트를 조회합니다.
     *
     * @param loadPromptLikeStatus 좋아요 상태 조회 요청 값
     * @return 좋아요 상태 및 카운트
     * @throws BaseException 비즈니스 예외
     */
    public LikeStatus getLikeStatus(LoadPromptLikeStatus loadPromptLikeStatus) {
        Assert.notNull(loadPromptLikeStatus, "LoadPromptLikeStatus must not be null");
        log.debug("Querying like status: userId={}, promptTemplateId={}",
            loadPromptLikeStatus.getUserId(), loadPromptLikeStatus.getPromptTemplateId());
        try {
            return loadPromptLikeStatusPort.loadStatus(loadPromptLikeStatus);
        } catch (BaseException e) {
            log.error(
                "Business exception occurred while querying like status. userId={}, promptTemplateId={}, message={}",
                loadPromptLikeStatus.getUserId(), loadPromptLikeStatus.getPromptTemplateId(), e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while querying like status. userId={}, promptTemplateId={}",
                loadPromptLikeStatus.getUserId(), loadPromptLikeStatus.getPromptTemplateId(), e);
            throw new LikeOperationException("Failed to query like status", e);
        }
    }

    /**
     * 프롬프트별 좋아요 수를 조회합니다.
     *
     * @param promptTemplateId 프롬프트 ID
     * @return 좋아요 수
     * @throws BaseException 비즈니스 예외
     */
    public long getLikeCount(Long promptTemplateId) {
        Assert.notNull(promptTemplateId, "PromptTemplate ID must not be null");
        log.debug("Querying like count: promptTemplateId={}", promptTemplateId);
        try {
            return loadPromptLikeCountPort.loadLikeCount(promptTemplateId);
        } catch (BaseException e) {
            log.error("Business exception occurred while querying like count. promptTemplateId={}, message={}",
                promptTemplateId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while querying like count. promptTemplateId={}", promptTemplateId, e);
            throw new LikeOperationException("Failed to query like count", e);
        }
    }

    /**
     * 내가 좋아요한 프롬프트 목록을 조회합니다.
     *
     * @param findLikedPrompts 좋아요한 프롬프트 목록 조회 요청 값
     * @return 좋아요한 프롬프트 목록(페이징)
     * @throws BaseException 비즈니스 예외
     */
    public Page<LikedPromptResult> getLikedPrompts(FindLikedPrompts findLikedPrompts) {
        Assert.notNull(findLikedPrompts, "FindLikedPrompts must not be null");
        Assert.notNull(findLikedPrompts.getUserId(), "User ID must not be null");
        log.debug("Querying liked prompts: userId={}, pageable={}",
            findLikedPrompts.getUserId(), findLikedPrompts.getPageable());
        try {
            return findLikedPromptsPort.findLikedPrompts(findLikedPrompts);
        } catch (BaseException e) {
            log.error("Business exception occurred while querying liked prompts. userId={}, message={}",
                findLikedPrompts.getUserId(), e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while querying liked prompts. userId={}",
                findLikedPrompts.getUserId(), e);
            throw new LikeOperationException("Failed to query liked prompts", e);
        }
    }
}
