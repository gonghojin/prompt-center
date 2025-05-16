package com.gongdel.promptserver.adapter.in.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
   * 프롬프트 제목
   * 비어 있지 않아야 하며 200자 이내여야 합니다.
   */
  @NotBlank(message = "Title is required")
  @Size(max = 200, message = "Title must be less than 200 characters")
  private String title;

  /**
   * 프롬프트 설명
   * 선택 사항이며 1000자 이내여야 합니다.
   */
  @Size(max = 1000, message = "Description must be less than 1000 characters")
  private String description;

  /**
   * 프롬프트 내용
   * 비어 있지 않아야 하며 20000자 이내여야 합니다.
   */
  @NotBlank(message = "Content is required")
  @Size(max = 20000, message = "Content must be less than 20000 characters")
  private String content;

  /**
   * 프롬프트에 연결할 태그 ID 목록
   */
  @Builder.Default
  private Set<UUID> tagIds = new HashSet<>();

  /**
   * 프롬프트 공개 여부
   * true: 공개, false: 비공개
   */
  private boolean isPublic;
}
