package com.gongdel.promptserver.application.usecase.command;

import com.gongdel.promptserver.application.port.in.command.FavoriteCommandUseCase;
import com.gongdel.promptserver.application.port.out.command.DeleteFavoritePort;
import com.gongdel.promptserver.application.port.out.command.SaveFavoritePort;
import com.gongdel.promptserver.application.port.out.query.LoadPromptTemplateIdPort;
import com.gongdel.promptserver.domain.exception.FavoriteException;
import com.gongdel.promptserver.domain.model.favorite.Favorite;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 즐겨찾기 관련 Command 유스케이스 구현체입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteCommandService implements FavoriteCommandUseCase {

    private final SaveFavoritePort saveFavoritePort;
    private final DeleteFavoritePort deleteFavoritePort;
    private final LoadPromptTemplateIdPort loadPromptTemplateIdPort;

    /**
     * 프롬프트를 즐겨찾기에 추가합니다.
     *
     * @param userId             사용자 ID (null 불가)
     * @param promptTemplateUuid 프롬프트 템플릿 UUID (null 불가)
     * @return 저장된 Favorite 엔티티
     * @throws FavoriteException 내부 오류 또는 프롬프트 미존재 시 발생
     */
    @Override
    public Favorite addFavorite(Long userId, UUID promptTemplateUuid) {
        validateUserIdAndPromptTemplateUuid(userId, promptTemplateUuid);
        Long promptId = findPromptIdOrThrow(promptTemplateUuid);
        Favorite favorite = Favorite.builder()
            .userId(userId)
            .promptTemplateId(promptId)
            .createdAt(LocalDateTime.now())
            .build();
        try {
            Favorite saved = saveFavoritePort.save(favorite);
            log.info("Favorite added successfully: userId={}, promptTemplateUuid={}", userId, promptTemplateUuid);
            return saved;
        } catch (Exception e) {
            log.error("Failed to add favorite: userId={}, promptTemplateUuid={}, error={}", userId, promptTemplateUuid,
                e.getMessage(), e);
            throw FavoriteException.internalError("Failed to add favorite", e);
        }
    }

    /**
     * 프롬프트 즐겨찾기를 삭제합니다.
     *
     * @param userId             사용자 ID (null 불가)
     * @param promptTemplateUuid 프롬프트 템플릿 UUID (null 불가)
     * @throws FavoriteException 내부 오류 또는 프롬프트 미존재 시 발생
     */
    @Override
    public void removeFavorite(Long userId, UUID promptTemplateUuid) {
        validateUserIdAndPromptTemplateUuid(userId, promptTemplateUuid);
        Long promptId = findPromptIdOrThrow(promptTemplateUuid);
        try {
            deleteFavoritePort.deleteByUserIdAndPromptTemplateId(userId, promptId);
            log.info("Favorite removed successfully: userId={}, promptTemplateUuid={}", userId, promptTemplateUuid);
        } catch (Exception e) {
            log.error("Failed to remove favorite: userId={}, promptTemplateUuid={}, error={}", userId,
                promptTemplateUuid, e.getMessage(), e);
            throw FavoriteException.internalError("Failed to remove favorite", e);
        }
    }

    /**
     * 사용자 ID와 프롬프트 템플릿 UUID의 유효성을 검증합니다.
     *
     * @param userId             사용자 ID (null 불가)
     * @param promptTemplateUuid 프롬프트 템플릿 UUID (null 불가)
     * @throws IllegalArgumentException 파라미터가 null일 경우
     */
    private void validateUserIdAndPromptTemplateUuid(Long userId, UUID promptTemplateUuid) {
        Assert.notNull(userId, "UserId must not be null");
        Assert.notNull(promptTemplateUuid, "PromptTemplateUuid must not be null");
    }

    /**
     * 프롬프트 UUID로 내부 ID를 조회합니다. 없으면 FavoriteException.notFound 예외를 발생시킵니다.
     *
     * @param promptTemplateUuid 프롬프트 템플릿 UUID
     * @return 프롬프트 내부 ID
     * @throws FavoriteException 프롬프트가 존재하지 않을 때 발생
     */
    private Long findPromptIdOrThrow(UUID promptTemplateUuid) {
        return loadPromptTemplateIdPort.findIdByUuid(promptTemplateUuid)
            .orElseThrow(() -> {
                log.error("Prompt not found for UUID: {}", promptTemplateUuid);
                throw FavoriteException.notFound(promptTemplateUuid);
            });
    }
}
