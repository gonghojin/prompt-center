package com.gongdel.promptserver.application.port.in.command;

import com.gongdel.promptserver.domain.user.Email;
import com.gongdel.promptserver.domain.user.Password;
import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 요청을 처리하기 위한 커맨드 객체입니다.
 * 이메일과 비밀번호를 포함하여 사용자 인증에 필요한 정보를 전달합니다.
 */
@Getter
public class LoginCommand {
    /**
     * 사용자 이메일
     */
    private final Email email;

    /**
     * 사용자 비밀번호
     */
    private final Password password;

    /**
     * 로그인 커맨드를 생성합니다.
     *
     * @param email    사용자 이메일
     * @param password 사용자 비밀번호
     * @throws IllegalArgumentException 이메일이나 비밀번호가 null이거나 비어있는 경우
     */
    @Builder
    public LoginCommand(String email, String password) {
        this.email = new Email(email);
        this.password = new Password(password);
    }
}
