package com.gongdel.promptserver.adapter.in.rest.controller.category;

import com.gongdel.promptserver.adapter.in.rest.request.category.CreateCategoryRequest;
import com.gongdel.promptserver.adapter.in.rest.request.category.UpdateCategoryRequest;
import com.gongdel.promptserver.adapter.in.rest.response.category.CategoryResponse;
import com.gongdel.promptserver.application.exception.CategoryDuplicateNameException;
import com.gongdel.promptserver.application.exception.CategoryNotFoundException;
import com.gongdel.promptserver.application.port.in.CategoryCommandUseCase;
import com.gongdel.promptserver.domain.model.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "카테고리 관리", description = "카테고리 생성, 수정, 삭제 API")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryCommandController {
    private final CategoryCommandUseCase categoryCommandUseCase;

    @Operation(summary = "카테고리 생성", description = "새로운 카테고리를 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "카테고리 생성 성공"),
        @ApiResponse(responseCode = "409", description = "카테고리 이름 중복"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
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

    @Operation(summary = "카테고리 수정", description = "기존 카테고리 정보를 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "카테고리 수정 성공"),
        @ApiResponse(responseCode = "404", description = "카테고리 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
        @Parameter(description = "카테고리 ID", example = "1") @PathVariable Long id,
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

    @Operation(summary = "카테고리 삭제", description = "카테고리를 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "카테고리 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "카테고리 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
        @Parameter(description = "카테고리 ID", example = "1") @PathVariable Long id) {
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
