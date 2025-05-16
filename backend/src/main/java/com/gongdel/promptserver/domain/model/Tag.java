package com.gongdel.promptserver.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Tag {

  private UUID id;

  private String name;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  @Builder
  public Tag(UUID id, String name) {
    this.id = id;
    this.name = name;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  public void update(String name) {
    this.name = name;
    this.updatedAt = LocalDateTime.now();
  }
}
