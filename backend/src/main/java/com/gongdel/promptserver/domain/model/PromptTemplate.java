package com.gongdel.promptserver.domain.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 프롬프트 템플릿을 나타내는 도메인 모델 클래스
 * 프롬프트의 내용과 메타데이터를 관리합니다.
 */
public class PromptTemplate {

  private UUID id;

  private String title;

  private String description;

  private String content;

  private User author;

  private Set<Tag> tags;

  private Visibility visibility;

  private PromptStats stats;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  /**
   * 기본 생성자
   */
  public PromptTemplate() {
    this.tags = new HashSet<>();
    this.stats = new PromptStats();
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * 모든 필드를 초기화하는 생성자
   *
   * @param id          프롬프트 ID
   * @param title       제목
   * @param description 설명
   * @param content     내용
   * @param author      작성자
   * @param tags        태그 목록
   * @param visibility  가시성(공개/비공개)
   */
  public PromptTemplate(
      UUID id,
      String title,
      String description,
      String content,
      User author,
      Set<Tag> tags,
      Visibility visibility) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.content = content;
    this.author = author;
    this.tags = tags != null ? new HashSet<>(tags) : new HashSet<>();
    this.visibility = visibility;
    this.stats = new PromptStats();
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * 프롬프트 템플릿 정보를 업데이트합니다.
   *
   * @param title       업데이트할 제목
   * @param description 업데이트할 설명
   * @param content     업데이트할 내용
   * @param tags        업데이트할 태그 목록
   * @param visibility  업데이트할 가시성
   * @throws PromptValidationException 유효성 검증에 실패한 경우
   */
  public void update(String title, String description, String content, Set<Tag> tags,
      Visibility visibility) throws PromptValidationException {
    try {
      validateUpdateInput(title, content);
      this.title = title;
      this.description = description;
      this.content = content;
      this.tags = tags != null ? new HashSet<>(tags) : new HashSet<>();
      this.visibility = visibility;
      this.updatedAt = LocalDateTime.now();
    } catch (IllegalArgumentException e) {
      throw new PromptValidationException("Failed to update prompt", e);
    }
  }

  /**
   * 입력값의 유효성을 검증합니다.
   *
   * @param title   검증할 제목
   * @param content 검증할 내용
   * @throws IllegalArgumentException 유효성 검증에 실패한 경우
   */
  private void validateUpdateInput(String title, String content) {
    if (title == null || title.trim().isEmpty()) {
      throw new IllegalArgumentException("Title cannot be empty");
    }
    if (title.length() > 200) {
      throw new IllegalArgumentException("Title must be less than 200 characters");
    }
    if (content == null || content.trim().isEmpty()) {
      throw new IllegalArgumentException("Content cannot be empty");
    }
    if (content.length() > 4000) {
      throw new IllegalArgumentException("Content must be less than 4000 characters");
    }
  }

  /**
   * 프롬프트가 공개인지 여부를 반환합니다.
   *
   * @return 공개 여부
   */
  public boolean isPublic() {
    return Visibility.PUBLIC.equals(this.visibility);
  }

  // Getter 메소드
  public UUID getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getContent() {
    return content;
  }

  public User getAuthor() {
    return author;
  }

  public Set<Tag> getTags() {
    return new HashSet<>(tags);
  }

  public Visibility getVisibility() {
    return visibility;
  }

  public PromptStats getStats() {
    return stats;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public int getViewCount() {
    return stats.getViewCount();
  }

  public int getFavoriteCount() {
    return stats.getFavoriteCount();
  }

  /**
   * PromptTemplate 객체를 생성하기 위한 빌더 클래스
   */
  public static class Builder {
    private UUID id;

    private String title;

    private String description;

    private String content;

    private User author;

    private Set<Tag> tags;

    private Visibility visibility;

    public Builder id(UUID id) {
      this.id = id;
      return this;
    }

    public Builder title(String title) {
      this.title = title;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder content(String content) {
      this.content = content;
      return this;
    }

    public Builder author(User author) {
      this.author = author;
      return this;
    }

    public Builder tags(Set<Tag> tags) {
      this.tags = tags;
      return this;
    }

    public Builder visibility(Visibility visibility) {
      this.visibility = visibility;
      return this;
    }

    /**
     * 프롬프트의 공개 여부를 설정합니다. (기존 API 호환성 유지)
     *
     * @param isPublic true면 PUBLIC, false면 PRIVATE로 설정
     * @return 빌더 인스턴스
     */
    public Builder isPublic(boolean isPublic) {
      this.visibility = isPublic ? Visibility.PUBLIC : Visibility.PRIVATE;
      return this;
    }

    public PromptTemplate build() {
      return new PromptTemplate(id, title, description, content, author, tags, visibility);
    }
  }

  /**
   * 빌더 객체를 생성합니다.
   *
   * @return 새로운 Builder 인스턴스
   */
  public static Builder builder() {
    return new Builder();
  }
}
