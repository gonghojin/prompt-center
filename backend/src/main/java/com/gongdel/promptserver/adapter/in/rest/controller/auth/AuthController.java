package com.gongdel.promptserver.adapter.in.rest.controller.auth;

import com.gongdel.promptserver.adapter.in.rest.request.auth.LoginRequest;
import com.gongdel.promptserver.adapter.in.rest.request.auth.SignUpRequest;
import com.gongdel.promptserver.adapter.in.rest.request.auth.TokenRefreshRequest;
import com.gongdel.promptserver.adapter.in.rest.response.TokenRefreshResponse;
import com.gongdel.promptserver.adapter.in.rest.response.auth.LoginResponse;
import com.gongdel.promptserver.application.port.in.AuthCommandUseCase;
import com.gongdel.promptserver.application.port.in.command.LoginCommand;
import com.gongdel.promptserver.application.port.in.command.LogoutCommand;
import com.gongdel.promptserver.application.port.in.command.SignUpCommand;
import com.gongdel.promptserver.application.port.in.command.TokenRefreshCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 인증(회원가입, 로그인, 토큰 갱신) API 컨트롤러입니다.
 */
@Slf4j
@Tag(name = "인증", description = "회원가입, 로그인, 토큰 갱신, 로그아웃 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthCommandUseCase authCommandUseCase;

    /**
     * 회원가입 API
     */
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "회원가입 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일")
    })
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@Valid @RequestBody SignUpRequest request) {
        log.info("Sign up request received for email: {}", request.getEmail());
        SignUpCommand command = buildSignUpCommand(request);
        authCommandUseCase.signUp(command);
        log.info("Sign up completed for email: {}", request.getEmail());
    }

    /**
     * 로그인 API
     */
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for email: {}", request.getEmail());
        LoginCommand command = buildLoginCommand(request);
        return authCommandUseCase.login(command);
    }

    /**
     * 토큰 갱신 API
     */
    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "토큰 갱신 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰")
    })
    @PostMapping("/refresh")
    public TokenRefreshResponse refresh(@Valid @RequestBody TokenRefreshRequest request) {
        log.info("Token refresh request received");
        TokenRefreshCommand command = buildTokenRefreshCommand(request);
        return authCommandUseCase.refresh(command);
    }

    /**
     * 로그아웃 API
     */
    @Operation(summary = "로그아웃", description = "현재 사용자의 액세스 토큰을 무효화합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청")
    })
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(
        @Parameter(description = "Bearer 토큰", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") @RequestHeader("Authorization") String authorization) {
        log.info("Logout request received");
        String accessToken = authorization.replace("Bearer ", "");
        LogoutCommand command = buildLogoutCommand(accessToken);
        authCommandUseCase.logout(command);
        log.info("Logout completed");
    }

    private SignUpCommand buildSignUpCommand(SignUpRequest request) {
        return SignUpCommand.builder()
            .email(request.getEmail())
            .password(request.getPassword())
            .name(request.getName())
            .build();
    }

    private LoginCommand buildLoginCommand(LoginRequest request) {
        return LoginCommand.builder()
            .email(request.getEmail())
            .password(request.getPassword())
            .build();
    }

    private TokenRefreshCommand buildTokenRefreshCommand(TokenRefreshRequest request) {
        return TokenRefreshCommand.builder()
            .refreshToken(request.getRefreshToken())
            .build();
    }

    private LogoutCommand buildLogoutCommand(String accessToken) {
        return LogoutCommand.builder()
            .accessToken(accessToken)
            .build();
    }
}
