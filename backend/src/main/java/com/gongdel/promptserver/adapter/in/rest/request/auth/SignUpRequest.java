package com.gongdel.promptserver.adapter.in.rest.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 회원가입 요청 DTO입니다.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(exclude = { "password" })
@Schema(description = "회원가입 요청 DTO")
public class SignUpRequest {
    @Schema(description = "사용자 이메일", example = "user@example.com", required = true)
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @Schema(description = "사용자 비밀번호", example = "password123", required = true)
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @Schema(description = "사용자 이름", example = "홍길동", required = true)
    @NotBlank(message = "이름은 필수입니다.")
    private String name;
}
