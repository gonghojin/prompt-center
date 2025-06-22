package com.gongdel.promptserver.adapter.out.persistence.command;

import com.gongdel.promptserver.adapter.out.persistence.entity.PromptTemplateEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.UserEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.favorite.FavoriteEntity;
import com.gongdel.promptserver.adapter.out.persistence.repository.FavoriteRepository;
import com.gongdel.promptserver.adapter.out.persistence.repository.PromptTemplateJpaRepository;
import com.gongdel.promptserver.adapter.out.persistence.repository.UserJpaRepository;
import com.gongdel.promptserver.application.port.out.command.DeleteFavoritePort;
import com.gongdel.promptserver.application.port.out.command.SaveFavoritePort;
import com.gongdel.promptserver.domain.exception.FavoriteException;
import com.gongdel.promptserver.domain.exception.PromptErrorType;
import com.gongdel.promptserver.domain.model.favorite.Favorite;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 즐겨찾기(Favorite) 저장을 위한 커맨드 어댑터입니다.
 * DB 제약조건 위반 시 비즈니스 예외로 변환합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class FavoriteCommandAdapter implements SaveFavoritePort, DeleteFavoritePort {
    private static final String DUPLICATE_FAVORITE_MESSAGE = "이미 즐겨찾기한 프롬프트입니다.";
    private static final String UNKNOWN_ERROR_MESSAGE = "즐겨찾기 저장 중 알 수 없는 오류";

    private final FavoriteRepository favoriteRepository;
    private final UserJpaRepository userJpaRepository;
    private final PromptTemplateJpaRepository promptTemplateJpaRepository;

    /**
     * 즐겨찾기 도메인 객체를 저장합니다.
     *
     * @param favorite 즐겨찾기 도메인 객체
     * @return 저장된 즐겨찾기 도메인 객체
     * @throws FavoriteException 중복, 존재하지 않는 프롬프트/사용자 등
     */
    @Override
    public Favorite save(Favorite favorite) {
        try {
            // 이미 앞 단에서 존재 유무 판단했기 떄문에, 프록시로 대체
            UserEntity user = userJpaRepository.getReferenceById(favorite.getUserId());
            PromptTemplateEntity prompt = promptTemplateJpaRepository.getReferenceById(favorite.getPromptTemplateId());

            FavoriteEntity entity = FavoriteEntity.builder()
                .user(user)
                .promptTemplate(prompt)
                .build();
            FavoriteEntity saved = favoriteRepository.save(entity);
            return toDomain(saved);
        } catch (DataIntegrityViolationException e) {
            log.warn("Duplicate favorite detected. userId={}, promptTemplateId={}", favorite.getUserId(),
                favorite.getPromptTemplateId());
            throw new FavoriteException(PromptErrorType.DUPLICATE_TITLE, DUPLICATE_FAVORITE_MESSAGE, e);
        } catch (Exception e) {
            log.error("Failed to save favorite. userId={}, promptTemplateId={}", favorite.getUserId(),
                favorite.getPromptTemplateId(), e);
            throw FavoriteException.internalError(UNKNOWN_ERROR_MESSAGE, e);
        }
    }

    /**
     * FavoriteEntity -> 도메인 Favorite 변환
     */
    private Favorite toDomain(FavoriteEntity entity) {
        return Favorite.builder()
            .id(entity.getId())
            .userId(entity.getUser().getId())
            .promptTemplateId(entity.getPromptTemplate().getId())
            .createdAt(entity.getCreatedAt())
            .build();
    }

    /**
     * 사용자와 프롬프트 ID로 즐겨찾기를 삭제합니다.
     *
     * @param userId           사용자 ID
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 삭제된 개수
     * @throws FavoriteException 중복, 존재하지 않는 프롬프트/사용자 등
     */
    @Override
    public long deleteByUserIdAndPromptTemplateId(Long userId, Long promptTemplateId) {
        try {
            UserEntity user = userJpaRepository.getReferenceById(userId);
            PromptTemplateEntity prompt = promptTemplateJpaRepository.getReferenceById(promptTemplateId);
            long deleted = favoriteRepository.deleteByUserAndPromptTemplate(user, prompt);
            if (deleted == 0) {
                log.warn("No favorite found to delete. userId={}, promptTemplateId={}", userId, promptTemplateId);
            } else {
                log.info("Favorite deleted. userId={}, promptTemplateId={}", userId, promptTemplateId);
            }
            return deleted;
        } catch (Exception e) {
            log.error("Failed to delete favorite. userId={}, promptTemplateId={}", userId, promptTemplateId, e);
            throw FavoriteException.internalError("즐겨찾기 삭제 중 알 수 없는 오류", e);
        }
    }
}
