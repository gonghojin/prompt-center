package com.gongdel.promptserver.application.usecase;

import com.gongdel.promptserver.application.exception.CategoryDuplicateNameException;
import com.gongdel.promptserver.application.exception.CategoryExceptionConverter;
import com.gongdel.promptserver.application.exception.CategoryNotFoundException;
import com.gongdel.promptserver.application.exception.CategoryOperationFailedException;
import com.gongdel.promptserver.application.port.in.CategoryCommandUseCase;
import com.gongdel.promptserver.application.port.in.command.CreateCategoryCommand;
import com.gongdel.promptserver.application.port.in.command.UpdateCategoryCommand;
import com.gongdel.promptserver.application.port.out.command.DeleteCategoryPort;
import com.gongdel.promptserver.application.port.out.command.SaveCategoryPort;
import com.gongdel.promptserver.application.port.out.command.UpdateCategoryPort;
import com.gongdel.promptserver.application.port.out.query.LoadCategoryPort;
import com.gongdel.promptserver.domain.exception.CategoryDomainException;
import com.gongdel.promptserver.domain.exception.CategoryDuplicateNameDomainException;
import com.gongdel.promptserver.domain.exception.CategoryErrorType;
import com.gongdel.promptserver.domain.exception.CategoryNotFoundDomainException;
import com.gongdel.promptserver.domain.exception.CategoryOperationException;
import com.gongdel.promptserver.domain.model.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 카테고리 명령 유즈케이스 구현체입니다. 이 서비스는 카테고리 생성, 수정, 삭제 작업을 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryCommandService implements CategoryCommandUseCase {

    private final SaveCategoryPort saveCategoryPort;
    private final UpdateCategoryPort updateCategoryPort;
    private final DeleteCategoryPort deleteCategoryPort;
    private final LoadCategoryPort loadCategoryPort;

    /**
     * 새 카테고리를 생성합니다.
     *
     * @param command 카테고리 생성 명령 객체
     * @return 생성된 카테고리
     * @throws CategoryDuplicateNameException   동일한 이름의 카테고리가 이미 존재하는 경우
     * @throws CategoryNotFoundException        지정된 상위 카테고리를 찾을 수 없는 경우
     * @throws CategoryOperationFailedException 카테고리 저장 중 오류가 발생한 경우
     */
    @Override
    public Category createCategory(CreateCategoryCommand command) {
        validateCreateCommand(command);
        log.debug("Starting category creation: {}", command.getName());

        try {
            // 이름 중복 검사
            checkDuplicateCategoryName(command.getName());

            // 도메인 객체 생성
            final Category initialCategory = command.toDomain();

            // 카테고리 생성 및 저장 로직
            Category categoryToSave = initialCategory;

            // 상위 카테고리가 지정된 경우 연결
            if (command.getParentCategoryId() != null) {
                Category parentCategory = findCategoryById(command.getParentCategoryId());
                // 새 카테고리와 부모 카테고리 연결을 위한 새로운 객체 생성
                categoryToSave = new Category(
                        initialCategory.getName(),
                        initialCategory.getDisplayName(),
                        initialCategory.getDescription(),
                        parentCategory);
            }

            // 카테고리 저장
            Category savedCategory = saveCategoryPort.saveCategory(categoryToSave);
            log.info("Category created successfully. ID: {}, Name: {}",
                    savedCategory.getId(), savedCategory.getName());
            return savedCategory;
        } catch (CategoryDomainException e) {
            // 도메인 예외를 애플리케이션 예외로 변환
            throw CategoryExceptionConverter.convertToApplicationException(e, command.getName());
        } catch (Exception e) {
            log.error("Failed to create category: {}", command.getName(), e);
            throw new CategoryOperationFailedException(
                    "Error occurred during category operation: " + e.getMessage(),
                    e);
        }
    }

    /**
     * 기존 카테고리를 업데이트합니다.
     *
     * @param command 카테고리 업데이트 명령 객체
     * @return 업데이트된 카테고리
     * @throws CategoryNotFoundException        카테고리 또는 상위 카테고리를 찾을 수 없는 경우
     * @throws CategoryOperationFailedException 카테고리 업데이트 중 오류가 발생한 경우
     */
    @Override
    public Category updateCategory(UpdateCategoryCommand command) {
        validateUpdateCommand(command);
        log.debug("Starting category update. ID: {}", command.getId());

        try {
            // 기존 카테고리 조회
            Category existingCategory = findCategoryById(command.getId());

            // 상위 카테고리 조회 (지정된 경우)
            Category parentCategory = null;
            if (command.getParentCategoryId() != null) {
                parentCategory = findCategoryById(command.getParentCategoryId());

                // 순환 참조 방지
                validateNonCircularReference(command.getId(), command.getParentCategoryId());
            }

            // 카테고리 정보 업데이트
            existingCategory.update(command.getDisplayName(), command.getDescription(), parentCategory);

            // 업데이트된 카테고리 저장
            Category updatedCategory = updateCategoryPort.updateCategory(existingCategory);
            log.info("Category updated successfully. ID: {}, Name: {}",
                    updatedCategory.getId(), updatedCategory.getName());

            return updatedCategory;
        } catch (CategoryDomainException e) {
            // 도메인 예외를 애플리케이션 예외로 변환
            throw CategoryExceptionConverter.convertToApplicationException(e, command.getId());
        } catch (Exception e) {
            // 그 외 예외는 CategoryOperationFailedException으로 변환
            log.error("Failed to update category. ID: {}", command.getId(), e);
            throw new CategoryOperationFailedException(
                    "Error occurred during category operation: " + e.getMessage(),
                    e);
        }
    }

    /**
     * 카테고리를 삭제합니다.
     *
     * @param id 삭제할 카테고리 ID
     * @throws CategoryNotFoundException        카테고리를 찾을 수 없는 경우
     * @throws CategoryOperationFailedException 카테고리 삭제 중 오류가 발생한 경우
     */
    @Override
    public void deleteCategory(Long id) {
        Assert.notNull(id, "Category ID must not be null");
        log.debug("Starting category deletion. ID: {}", id);

        try {
            // 카테고리 존재 여부 확인
            findCategoryById(id);

            // 카테고리 삭제
            deleteCategoryPort.deleteCategory(id);
            log.info("Category deleted successfully. ID: {}", id);
        } catch (CategoryDomainException e) {
            // 도메인 예외를 애플리케이션 예외로 변환
            throw CategoryExceptionConverter.convertToApplicationException(e, id);
        } catch (Exception e) {
            // 그 외 예외는 CategoryOperationFailedException으로 변환
            log.error("Failed to delete category. ID: {}", id, e);
            throw new CategoryOperationFailedException(
                    "Error occurred during category operation: " + e.getMessage(),
                    e);
        }
    }

    // 카테고리 생성 명령 검증
    private void validateCreateCommand(CreateCategoryCommand command) {
        Assert.notNull(command, "Create category command must not be null");
        Assert.hasText(command.getName(), "Category name must not be empty");
        Assert.hasText(command.getDisplayName(), "Category display name must not be empty");
    }

    // 카테고리 업데이트 명령 검증
    private void validateUpdateCommand(UpdateCategoryCommand command) {
        Assert.notNull(command, "Update category command must not be null");
        Assert.notNull(command.getId(), "Category ID must not be null");
        Assert.hasText(command.getDisplayName(), "Category display name must not be empty");
    }

    // 카테고리 이름 중복 검사
    private void checkDuplicateCategoryName(String name) {
        if (loadCategoryPort.loadCategoryByName(name).isPresent()) {
            log.warn("Duplicate category name detected: {}", name);
            throw new CategoryDuplicateNameDomainException(name);
        }
    }

    // 카테고리 ID로 카테고리 조회
    private Category findCategoryById(Long id) {
        return loadCategoryPort.loadCategoryById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found. ID: {}", id);
                    return new CategoryNotFoundDomainException(id);
                });
    }

    // 순환 참조 검증
    private void validateNonCircularReference(Long categoryId, Long parentCategoryId) {
        if (categoryId.equals(parentCategoryId)) {
            throw new CategoryOperationException(
                    CategoryErrorType.CIRCULAR_REFERENCE,
                    "A category cannot have itself as parent category");
        }
    }
}
