package com.gongdel.promptserver.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 태그 정보를 나타내는 도메인 모델 클래스입니다.
 * 프롬프트 분류에 사용됩니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@ToString(of = { "id", "name" })
public class Tag {

  private UUID id;
  private String name;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  /**
   * 태그를 생성합니다.
   *
   * @param id   태그 ID
   * @param name 태그 이름
   */
  @Builder
  public Tag(UUID id, String name) {
    this.id = Objects.requireNonNullElseGet(id, UUID::randomUUID);
    this.name = name;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * 태그 정보를 업데이트합니다.
   *
   * @param name 변경할 태그 이름
   */
  public void update(String name) {
    this.name = name;
    this.updatedAt = LocalDateTime.now();
  }
}
