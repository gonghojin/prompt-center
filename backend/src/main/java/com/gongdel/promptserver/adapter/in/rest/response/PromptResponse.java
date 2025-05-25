package com.gongdel.promptserver.adapter.in.rest.response;

import com.gongdel.promptserver.domain.model.PromptTemplate;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * 프롬프트 템플릿 정보를 클라이언트에 응답하기 위한 DTO 클래스입니다. 프롬프트 템플릿의 기본 정보, 작성자 정보, 태그 정보를
 * 포함합니다.
 */
@Getter
@Builder
public class PromptResponse {

    private final UUID id;
    private final String title;
    private final String description;
    private final String content;
    private final UserResponse author;
    private final Set<TagResponse> tags;
    private final boolean isPublic;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final int viewCount;
    private final int favoriteCount;
    private final Long categoryId;
    private final String visibility;
    private final String status;

    /**
     * 도메인 모델 객체로부터 응답 DTO를 생성합니다.
     *
     * @param promptTemplate 변환할 프롬프트 템플릿 도메인 객체
     * @return 프롬프트 응답 DTO
     */
    public static PromptResponse from(PromptTemplate promptTemplate) {
        return PromptResponse.builder()
            .id(promptTemplate.getUuid())
            .title(promptTemplate.getTitle())
            .description(promptTemplate.getDescription())
            .content(getPromptContent(promptTemplate))
            .author(createAuthorResponse(promptTemplate))
            .tags(getTags(promptTemplate))
            .isPublic(promptTemplate.isPublic())
            .createdAt(promptTemplate.getCreatedAt())
            .updatedAt(promptTemplate.getUpdatedAt())
            .viewCount(promptTemplate.getStats().getViewCount())
            .favoriteCount(promptTemplate.getStats().getFavoriteCount())
            .categoryId(promptTemplate.getCategoryId())
            .visibility(promptTemplate.getVisibility().name())
            .status(promptTemplate.getStatus().name())
            .build();
    }

    /**
     * 프롬프트 템플릿의 내용을 가져옵니다. 실제 구현에서는 현재 버전 ID를 사용하여 프롬프트 버전 저장소에서 가져와야 합니다.
     *
     * @param promptTemplate 프롬프트 템플릿
     * @return 프롬프트 내용
     */
    private static String getPromptContent(PromptTemplate promptTemplate) {
        if (promptTemplate.getVersion() != null) {
            return promptTemplate.getVersion().getContent();
        }
        return "";
    }

    /**
     * 작성자 정보를 생성합니다. 실제 구현에서는 createdById를 사용하여 사용자 저장소에서 작성자 정보를 가져와야 합니다.
     *
     * @param promptTemplate 프롬프트 템플릿
     * @return 작성자 응답 DTO
     */
    private static UserResponse createAuthorResponse(PromptTemplate promptTemplate) {
        if (promptTemplate.getCreatedBy() != null) {
            return UserResponse.from(promptTemplate.getCreatedBy());
        }
        return null;
    }

    /**
     * 태그 정보를 가져옵니다. 실제 구현에서는 태그 저장소에서 템플릿에 연결된 태그 정보를 가져와야 합니다.
     *
     * @param promptTemplate 프롬프트 템플릿
     * @return 태그 응답 DTO 세트
     */
    private static Set<TagResponse> getTags(PromptTemplate promptTemplate) {
        if (promptTemplate.getTags() != null && !promptTemplate.getTags().isEmpty()) {
            return promptTemplate.getTags().stream()
                .map(TagResponse::from)
                .collect(java.util.stream.Collectors.toSet());
        }
        return Set.of();
    }
}
