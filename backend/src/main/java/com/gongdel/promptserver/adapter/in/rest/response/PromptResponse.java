package com.gongdel.promptserver.adapter.in.rest.response;

import com.gongdel.promptserver.domain.model.PromptTemplate;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

/**
 * 프롬프트 템플릿 정보를 클라이언트에 응답하기 위한 DTO 클래스입니다.
 * 프롬프트 템플릿의 기본 정보, 작성자 정보, 태그 정보를 포함합니다.
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

  /**
   * 도메인 모델 객체로부터 응답 DTO를 생성합니다.
   *
   * @param promptTemplate 변환할 프롬프트 템플릿 도메인 객체
   * @return 프롬프트 응답 DTO
   */
  public static PromptResponse from(PromptTemplate promptTemplate) {
    return PromptResponse.builder()
        .id(promptTemplate.getId())
        .title(promptTemplate.getTitle())
        .description(promptTemplate.getDescription())
        .content(promptTemplate.getContent())
        .author(UserResponse.from(promptTemplate.getAuthor()))
        .tags(promptTemplate.getTags().stream()
            .map(TagResponse::from)
            .collect(Collectors.toSet()))
        .isPublic(promptTemplate.isPublic())
        .createdAt(promptTemplate.getCreatedAt())
        .updatedAt(promptTemplate.getUpdatedAt())
        .viewCount(promptTemplate.getViewCount())
        .favoriteCount(promptTemplate.getFavoriteCount())
        .build();
  }
}
