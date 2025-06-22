package com.gongdel.promptserver.adapter.out.persistence.repository;

import com.gongdel.promptserver.adapter.out.persistence.entity.QCategoryEntity;
import com.gongdel.promptserver.adapter.out.persistence.entity.QPromptTemplateEntity;
import com.gongdel.promptserver.domain.model.statistics.CategoryPromptCount;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

/**
 * QueryDSL 및 네이티브 쿼리 기반 카테고리별 프롬프트 개수 집계용 Repository 구현체입니다.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CategoryStatisticsQueryRepositoryImpl implements CategoryStatisticsQueryRepository {

    private static final String ROOT_CATEGORY_PROMPT_COUNT_SQL = """
        WITH RECURSIVE category_tree AS (
            SELECT id AS category_id, id AS root_id
            FROM categories
            WHERE parent_category_id IS NULL
            UNION ALL
            SELECT c.id AS category_id, ct.root_id
            FROM categories c
            JOIN category_tree ct ON c.parent_category_id = ct.category_id
        )
        SELECT ct.root_id, r.name AS root_name, COUNT(p.id) AS prompt_count
        FROM category_tree ct
        JOIN categories r ON ct.root_id = r.id
        LEFT JOIN prompt_templates p ON p.category_id = ct.category_id
        GROUP BY ct.root_id, r.name
        ORDER BY ct.root_id
        """;
    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    /**
     * 루트 카테고리별(모든 하위 포함) 프롬프트 개수 목록을 집계합니다.
     * <p>
     * 재귀 CTE 기반 네이티브 쿼리를 사용합니다.
     *
     * @return 루트 카테고리별 프롬프트 개수 목록
     */
    @Override
    public List<CategoryPromptCount> findRootCategoryPromptCounts() {
        List<Object[]> results = entityManager.createNativeQuery(ROOT_CATEGORY_PROMPT_COUNT_SQL).getResultList();
        return mapToCategoryPromptCount(results);
    }

    /**
     * 특정 루트의 하위 카테고리별 프롬프트 개수 목록을 집계합니다.
     *
     * @param rootId 루트 카테고리 ID
     * @return 하위 카테고리별 프롬프트 개수 목록
     */
    @Override
    public List<CategoryPromptCount> findChildCategoryPromptCounts(Long rootId) {
        Assert.notNull(rootId, "rootId must not be null");
        QCategoryEntity category = QCategoryEntity.categoryEntity;
        QPromptTemplateEntity prompt = QPromptTemplateEntity.promptTemplateEntity;

        return queryFactory
            .select(Projections.constructor(CategoryPromptCount.class,
                category.id,
                category.name,
                prompt.id.count()))
            .from(category)
            .leftJoin(prompt).on(prompt.category.id.eq(category.id))
            .where(category.parentCategory.id.eq(rootId))
            .groupBy(category.id, category.name)
            .fetch();
    }

    /**
     * 네이티브 쿼리 결과를 CategoryPromptCount 리스트로 변환합니다.
     *
     * @param results 네이티브 쿼리 결과
     * @return CategoryPromptCount 리스트
     */
    private List<CategoryPromptCount> mapToCategoryPromptCount(List<Object[]> results) {
        return results.stream()
            .map(row -> {
                Long rootId = ((Number) row[0]).longValue();
                String rootName = (String) row[1];
                Long promptCount = ((Number) row[2]).longValue();
                return new CategoryPromptCount(rootId, rootName, promptCount);
            })
            .toList();
    }
}
