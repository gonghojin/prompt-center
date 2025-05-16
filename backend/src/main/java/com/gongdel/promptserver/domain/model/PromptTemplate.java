package com.gongdel.promptserver.domain.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 프롬프트 템플릿을 나타내는 도메인 모델 클래스
 * 프롬프트의 내용과 메타데이터를 관리합니다.
 */
@Getter
@ToString(exclude = { "content" })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class PromptTemplate {
  private static final Logger log = LoggerFactory.getLogger(PromptTemplate.class);

  // 상수 정의
  public static final int MAX_TITLE_LENGTH = 200;
  public static final int MAX_CONTENT_LENGTH = 20000;
  public static final int MAX_DESCRIPTION_LENGTH = 1000;

  // 필수 필드
  private UUID id;
  private String title;
  private String description;
  private String content;
  private User author;
  private Visibility visibility;

  // 부가 필드
  private final Set<Tag> tags = new HashSet<>();
  private final PromptStats stats = new PromptStats();
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

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
   * @throws PromptValidationException 유효성 검증에 실패한 경우
   */
  @Builder
  public PromptTemplate(
      UUID id,
      String title,
      String description,
      String content,
      User author,
      Set<Tag> tags,
      Visibility visibility) throws PromptValidationException {
    // 기본 유효성 검증
    validateInput(title, content, description);

    // 필드 초기화
    this.id = Objects.requireNonNullElseGet(id, UUID::randomUUID);
    this.title = title;
    this.description = description;
    this.content = content;
    this.author = Objects.requireNonNull(author, "Author cannot be null");
    this.visibility = Objects.requireNonNullElse(visibility, Visibility.PRIVATE);

    // 태그 추가
    if (tags != null) {
      this.tags.addAll(tags);
    }

    // 시간 정보 초기화
    LocalDateTime now = LocalDateTime.now();
    this.createdAt = now;
    this.updatedAt = now;

    log.debug("Created prompt template with id: {}", this.id);
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
  public void update(
      String title,
      String description,
      String content,
      Set<Tag> tags,
      Visibility visibility) throws PromptValidationException {
    // 유효성 검증
    validateInput(title, content, description);

    // 필드 업데이트
    this.title = title;
    this.description = description;
    this.content = content;
    this.visibility = Objects.requireNonNullElse(visibility, Visibility.PRIVATE);

    // 태그 업데이트
    this.tags.clear();
    if (tags != null) {
      this.tags.addAll(tags);
    }

    // 수정 시간 업데이트
    this.updatedAt = LocalDateTime.now();

    log.debug("Updated prompt template with id: {}", this.id);
  }

  /**
   * 입력값의 유효성을 검증합니다.
   *
   * @param title       검증할 제목
   * @param content     검증할 내용
   * @param description 검증할 설명
   * @throws PromptValidationException 유효성 검증에 실패한 경우
   */
  private void validateInput(String title, String content, String description) throws PromptValidationException {
    // 제목 검증
    if (title == null || title.trim().isEmpty()) {
      throw new PromptValidationException("Title cannot be empty");
    }
    if (title.length() > MAX_TITLE_LENGTH) {
      throw new PromptValidationException("Title must be less than " + MAX_TITLE_LENGTH + " characters");
    }

    // 내용 검증
    if (content == null || content.trim().isEmpty()) {
      throw new PromptValidationException("Content cannot be empty");
    }
    if (content.length() > MAX_CONTENT_LENGTH) {
      throw new PromptValidationException("Content must be less than " + MAX_CONTENT_LENGTH + " characters");
    }

    // 설명 검증 (선택 사항)
    if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
      throw new PromptValidationException("Description must be less than " + MAX_DESCRIPTION_LENGTH + " characters");
    }
  }

  /**
   * 프롬프트가 공개인지 여부를 반환합니다.
   *
   * @return 공개 여부
   */
  public boolean isPublic() {
    return visibility != null && visibility.isPublic();
  }

  /**
   * 프롬프트의 모든 태그를 불변 세트로 반환합니다.
   *
   * @return 불변 태그 세트
   */
  public Set<Tag> getTags() {
    return Collections.unmodifiableSet(tags);
  }

  /**
   * 태그를 추가합니다.
   *
   * @param tag 추가할 태그
   * @return 추가 성공 여부
   */
  public boolean addTag(Tag tag) {
    if (tag == null) {
      return false;
    }
    return tags.add(tag);
  }

  /**
   * 태그를 제거합니다.
   *
   * @param tag 제거할 태그
   * @return 제거 성공 여부
   */
  public boolean removeTag(Tag tag) {
    return tags.remove(tag);
  }

  /**
   * 조회수를 반환합니다.
   *
   * @return 조회수
   */
  public int getViewCount() {
    return stats.getViewCount();
  }

  /**
   * 좋아요 수를 반환합니다.
   *
   * @return 좋아요 수
   */
  public int getFavoriteCount() {
    return stats.getFavoriteCount();
  }

  /**
   * 조회수를 증가시킵니다.
   */
  public void incrementViewCount() {
    stats.incrementViewCount();
  }

  /**
   * 좋아요 수를 증가시킵니다.
   */
  public void incrementFavoriteCount() {
    stats.incrementFavoriteCount();
  }

  /**
   * 좋아요 수를 감소시킵니다.
   */
  public void decrementFavoriteCount() {
    stats.decrementFavoriteCount();
  }
}
