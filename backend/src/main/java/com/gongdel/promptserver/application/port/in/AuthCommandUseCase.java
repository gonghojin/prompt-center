package com.gongdel.promptserver.application.port.in;

import com.gongdel.promptserver.adapter.in.rest.response.TokenRefreshResponse;
import com.gongdel.promptserver.adapter.in.rest.response.auth.LoginResponse;
import com.gongdel.promptserver.application.port.in.command.LoginCommand;
import com.gongdel.promptserver.application.port.in.command.LogoutCommand;
import com.gongdel.promptserver.application.port.in.command.SignUpCommand;
import com.gongdel.promptserver.application.port.in.command.TokenRefreshCommand;

/**
 * 인증 관련 유스케이스 인터페이스
 */
public interface AuthCommandUseCase {
    /**
     * 회원가입
     */
    void signUp(SignUpCommand command);

    /**
     * 로그인
     */
    LoginResponse login(LoginCommand command);

    /**
     * 토큰 갱신
     */
    TokenRefreshResponse refresh(TokenRefreshCommand command);

    /**
     * 로그아웃
     */
    void logout(LogoutCommand command);
}
