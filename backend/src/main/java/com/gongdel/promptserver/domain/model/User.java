package com.gongdel.promptserver.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class User {

  private UUID id;

  private String email;

  private String name;

  private String password;

  private UserRole role;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  @Builder
  public User(
      UUID id,
      String email,
      String name,
      String password,
      UserRole role) {
    this.id = id;
    this.email = email;
    this.name = name;
    this.password = password;
    this.role = role;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  public void update(String name, String password) {
    this.name = name;
    this.password = password;
    this.updatedAt = LocalDateTime.now();
  }
}
