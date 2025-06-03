package com.gongdel.promptserver.application.port.in.command;

import com.gongdel.promptserver.domain.user.Email;
import com.gongdel.promptserver.domain.user.Password;
import lombok.Builder;
import lombok.Getter;

/**
 * 회원가입 요청을 처리하기 위한 커맨드 객체입니다.
 * 이메일, 비밀번호, 이름을 포함하여 새로운 사용자 등록에 필요한 정보를 전달합니다.
 */
@Getter
public class SignUpCommand {
    /**
     * 사용자 이메일
     */
    private final Email email;

    /**
     * 사용자 비밀번호
     */
    private final Password password;

    /**
     * 사용자 이름
     */
    private final String name;

    /**
     * 회원가입 커맨드를 생성합니다.
     *
     * @param email    사용자 이메일
     * @param password 사용자 비밀번호
     * @param name     사용자 이름
     * @throws IllegalArgumentException 이메일, 비밀번호, 이름이 null이거나 비어있는 경우
     */
    @Builder
    public SignUpCommand(String email, String password, String name) {
        this.email = new Email(email);
        this.password = new Password(password);
        this.name = name;
    }
}
