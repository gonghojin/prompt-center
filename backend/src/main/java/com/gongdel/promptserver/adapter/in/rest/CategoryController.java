package com.gongdel.promptserver.adapter.in.rest;

import com.gongdel.promptserver.adapter.in.rest.request.CreateCategoryRequest;
import com.gongdel.promptserver.adapter.in.rest.request.UpdateCategoryRequest;
import com.gongdel.promptserver.adapter.in.rest.response.CategoryResponse;
import com.gongdel.promptserver.application.exception.CategoryDuplicateNameException;
import com.gongdel.promptserver.application.exception.CategoryNotFoundException;
import com.gongdel.promptserver.application.port.in.CategoryCommandUseCase;
import com.gongdel.promptserver.application.port.in.CategoryQueryUseCase;
import com.gongdel.promptserver.domain.model.Category;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 카테고리 리소스에 대한 REST API를 제공하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryCommandUseCase categoryCommandUseCase;
    private final CategoryQueryUseCase categoryQueryUseCase;

    /**
     * 새로운 카테고리를 생성합니다.
     *
     * @param request 카테고리 생성 요청 정보
     * @return 생성된 카테고리 정보
     */
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        log.info("Creating new category with name: [{}]", request.getName());

        try {
            Category category = categoryCommandUseCase.createCategory(request.toCommand());
            log.info("Successfully created category with ID: [{}]", category.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(CategoryResponse.from(category));
        } catch (CategoryDuplicateNameException e) {
            log.warn("Duplicate category name: [{}]", request.getName(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * ID로 특정 카테고리를 조회합니다.
     *
     * @param id 카테고리 ID
     * @return 카테고리 정보 또는 404 응답
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        log.info("Retrieving category with id: {}", id);
        return categoryQueryUseCase.getCategoryById(id)
                .map(category -> ResponseEntity.ok(CategoryResponse.from(category)))
                .orElseGet(() -> {
                    log.warn("Category not found with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    // TODO : 추후 분리하기 - name and system 필터링 추가
    /**
     * 모든 카테고리 목록을 조회합니다.
     * 이름 또는 시스템 카테고리 여부로 필터링할 수 있습니다.
     *
     * @param name     필터링할 카테고리 이름 (선택)
     * @param isSystem 시스템 카테고리 여부 (선택)
     * @return 카테고리 목록
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isSystem) {

        // 이름으로 조회하는 경우
        if (name != null && !name.isEmpty()) {
            log.info("Retrieving category with name: {}", name);
            return categoryQueryUseCase.getCategoryByName(name)
                    .map(category -> ResponseEntity.ok(List.of(CategoryResponse.from(category))))
                    .orElseGet(() -> {
                        log.warn("Category not found with name: {}", name);
                        return ResponseEntity.ok(List.of());
                    });
        }

        // 시스템 카테고리 여부로 필터링하는 경우
        if (isSystem != null) {
            log.info("Retrieving categories with system flag: {}", isSystem);
            List<Category> categories = categoryQueryUseCase.getCategoriesBySystemFlag(isSystem);
            List<CategoryResponse> response = categories.stream()
                    .map(CategoryResponse::from)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        }

        // 모든 카테고리 조회
        log.info("Retrieving all categories");
        List<Category> categories = categoryQueryUseCase.getAllCategories();
        List<CategoryResponse> response = categories.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * 최상위 카테고리(상위 카테고리가 없는)를 조회합니다.
     *
     * @return 최상위 카테고리 목록
     */
    @GetMapping("/roots")
    public ResponseEntity<List<CategoryResponse>> getRootCategories() {
        log.info("Retrieving root categories");
        List<Category> categories = categoryQueryUseCase.getRootCategories();
        List<CategoryResponse> response = categories.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * 특정 카테고리의 하위 카테고리를 조회합니다.
     *
     * @param parentId 상위 카테고리 ID
     * @return 하위 카테고리 목록
     */
    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<List<CategoryResponse>> getSubCategories(@PathVariable Long parentId) {
        log.info("Retrieving subcategories of parent ID: {}", parentId);
        try {
            List<Category> categories = categoryQueryUseCase.getSubCategories(parentId);
            List<CategoryResponse> response = categories.stream()
                    .map(CategoryResponse::from)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (CategoryNotFoundException e) {
            log.warn("Parent category not found with id: {}", parentId, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 카테고리를 업데이트합니다.
     *
     * @param id      업데이트할 카테고리 ID
     * @param request 카테고리 업데이트 요청 정보
     * @return 업데이트된 카테고리 정보
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        log.info("Updating category with id: {}", id);
        try {
            Category category = categoryCommandUseCase.updateCategory(request.toCommand(id));
            log.info("Successfully updated category with ID: [{}]", category.getId());
            return ResponseEntity.ok(CategoryResponse.from(category));
        } catch (CategoryNotFoundException e) {
            log.warn("Category not found for update with id: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 카테고리를 삭제합니다.
     *
     * @param id 삭제할 카테고리 ID
     * @return 응답 상태
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.info("Deleting category with id: {}", id);
        try {
            categoryCommandUseCase.deleteCategory(id);
            log.info("Successfully deleted category with ID: [{}]", id);
            return ResponseEntity.noContent().build();
        } catch (CategoryNotFoundException e) {
            log.warn("Category not found for deletion with id: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }
}
