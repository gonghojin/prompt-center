package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.entity.UserEntity;
import com.gongdel.promptserver.adapter.out.persistence.repository.FavoriteRepository;
import com.gongdel.promptserver.application.port.out.query.FindFavoritesPort;
import com.gongdel.promptserver.application.port.out.query.LoadFavoritesPort;
import com.gongdel.promptserver.domain.model.favorite.FavoritePromptResult;
import com.gongdel.promptserver.domain.model.favorite.FavoriteSearchCondition;
import com.gongdel.promptserver.domain.statistics.ComparisonPeriod;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 즐겨찾기 쿼리 어댑터 (CQRS Query Adapter)
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteQueryAdapter implements FindFavoritesPort, LoadFavoritesPort {

    private static final Logger log = LoggerFactory.getLogger(FavoriteQueryAdapter.class);
    private final FavoriteRepository favoriteRepository;

    @Override
    public long countByUser(Long userId) {
        Assert.notNull(userId, "userId must not be null");
        return favoriteRepository.countByUser(new UserEntity(userId));
    }

    @Override
    public long countTotal() {
        return favoriteRepository.count();
    }

    @Override
    public Page<FavoritePromptResult> findFavorites(FavoriteSearchCondition condition) {
        Assert.notNull(condition, "FavoriteSearchCondition must not be null");
        Assert.notNull(condition.getUserId(), "userId in condition must not be null");
        Assert.notNull(condition.getPageable(), "pageable in condition must not be null");
        // TODO: FavoriteEntity -> FavoritePromptResult 매핑 구현 필요
        throw new UnsupportedOperationException("findFavorites 매핑 구현 필요");
    }

    @Override
    public long loadFavoriteCountByPeriod(ComparisonPeriod period) {
        Assert.notNull(period, "period must not be null");
        log.debug("Loading favorite count for period: {} to {}", period.getStartDate(), period.getEndDate());

        long count = favoriteRepository.countByCreatedAtBetween(
            period.getStartDate(),
            period.getEndDate());

        log.debug("Favorite count for period: {}", count);
        return count;
    }
}
