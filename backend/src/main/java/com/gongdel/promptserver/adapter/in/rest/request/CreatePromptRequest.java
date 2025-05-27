package com.gongdel.promptserver.adapter.in.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 새로운 프롬프트 생성 요청을 위한 DTO 클래스입니다.
 * 프롬프트 생성에 필요한 정보와 입력값 검증 규칙을 포함합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePromptRequest {

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
     * 작성자 정보 (User DTO)
     */
    private UserDto createdBy;

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
     * 카테고리 ID
     */
    private Long categoryId;

    /**
     * 가시성 (PUBLIC, PRIVATE, TEAM 등, 선택)
     * 예시: "PUBLIC", "PRIVATE", "TEAM"
     * 실제 enum: com.gongdel.promptserver.domain.model.Visibility
     */
    private String visibility;

    /**
     * 상태 (DRAFT, PUBLISHED, ARCHIVED, DELETED 등, 선택)
     * 예시: "ARCHIVED", "DRAFT"
     * 실제 enum: com.gongdel.promptserver.domain.model.PromptStatus
     */
    private String status;

    /**
     * 작성자 정보 전달용 내부 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserDto {
        private UUID id;
        private String email;
        private String name;
    }
}
