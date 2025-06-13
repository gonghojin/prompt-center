package com.gongdel.promptserver.adapter.in.rest.request.prompt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 프롬프트 수정 요청을 위한 DTO 클래스입니다.
 * 프롬프트 수정에 필요한 정보와 입력값 검증 규칙을 포함합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePromptRequest {

    /**
     * 프롬프트 제목 (필수, 200자 이내)
     */
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be less than 200 characters")
    private String title;

    /**
     * 프롬프트 설명 (선택, 1000자 이내)
     */
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    /**
     * 프롬프트 내용 (필수, 20000자 이내)
     */
    @NotBlank(message = "Content is required")
    @Size(max = 20000, message = "Content must be less than 20000 characters")
    private String content;

    /**
     * 카테고리 ID (필수)
     */
    @NotNull(message = "CategoryId is required")
    private Long categoryId;

    /**
     * 태그 문자열 목록 (선택)
     */
    @Builder.Default
    private Set<String> tags = new HashSet<>();

    /**
     * 입력 변수 목록 (선택)
     */
    private List<InputVariableDto> inputVariables;

    /**
     * 가시성 (필수)
     * 예시: "PUBLIC", "PRIVATE", "TEAM"
     */
    @NotBlank(message = "Visibility is required")
    private String visibility;

    /**
     * 상태 (필수)
     * 예시: "DRAFT", "PUBLISHED", "ARCHIVED", "DELETED"
     */
    @NotBlank(message = "Status is required")
    private String status;
}
