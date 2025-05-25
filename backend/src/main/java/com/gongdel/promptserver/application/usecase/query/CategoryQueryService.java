package com.gongdel.promptserver.application.usecase.query;

import com.gongdel.promptserver.application.exception.CategoryExceptionConverter;
import com.gongdel.promptserver.application.exception.CategoryNotFoundException;
import com.gongdel.promptserver.application.exception.CategoryOperationFailedException;
import com.gongdel.promptserver.application.port.in.CategoryQueryUseCase;
import com.gongdel.promptserver.application.port.out.query.FindCategoriesPort;
import com.gongdel.promptserver.application.port.out.query.LoadCategoryPort;
import com.gongdel.promptserver.domain.exception.CategoryDomainException;
import com.gongdel.promptserver.domain.exception.CategoryNotFoundDomainException;
import com.gongdel.promptserver.domain.model.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 조회 유즈케이스 구현체입니다.
 * 이 서비스는 카테고리 조회 작업을 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryQueryService implements CategoryQueryUseCase {

    private final LoadCategoryPort loadCategoryPort;
    private final FindCategoriesPort findCategoriesPort;

    /**
     * 카테고리 ID로 카테고리를 조회합니다.
     *
     * @param id 카테고리 ID
     * @return 카테고리 Optional
     * @throws CategoryNotFoundException        카테고리를 찾을 수 없는 경우
     * @throws CategoryOperationFailedException 조회 중 오류가 발생한 경우
     */
    @Override
    public Optional<Category> getCategoryById(Long id) {
        Assert.notNull(id, "Category ID must not be null");
        log.debug("Querying category by ID: {}", id);

        try {
            Optional<Category> category = loadCategoryPort.loadCategoryById(id);

            if (category.isPresent()) {
                log.info("Category found: id={}, name={}", id, category.get().getName());
            } else {
                log.info("Category not found: id={}", id);
            }

            return category;
        } catch (CategoryDomainException e) {
            // 도메인 예외를 애플리케이션 예외로 변환
            throw CategoryExceptionConverter.convertToApplicationException(e, id);
        } catch (Exception e) {
            log.error("Unexpected error while querying category by ID: {}", id, e);
            throw new CategoryOperationFailedException(
                "Error occurred during category operation: " + e.getMessage(),
                e);
        }
    }

    /**
     * 카테고리 이름으로 카테고리를 조회합니다.
     *
     * @param name 카테고리 이름
     * @return 카테고리 Optional
     * @throws CategoryNotFoundException        카테고리를 찾을 수 없는 경우
     * @throws CategoryOperationFailedException 조회 중 오류가 발생한 경우
     */
    @Override
    public Optional<Category> getCategoryByName(String name) {
        Assert.hasText(name, "Category name must not be empty");
        log.debug("Querying category by name: {}", name);

        try {
            Optional<Category> category = loadCategoryPort.loadCategoryByName(name);

            if (category.isPresent()) {
                log.info("Category found: name={}, id={}", name, category.get().getId());
            } else {
                log.info("Category not found: name={}", name);
            }

            return category;
        } catch (CategoryDomainException e) {
            // 도메인 예외를 애플리케이션 예외로 변환
            throw CategoryExceptionConverter.convertToApplicationException(e, name);
        } catch (Exception e) {
            log.error("Unexpected error while querying category by name: {}", name, e);
            throw new CategoryOperationFailedException(
                "Error occurred during category operation: " + e.getMessage(),
                e);
        }
    }

    /**
     * 모든 카테고리를 조회합니다.
     *
     * @return 카테고리 목록
     * @throws CategoryOperationFailedException 조회 중 오류가 발생한 경우
     */
    @Override
    public List<Category> getAllCategories() {
        log.debug("Querying all categories");

        try {
            List<Category> categories = findCategoriesPort.findAllCategories();
            log.info("Retrieved all categories: total count={}", categories.size());
            return categories;
        } catch (CategoryDomainException e) {
            // 도메인 예외를 애플리케이션 예외로 변환
            throw CategoryExceptionConverter.convertToApplicationException(e, "all categories");
        } catch (Exception e) {
            log.error("Unexpected error while querying all categories", e);
            throw new CategoryOperationFailedException(
                "Error occurred during category operation: " + e.getMessage(),
                e);
        }
    }

    /**
     * 시스템 카테고리 여부로 카테고리 목록을 조회합니다.
     *
     * @param isSystem 시스템 카테고리 여부
     * @return 카테고리 목록
     * @throws CategoryOperationFailedException 조회 중 오류가 발생한 경우
     */
    @Override
    public List<Category> getCategoriesBySystemFlag(boolean isSystem) {
        log.debug("Querying categories by system flag: isSystem={}", isSystem);

        try {
            List<Category> categories = findCategoriesPort.findCategoriesByIsSystem(isSystem);
            log.info("Retrieved categories by system flag: isSystem={}, total count={}", isSystem, categories.size());
            return categories;
        } catch (CategoryDomainException e) {
            // 도메인 예외를 애플리케이션 예외로 변환
            throw CategoryExceptionConverter.convertToApplicationException(e, "isSystem=" + isSystem);
        } catch (Exception e) {
            log.error("Unexpected error while querying categories by system flag: {}", isSystem, e);
            throw new CategoryOperationFailedException(
                "Error occurred during category operation: " + e.getMessage(),
                e);
        }
    }

    /**
     * 최상위 카테고리(상위 카테고리가 없는)를 조회합니다.
     *
     * @return 최상위 카테고리 목록
     * @throws CategoryOperationFailedException 조회 중 오류가 발생한 경우
     */
    @Override
    public List<Category> getRootCategories() {
        log.debug("Querying root categories");

        try {
            List<Category> rootCategories = findCategoriesPort.findRootCategories();
            log.info("Retrieved root categories: total count={}", rootCategories.size());
            return rootCategories;
        } catch (CategoryDomainException e) {
            // 도메인 예외를 애플리케이션 예외로 변환
            throw CategoryExceptionConverter.convertToApplicationException(e, "root categories");
        } catch (Exception e) {
            log.error("Unexpected error while querying root categories", e);
            throw new CategoryOperationFailedException(
                "Error occurred during category operation: " + e.getMessage(),
                e);
        }
    }

    /**
     * 특정 카테고리의 하위 카테고리를 조회합니다.
     *
     * @param parentId 상위 카테고리 ID
     * @return 하위 카테고리 목록
     * @throws CategoryNotFoundException        상위 카테고리를 찾을 수 없는 경우
     * @throws CategoryOperationFailedException 조회 중 오류가 발생한 경우
     */
    @Override
    public List<Category> getSubCategories(Long parentId) {
        Assert.notNull(parentId, "Parent category ID must not be null");
        log.debug("Querying subcategories by parent ID: {}", parentId);

        try {
            // 상위 카테고리가 존재하는지 확인
            if (!loadCategoryPort.loadCategoryById(parentId).isPresent()) {
                log.warn("Parent category not found. ID: {}", parentId);
                throw new CategoryNotFoundDomainException(parentId);
            }

            List<Category> subCategories = findCategoriesPort.findCategoriesByParentId(parentId);
            log.info("Retrieved subcategories: parentId={}, total count={}", parentId, subCategories.size());
            return subCategories;
        } catch (CategoryDomainException e) {
            // 도메인 예외를 애플리케이션 예외로 변환
            throw CategoryExceptionConverter.convertToApplicationException(e, parentId);
        } catch (Exception e) {
            log.error("Unexpected error while querying subcategories for parent ID: {}", parentId, e);
            throw new CategoryOperationFailedException(
                "Error occurred during category operation: " + e.getMessage(),
                e);
        }
    }
}
