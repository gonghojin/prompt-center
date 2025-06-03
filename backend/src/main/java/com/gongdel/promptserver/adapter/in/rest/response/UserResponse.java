package com.gongdel.promptserver.adapter.in.rest.response;

import com.gongdel.promptserver.domain.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

/**
 * 사용자 정보를 클라이언트에 응답하기 위한 DTO 클래스입니다.
 * 사용자의 기본 정보를 포함합니다.
 */
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@Schema(description = "사용자 정보 응답 DTO")
public class UserResponse {

    @Schema(description = "사용자 고유 식별자", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String email;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    /**
     * 사용자 도메인 모델로부터 응답 DTO를 생성합니다.
     *
     * @param user 변환할 사용자 도메인 객체
     * @return 사용자 응답 DTO
     */
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getUuid().getValue())
                .email(user.getEmail().getValue())
                .name(user.getName())
                .build();
    }
}
