package com.gongdel.promptserver.adapter.out.persistence.command;

import com.gongdel.promptserver.adapter.out.persistence.entity.CategoryEntity;
import com.gongdel.promptserver.adapter.out.persistence.repository.JpaCategoryRepository;
import com.gongdel.promptserver.application.port.out.command.DeleteCategoryPort;
import com.gongdel.promptserver.application.port.out.command.SaveCategoryPort;
import com.gongdel.promptserver.application.port.out.command.UpdateCategoryPort;
import com.gongdel.promptserver.domain.exception.CategoryErrorType;
import com.gongdel.promptserver.domain.exception.CategoryNotFoundDomainException;
import com.gongdel.promptserver.domain.exception.CategoryOperationException;
import com.gongdel.promptserver.domain.model.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * 카테고리 명령 포트 구현체입니다. JpaCategoryRepository를 사용하여 카테고리 저장, 업데이트 및 삭제 작업을 수행합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryCommandAdapter implements SaveCategoryPort, UpdateCategoryPort, DeleteCategoryPort {

    private final JpaCategoryRepository jpaCategoryRepository;

    /**
     * 새 카테고리를 생성합니다.
     *
     * @param category 저장할 카테고리 객체
     * @return 저장된 카테고리 객체
     * @throws CategoryOperationException 카테고리 저장 중 오류 발생 시 (동일한 이름의 카테고리가 있을 경우 포함)
     */
    @Override
    @Transactional
    public Category saveCategory(Category category) {
        validateCategoryForCreation(category);
        checkCategoryNameDuplicate(category.getName());

        log.debug("Creating category: {}", category.getName());

        try {
            CategoryEntity entity = CategoryEntity.fromDomain(category);
            CategoryEntity savedEntity = jpaCategoryRepository.save(entity);
            Category savedCategory = savedEntity.toDomain();

            log.info("Category created with id: {}, name: {}",
                savedCategory.getId(),
                savedCategory.getName());

            return savedCategory;
        } catch (DataIntegrityViolationException e) {
            String errorMsg = "Failed to create category due to data integrity violation: " + category.getName();
            log.error(errorMsg, e);
            throw new CategoryOperationException(CategoryErrorType.INVALID_CATEGORY, errorMsg, e);
        } catch (Exception e) {
            log.error("Failed to create category: {}. Error: {}", category.getName(), e.getMessage(), e);
            throw createCategoryOperationException("Failed to create category: " + category.getName(), e);
        }
    }

    /**
     * 기존 카테고리를 업데이트합니다.
     *
     * @param category 업데이트할 카테고리 객체
     * @return 업데이트된 카테고리 객체
     * @throws CategoryNotFoundDomainException 카테고리를 찾을 수 없는 경우
     * @throws CategoryOperationException      카테고리 업데이트 중 오류 발생 시
     */
    @Override
    @Transactional
    public Category updateCategory(Category category) {
        validateCategoryForUpdate(category);
        checkCategoryNameDuplicateForUpdate(category);

        log.debug("Updating category: {} with id: {}", category.getName(), category.getId());

        // 카테고리가 존재하는지 확인
        if (!jpaCategoryRepository.existsById(category.getId())) {
            throw new CategoryNotFoundDomainException(category.getId());
        }

        try {
            CategoryEntity entity = CategoryEntity.fromDomain(category);
            CategoryEntity updatedEntity = jpaCategoryRepository.save(entity);
            Category updatedCategory = updatedEntity.toDomain();

            log.info("Category updated with id: {}, name: {}",
                updatedCategory.getId(),
                updatedCategory.getName());

            return updatedCategory;
        } catch (DataIntegrityViolationException e) {
            String errorMsg = "Failed to update category due to data integrity violation: " + category.getName();
            log.error(errorMsg, e);
            throw new CategoryOperationException(CategoryErrorType.INVALID_CATEGORY, errorMsg, e);
        } catch (Exception e) {
            log.error("Failed to update category with id: {}, name: {}. Error: {}",
                category.getId(), category.getName(), e.getMessage(), e);
            throw createCategoryOperationException("Failed to update category with id: " + category.getId(), e);
        }
    }

    /**
     * 카테고리를 삭제합니다.
     *
     * @param id 삭제할 카테고리 ID
     * @throws CategoryNotFoundDomainException 카테고리를 찾을 수 없는 경우
     * @throws CategoryOperationException      카테고리 삭제 중 오류 발생 시
     */
    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Assert.notNull(id, "Category id must not be null");

        log.debug("Deleting category with id: {}", id);
        try {
            jpaCategoryRepository.deleteById(id);
            log.info("Category with id: {} successfully deleted", id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Category with id: {} not found for deletion", id, e);
            throw new CategoryNotFoundDomainException(id);
        } catch (Exception e) {
            log.error("Failed to delete category with id: {}. Error: {}", id, e.getMessage(), e);
            throw createCategoryOperationException("Failed to delete category with id: " + id, e);
        }
    }

    /**
     * 생성을 위한 카테고리 유효성 검사
     *
     * @param category 검사할 카테고리 객체
     */
    private void validateCategoryForCreation(Category category) {
        Assert.notNull(category, "Category must not be null");
        Assert.notNull(category.getName(), "Category name must not be null");
        Assert.isTrue(category.getName().trim().length() > 0, "Category name must not be empty");
        Assert.isNull(category.getId(), "Category id must be null for creation");
    }

    /**
     * 업데이트를 위한 카테고리 유효성 검사
     *
     * @param category 검사할 카테고리 객체
     */
    private void validateCategoryForUpdate(Category category) {
        Assert.notNull(category, "Category must not be null");
        Assert.notNull(category.getId(), "Category id must not be null for update");
        Assert.notNull(category.getName(), "Category name must not be null");
        Assert.isTrue(category.getName().trim().length() > 0, "Category name must not be empty");
    }

    /**
     * 카테고리 이름 중복 검사 (생성 시)
     *
     * @param name 검사할 카테고리 이름
     * @throws CategoryOperationException 동일한 이름의 카테고리가 이미 존재하는 경우
     */
    private void checkCategoryNameDuplicate(String name) {
        if (jpaCategoryRepository.findByName(name).isPresent()) {
            log.warn("Attempted to create category with duplicate name: {}", name);
            throw new CategoryOperationException(
                CategoryErrorType.DUPLICATE_NAME,
                "Category with name '" + name + "' already exists");
        }
    }

    /**
     * 카테고리 이름 중복 검사 (업데이트 시)
     *
     * @param category 업데이트할 카테고리
     * @throws CategoryOperationException 다른 ID를 가진 동일한 이름의 카테고리가 이미 존재하는 경우
     */
    private void checkCategoryNameDuplicateForUpdate(Category category) {
        Optional<CategoryEntity> existingCategory = jpaCategoryRepository.findByName(category.getName());
        if (existingCategory.isPresent() && !existingCategory.get().getId().equals(category.getId())) {
            log.warn("Attempted to update category to duplicate name: {}", category.getName());
            throw new CategoryOperationException(
                CategoryErrorType.DUPLICATE_NAME,
                "Another category with name '" + category.getName() + "' already exists");
        }
    }

    /**
     * 카테고리 작업 예외 생성
     *
     * @param message 예외 메시지
     * @param cause   원인 예외
     * @return 생성된 CategoryOperationException
     */
    private CategoryOperationException createCategoryOperationException(String message, Throwable cause) {
        return new CategoryOperationException(
            CategoryErrorType.OPERATION_FAILED,
            message,
            cause);
    }
}
