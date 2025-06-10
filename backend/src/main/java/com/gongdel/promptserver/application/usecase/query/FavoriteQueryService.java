package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.port.in.query.FavoriteQueryUseCase;
import com.gongdel.promptserver.application.port.out.query.LoadFavoritesPort;
import com.gongdel.promptserver.application.port.out.query.SearchFavoritePort;
import com.gongdel.promptserver.domain.exception.FavoriteException;
import com.gongdel.promptserver.domain.model.favorite.FavoritePromptResult;
import com.gongdel.promptserver.domain.model.favorite.FavoriteSearchCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 즐겨찾기 관련 Query 유스케이스 구현체입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteQueryService implements FavoriteQueryUseCase {

    private final LoadFavoritesPort loadFavoritesPort;
    private final SearchFavoritePort searchFavoritePort;

    /**
     * 특정 사용자의 즐겨찾기 개수를 조회합니다.
     *
     * @param userId 사용자 ID (null 불가)
     * @return 즐겨찾기 개수
     * @throws IllegalArgumentException userId가 null인 경우
     * @throws FavoriteException        내부 오류 발생 시
     */
    @Override
    public long countByUser(Long userId) {
        try {
            validateUserId(userId);
            log.debug("Counting favorites for userId={}", userId);
            long count = loadFavoritesPort.countByUser(userId);
            log.info("Favorite count for userId={}: {}", userId, count);
            return count;
        } catch (IllegalArgumentException e) {
            log.warn("Invalid userId when counting favorites: {}", userId);
            throw e;
        } catch (Exception e) {
            log.error("Failed to count favorites for userId={}: {}", userId, e.getMessage(), e);
            throw FavoriteException.internalError("Failed to count favorites", e);
        }
    }

    /**
     * 즐겨찾기 목록을 조회합니다.
     *
     * @param condition 즐겨찾기 검색 조건 (userId, pageable 모두 null 불가)
     * @return 즐겨찾기 프롬프트 결과 페이지
     * @throws IllegalArgumentException condition, userId, pageable이 null인 경우
     * @throws FavoriteException        내부 오류 발생 시
     */
    @Override
    public Page<FavoritePromptResult> searchFavorites(FavoriteSearchCondition condition) {
        try {
            validateSearchCondition(condition);
            log.debug("Finding favorites for userId={}, condition={}", condition.getUserId(), condition);
            return searchFavoritePort.searchFavorites(condition);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid FavoriteSearchCondition: {}", condition);
            throw e;
        } catch (Exception e) {
            log.error("Failed to search favorites for userId={}: {}", condition != null ? condition.getUserId() : null,
                e.getMessage(), e);
            throw FavoriteException.internalError("Failed to search favorites", e);
        }
    }

    /**
     * 사용자 ID의 유효성을 검증합니다.
     *
     * @param userId 사용자 ID (null 불가)
     * @throws IllegalArgumentException userId가 null인 경우
     */
    private void validateUserId(Long userId) {
        Assert.notNull(userId, "userId must not be null");
    }

    /**
     * 즐겨찾기 검색 조건의 유효성을 검증합니다.
     *
     * @param condition 즐겨찾기 검색 조건 (userId, pageable 모두 null 불가)
     * @throws IllegalArgumentException condition, userId, pageable이 null인 경우
     */
    private void validateSearchCondition(FavoriteSearchCondition condition) {
        Assert.notNull(condition, "FavoriteSearchCondition must not be null");
    }
}
