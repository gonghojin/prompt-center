package com.gongdel.promptserver.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 시스템 사용자를 나타내는 도메인 모델입니다.
 * 사용자 식별 정보와 인증 정보, 역할을 포함합니다.
 */
@Getter
@ToString(exclude = "password")
@NoArgsConstructor
public class User {

  private UUID id;

  private String email;

  private String name;

  private String password;

  private UserRole role;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  /**
   * 사용자 객체를 생성합니다.
   *
   * @param id       사용자 고유 식별자
   * @param email    사용자 이메일 주소
   * @param name     사용자 이름
   * @param password 사용자 비밀번호 (암호화된 상태로 저장되어야 함)
   * @param role     사용자 역할
   */
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
    this.role = role != null ? role : UserRole.ROLE_USER; // 기본 역할 설정
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * 사용자 정보를 업데이트합니다.
   * 이름과 비밀번호만 변경 가능하며, 업데이트 시간도 자동으로 갱신됩니다.
   *
   * @param name     변경할 이름
   * @param password 변경할 비밀번호
   */
  public void update(String name, String password) {
    this.name = name;
    this.password = password;
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * 사용자 역할을 변경합니다.
   *
   * @param role 변경할 역할
   */
  public void updateRole(UserRole role) {
    this.role = role;
    this.updatedAt = LocalDateTime.now();
  }
}
