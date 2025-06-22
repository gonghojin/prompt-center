package com.gongdel.promptserver.adapter.out.persistence.like.query;

import com.gongdel.promptserver.adapter.out.persistence.entity.like.PromptLikeEntity;
import com.gongdel.promptserver.adapter.out.persistence.mapper.PromptLikeMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.PromptLikeJpaRepository;
import com.gongdel.promptserver.application.port.out.like.query.FindLikedPrompts;
import com.gongdel.promptserver.application.port.out.like.query.FindLikedPromptsPort;
import com.gongdel.promptserver.domain.exception.LikeOperationException;
import com.gongdel.promptserver.domain.like.LikedPromptResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 내가 좋아요한 프롬프트 목록을 조회하는 Query 어댑터입니다.
 * JPA와 MapStruct를 활용하여 도메인 객체로 변환합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromptLikeQueryAdapter implements FindLikedPromptsPort {
    private final PromptLikeJpaRepository promptLikeJpaRepository;
    private final PromptLikeMapper promptLikeMapper;

    /**
     * 내가 좋아요한 프롬프트 목록을 조회합니다.
     *
     * @param findLikedPrompts 조회 요청 값
     * @return 좋아요한 프롬프트 목록(페이징)
     * @throws LikeOperationException   조회 실패 시 발생
     * @throws IllegalArgumentException 파라미터가 null인 경우 발생
     */
    @Override
    public Page<LikedPromptResult> findLikedPrompts(FindLikedPrompts findLikedPrompts) {
        Assert.notNull(findLikedPrompts, "findLikedPrompts must not be null");
        Assert.notNull(findLikedPrompts.getUserId(), "userId must not be null");
        Assert.notNull(findLikedPrompts.getPageable(), "pageable must not be null");

        try {
            Page<PromptLikeEntity> page = promptLikeJpaRepository.findByUserId(
                findLikedPrompts.getUserId(),
                findLikedPrompts.getPageable());
            log.debug("Loaded liked prompts for userId={}, page={}", findLikedPrompts.getUserId(), page.getNumber());
            return page.map(promptLikeMapper::toDomain);
        } catch (DataAccessException e) {
            log.error("Failed to load liked prompts for userId={}", findLikedPrompts.getUserId(), e);
            throw new LikeOperationException("Failed to load liked prompts for userId: " + findLikedPrompts.getUserId(),
                e);
        } catch (Exception e) {
            log.error("Unexpected error while loading liked prompts for userId={}", findLikedPrompts.getUserId(), e);
            throw new LikeOperationException(
                "Unexpected error while loading liked prompts for userId: " + findLikedPrompts.getUserId(), e);
        }
    }
}
