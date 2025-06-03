package com.gongdel.promptserver.adapter.in.rest.response;

import lombok.*;

/**
 * 로그인 응답 DTO입니다.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
}
