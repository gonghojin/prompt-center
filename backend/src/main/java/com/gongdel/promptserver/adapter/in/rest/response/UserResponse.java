package com.gongdel.promptserver.adapter.in.rest.response;

import com.gongdel.promptserver.domain.model.User;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 정보를 클라이언트에 응답하기 위한 DTO 클래스입니다.
 * 사용자의 기본 정보를 포함합니다.
 */
@Getter
@Builder
public class UserResponse {

    private final UUID id;
    private final String email;
    private final String name;

    /**
     * 사용자 도메인 모델로부터 응답 DTO를 생성합니다.
     *
     * @param user 변환할 사용자 도메인 객체
     * @return 사용자 응답 DTO
     */
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
