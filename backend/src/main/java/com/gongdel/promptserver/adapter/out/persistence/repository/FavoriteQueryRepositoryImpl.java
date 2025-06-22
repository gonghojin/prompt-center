package com.gongdel.promptserver.adapter.out.persistence.repository;

import com.gongdel.promptserver.adapter.out.persistence.entity.QPromptTemplateEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.QPromptTemplateTagEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.QTagEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.favorite.FavoriteEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.favorite.QFavoriteEntity;
import com.gongdel.promptserver.domain.model.favorite.FavoriteSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * QueryDSL 기반 즐겨찾기 검색 전용 레포지토리 구현체입니다.
 * 동적 조건, 페이징, 정렬, 키워드 등 복합 검색을 지원합니다.
 */
@Repository
@RequiredArgsConstructor
public class FavoriteQueryRepositoryImpl implements FavoriteQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<FavoriteEntity> searchFavorites(FavoriteSearchCondition condition) {
        QFavoriteEntity favorite = QFavoriteEntity.favoriteEntity;
        QPromptTemplateEntity prompt = QPromptTemplateEntity.promptTemplateEntity;
        QPromptTemplateTagEntity tagRel = QPromptTemplateTagEntity.promptTemplateTagEntity;
        QTagEntity tag = QTagEntity.tagEntity;

        Pageable pageable = condition.getPageable();

        // 기본 조인 및 where
        JPAQuery<FavoriteEntity> query = queryFactory.selectFrom(favorite)
            .join(favorite.promptTemplate, prompt).fetchJoin()
            .leftJoin(prompt.tagRelations, tagRel)
            .leftJoin(tagRel.tag, tag)
            .where(
                favorite.user.id.eq(condition.getUserId()),
                buildSearchKeywordPredicate(condition.getSearchKeyword(), prompt, tag))
            .distinct();

        // 정렬 처리
        if (condition.getSortType() != null) {
            switch (condition.getSortType()) {
                case "title" -> query.orderBy(prompt.title.asc());
                case "createdAt" -> query.orderBy(favorite.createdAt.desc());
                default -> query.orderBy(favorite.createdAt.desc());
            }
        } else {
            query.orderBy(favorite.createdAt.desc());
        }

        // 페이징
        List<FavoriteEntity> content = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // count 쿼리
        long total = queryFactory.selectFrom(favorite)
            .join(favorite.promptTemplate, prompt)
            .leftJoin(prompt.tagRelations, tagRel)
            .leftJoin(tagRel.tag, tag)
            .where(
                favorite.user.id.eq(condition.getUserId()),
                buildSearchKeywordPredicate(condition.getSearchKeyword(), prompt, tag))
            .distinct()
            .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression buildSearchKeywordPredicate(String keyword, QPromptTemplateEntity prompt,
                                                          QTagEntity tag) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return prompt.title.containsIgnoreCase(keyword)
            .or(prompt.description.containsIgnoreCase(keyword))
            .or(tag.name.containsIgnoreCase(keyword));
    }
}
