package com.gongdel.promptserver.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gongdel.promptserver.domain.model.PromptTemplate;
import com.gongdel.promptserver.domain.model.Tag;

/**
 * 프롬프트 등록 결과를 반환하는 응답 DTO입니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterPromptResponse {
    private Long id;
    private UUID uuid;
    private String title;
    private Long currentVersionId;
    private Long categoryId;
    private Long createdById;
    private String visibility;
    private String status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> tags;

    /**
     * PromptTemplate과 태그 정보를 기반으로 응답 객체를 생성합니다.
     *
     * @param prompt 프롬프트 템플릿
     * @param tags   태그 집합
     * @return RegisterPromptResponse 객체
     */
    public static RegisterPromptResponse from(PromptTemplate prompt, Set<Tag> tags) {
        return RegisterPromptResponse.builder()
                .id(prompt.getId())
                .uuid(prompt.getUuid())
                .title(prompt.getTitle())
                .currentVersionId(prompt.getCurrentVersionId())
                .categoryId(prompt.getCategoryId())
                .createdById(prompt.getCreatedById())
                .visibility(prompt.getVisibility() != null ? prompt.getVisibility().name() : null)
                .status(prompt.getStatus() != null ? prompt.getStatus().name() : null)
                .description(prompt.getDescription())
                .createdAt(prompt.getCreatedAt())
                .updatedAt(prompt.getUpdatedAt())
                .tags(tags != null ? tags.stream().map(Tag::getName).collect(Collectors.toList()) : null)
                .build();
    }
}
