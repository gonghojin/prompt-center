package com.gongdel.promptserver.adapter.in.rest;

import com.gongdel.promptserver.adapter.in.rest.response.CategoryResponse;
import com.gongdel.promptserver.application.exception.CategoryNotFoundException;
import com.gongdel.promptserver.application.port.in.CategoryQueryUseCase;
import com.gongdel.promptserver.domain.model.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "카테고리 조회", description = "카테고리 조회 API")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryQueryController {
    private final CategoryQueryUseCase categoryQueryUseCase;

    @Operation(summary = "카테고리 단건 조회", description = "ID로 카테고리를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리 조회 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @Parameter(description = "카테고리 ID", example = "1") @PathVariable Long id) {
        log.info("Retrieving category with id: {}", id);
        return categoryQueryUseCase.getCategoryById(id)
                .map(category -> ResponseEntity.ok(CategoryResponse.from(category)))
                .orElseGet(() -> {
                    log.warn("Category not found with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(summary = "카테고리 목록 조회", description = "조건에 따라 카테고리 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories(
            @Parameter(description = "카테고리 이름으로 검색", example = "AI") @RequestParam(required = false) String name,
            @Parameter(description = "시스템 카테고리 여부", example = "false") @RequestParam(required = false) Boolean isSystem) {
        if (name != null && !name.isEmpty()) {
            log.info("Retrieving category with name: {}", name);
            return categoryQueryUseCase.getCategoryByName(name)
                    .map(category -> ResponseEntity.ok(List.of(CategoryResponse.from(category))))
                    .orElseGet(() -> {
                        log.warn("Category not found with name: {}", name);
                        return ResponseEntity.ok(List.of());
                    });
        }
        if (isSystem != null) {
            log.info("Retrieving categories with system flag: {}", isSystem);
            List<Category> categories = categoryQueryUseCase.getCategoriesBySystemFlag(isSystem);
            List<CategoryResponse> response = categories.stream()
                    .map(CategoryResponse::from)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        }
        log.info("Retrieving all categories");
        List<Category> categories = categoryQueryUseCase.getAllCategories();
        List<CategoryResponse> response = categories.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "루트 카테고리 목록 조회", description = "최상위 카테고리 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "루트 카테고리 목록 조회 성공")
    })
    @GetMapping("/roots")
    public ResponseEntity<List<CategoryResponse>> getRootCategories() {
        log.info("Retrieving root categories");
        List<Category> categories = categoryQueryUseCase.getRootCategories();
        List<CategoryResponse> response = categories.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "하위 카테고리 목록 조회", description = "특정 카테고리의 하위 카테고리 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "하위 카테고리 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "상위 카테고리 없음")
    })
    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<List<CategoryResponse>> getSubCategories(
            @Parameter(description = "상위 카테고리 ID", example = "1") @PathVariable Long parentId) {
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
}
