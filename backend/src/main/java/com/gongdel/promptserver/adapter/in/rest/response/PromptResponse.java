package com.gongdel.promptserver.adapter.in.rest.response;

import com.gongdel.promptserver.domain.model.PromptTemplate;
import com.gongdel.promptserver.domain.model.Tag;
import com.gongdel.promptserver.domain.model.User;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PromptResponse {

  private UUID id;

  private String title;

  private String description;

  private String content;

  private UserResponse author;

  private Set<TagResponse> tags;

  private boolean isPublic;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  private int viewCount;

  private int favoriteCount;

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

  @Getter
  @Builder
  public static class UserResponse {

    private UUID id;

    private String email;

    private String name;

    public static UserResponse from(User user) {
      return UserResponse.builder()
          .id(user.getId())
          .email(user.getEmail())
          .name(user.getName())
          .build();
    }
  }

  @Getter
  @Builder
  public static class TagResponse {

    private UUID id;

    private String name;

    public static TagResponse from(Tag tag) {
      return TagResponse.builder()
          .id(tag.getId())
          .name(tag.getName())
          .build();
    }
  }
}
