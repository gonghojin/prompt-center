package com.gongdel.promptserver.adapter.out.persistence.like.command;

import com.gongdel.promptserver.adapter.out.persistence.entity.PromptTemplateEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.UserEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.like.PromptLikeEntity;
import com.gongdel.promptserver.adapter.out.persistence.repository.PromptLikeJpaRepository;
import com.gongdel.promptserver.application.port.out.like.command.AddPromptLike;
import com.gongdel.promptserver.application.port.out.like.command.AddPromptLikePort;
import com.gongdel.promptserver.application.port.out.like.command.RemovePromptLike;
import com.gongdel.promptserver.application.port.out.like.command.RemovePromptLikePort;
import com.gongdel.promptserver.domain.exception.LikeOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 프롬프트 좋아요 추가/취소를 처리하는 Command 어댑터입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class PromptLikeCommandAdapter implements AddPromptLikePort, RemovePromptLikePort {
    private final PromptLikeJpaRepository promptLikeJpaRepository;

    /**
     * 프롬프트 좋아요를 추가합니다. (멱등성 보장)
     *
     * @param addPromptLike 좋아요 추가 요청 값
     * @throws LikeOperationException   추가 실패 시 발생
     * @throws IllegalArgumentException 파라미터가 null인 경우 발생
     */
    @Override
    public void addLike(AddPromptLike addPromptLike) {
        Assert.notNull(addPromptLike, "addPromptLike must not be null");
        try {
            boolean exists = promptLikeJpaRepository.existsByUserIdAndPromptTemplateId(
                addPromptLike.getUserId(), addPromptLike.getPromptTemplateId());
            if (exists) {
                log.info("Like already exists for userId={}, promptTemplateId={}", addPromptLike.getUserId(),
                    addPromptLike.getPromptTemplateId());
                return;
            }
            // 실제 UserEntity, PromptTemplateEntity 조회 및 생성 생략(실제 구현시 필요)
            PromptLikeEntity entity = PromptLikeEntity.builder()
                .user(new UserEntity(addPromptLike.getUserId()))
                .promptTemplate(new PromptTemplateEntity(addPromptLike.getPromptTemplateId()))
                .build();
            promptLikeJpaRepository.save(entity);
            log.info("Like added for userId={}, promptTemplateId={}", addPromptLike.getUserId(),
                addPromptLike.getPromptTemplateId());
        } catch (DataAccessException e) {
            log.error("Failed to add like for userId={}, promptTemplateId={}", addPromptLike.getUserId(),
                addPromptLike.getPromptTemplateId(), e);
            throw new LikeOperationException("Failed to add like for userId: " + addPromptLike.getUserId(), e);
        } catch (Exception e) {
            log.error("Unexpected error while adding like for userId={}, promptTemplateId={}",
                addPromptLike.getUserId(), addPromptLike.getPromptTemplateId(), e);
            throw new LikeOperationException(
                "Unexpected error while adding like for userId: " + addPromptLike.getUserId(), e);
        }
    }

    /**
     * 프롬프트 좋아요를 취소합니다. (멱등성 보장)
     *
     * @param removePromptLike 좋아요 취소 요청 값
     * @throws LikeOperationException   삭제 실패 시 발생
     * @throws IllegalArgumentException 파라미터가 null인 경우 발생
     */
    @Override
    public void removeLike(RemovePromptLike removePromptLike) {
        Assert.notNull(removePromptLike, "removePromptLike must not be null");
        try {
            promptLikeJpaRepository.deleteByUserIdAndPromptTemplateId(
                removePromptLike.getUserId(), removePromptLike.getPromptTemplateId());
            log.info("Like removed for userId={}, promptTemplateId={}", removePromptLike.getUserId(),
                removePromptLike.getPromptTemplateId());
        } catch (DataAccessException e) {
            log.error("Failed to remove like for userId={}, promptTemplateId={}", removePromptLike.getUserId(),
                removePromptLike.getPromptTemplateId(), e);
            throw new LikeOperationException("Failed to remove like for userId: " + removePromptLike.getUserId(), e);
        } catch (Exception e) {
            log.error("Unexpected error while removing like for userId={}, promptTemplateId={}",
                removePromptLike.getUserId(), removePromptLike.getPromptTemplateId(), e);
            throw new LikeOperationException(
                "Unexpected error while removing like for userId: " + removePromptLike.getUserId(), e);
        }
    }
}
