package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.entity.CategoryEntity;
import com.gongdel.promptserver.adapter.out.persistence.repository.JpaCategoryRepository;
import com.gongdel.promptserver.application.port.out.query.FindCategoriesPort;
import com.gongdel.promptserver.application.port.out.query.LoadCategoryPort;
import com.gongdel.promptserver.domain.exception.CategoryErrorType;
import com.gongdel.promptserver.domain.exception.CategoryOperationException;
import com.gongdel.promptserver.domain.model.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 카테고리 조회 포트 구현체입니다. JpaCategoryRepository를 사용하여 카테고리 조회 작업을 수행합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용 트랜잭션으로 성능 최적화
public class CategoryQueryAdapter implements LoadCategoryPort, FindCategoriesPort {

    private final JpaCategoryRepository jpaCategoryRepository;

    @Override
    public Optional<Category> loadCategoryById(Long id) {
        Assert.notNull(id, "Category id must not be null");

        try {
            log.debug("Loading category by id: {}", id);
            Optional<CategoryEntity> entityOpt = jpaCategoryRepository.findById(id);

            if (entityOpt.isEmpty()) {
                log.debug("Category not found with id: {}", id);
                return Optional.empty();
            }

            log.debug("Category found with id: {}", id);
            return entityOpt.map(CategoryEntity::toDomain);
        } catch (Exception e) {
            log.error("Failed to load category with id: {}. Error: {}", id, e.getMessage(), e);
            throw new CategoryOperationException(
                CategoryErrorType.OPERATION_FAILED,
                "Failed to load category with id: " + id,
                e);
        }
    }

    @Override
    public Optional<Category> loadCategoryByName(String name) {
        Assert.notNull(name, "Category name must not be null");

        try {
            log.debug("Loading category by name: {}", name);
            Optional<CategoryEntity> entityOpt = jpaCategoryRepository.findByName(name);

            if (entityOpt.isEmpty()) {
                log.debug("Category not found with name: {}", name);
                return Optional.empty();
            }

            log.debug("Category found with name: {}", name);
            return entityOpt.map(CategoryEntity::toDomain);
        } catch (Exception e) {
            log.error("Failed to load category with name: {}. Error: {}", name, e.getMessage(), e);
            throw new CategoryOperationException(
                CategoryErrorType.OPERATION_FAILED,
                "Failed to load category with name: " + name,
                e);
        }
    }

    @Override
    public List<Category> findAllCategories() {
        try {
            log.debug("Finding all categories");
            List<CategoryEntity> entities = jpaCategoryRepository.findAll();
            log.debug("Found {} categories", entities.size());
            return entities.stream()
                .map(CategoryEntity::toDomain)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to find all categories. Error: {}", e.getMessage(), e);
            throw new CategoryOperationException(
                CategoryErrorType.OPERATION_FAILED,
                "Failed to find all categories",
                e);
        }
    }

    @Override
    public List<Category> findCategoriesByIsSystem(boolean isSystem) {
        try {
            log.debug("Finding categories with isSystem: {}", isSystem);
            List<CategoryEntity> entities = jpaCategoryRepository.findByIsSystem(isSystem);
            log.debug("Found {} categories with isSystem: {}", entities.size(), isSystem);
            return entities.stream()
                .map(CategoryEntity::toDomain)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to find categories with isSystem: {}. Error: {}", isSystem, e.getMessage(), e);
            throw new CategoryOperationException(
                CategoryErrorType.OPERATION_FAILED,
                "Failed to find categories with isSystem: " + isSystem,
                e);
        }
    }

    @Override
    public List<Category> findRootCategories() {
        try {
            log.debug("Finding root categories");
            List<CategoryEntity> entities = jpaCategoryRepository.findByParentCategoryIsNull();
            log.debug("Found {} root categories", entities.size());
            return entities.stream()
                .map(CategoryEntity::toDomain)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to find root categories. Error: {}", e.getMessage(), e);
            throw new CategoryOperationException(
                CategoryErrorType.OPERATION_FAILED,
                "Failed to find root categories",
                e);
        }
    }

    @Override
    public List<Category> findCategoriesByParentId(Long parentId) {
        Assert.notNull(parentId, "Parent category id must not be null");

        try {
            log.debug("Finding categories by parent id: {}", parentId);
            List<CategoryEntity> entities = jpaCategoryRepository.findByParentCategoryId(parentId);
            log.debug("Found {} categories with parent id: {}", entities.size(), parentId);
            return entities.stream()
                .map(CategoryEntity::toDomain)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to find categories with parent id: {}. Error: {}", parentId, e.getMessage(), e);
            throw new CategoryOperationException(
                CategoryErrorType.OPERATION_FAILED,
                "Failed to find categories with parent id: " + parentId,
                e);
        }
    }
}
