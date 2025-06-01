package com.gongdel.promptserver.adapter.in.rest.response;

import com.gongdel.promptserver.application.dto.RegisterPromptResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 프롬프트 등록 결과 정보를 클라이언트에 응답하기 위한 DTO 클래스입니다.
 */
@Getter
@Builder
public class CreatePromptResponse {

    private final UUID id;
    private final String title;
    private final String description;
    private final String content;
    private final UserResponse author;
    private final Set<String> tags;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Long categoryId;
    private final String visibility;
    private final String status;

    /**
     * RegisterPromptResponse 객체로부터 응답 DTO를 생성합니다.
     *
     * @param response RegisterPromptResponse 객체
     * @return 프롬프트 응답 DTO
     */
    public static CreatePromptResponse from(RegisterPromptResponse response) {
        return CreatePromptResponse.builder()
                .id(response.getUuid())
                .title(response.getTitle())
                .description(response.getDescription())
                .content(null)
                .author(null)
                .tags(response.getTags() != null ? response.getTags().stream().collect(Collectors.toSet()) : Set.of())
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .categoryId(response.getCategoryId())
                .visibility(response.getVisibility())
                .status(response.getStatus())
                .build();
    }
}
