package com.gongdel.promptserver.application.usecase.command;

import com.gongdel.promptserver.adapter.in.rest.response.TokenRefreshResponse;
import com.gongdel.promptserver.adapter.in.rest.response.auth.LoginResponse;
import com.gongdel.promptserver.application.port.in.AuthCommandUseCase;
import com.gongdel.promptserver.application.port.in.command.LoginCommand;
import com.gongdel.promptserver.application.port.in.command.LogoutCommand;
import com.gongdel.promptserver.application.port.in.command.SignUpCommand;
import com.gongdel.promptserver.application.port.in.command.TokenRefreshCommand;
import com.gongdel.promptserver.application.port.out.command.*;
import com.gongdel.promptserver.application.port.out.query.LoadRefreshTokenPort;
import com.gongdel.promptserver.application.port.out.query.LoadRolePort;
import com.gongdel.promptserver.application.port.out.query.LoadUserPort;
import com.gongdel.promptserver.common.security.JwtTokenProvider;
import com.gongdel.promptserver.common.security.SecurityUserDetails;
import com.gongdel.promptserver.domain.exception.AuthErrorType;
import com.gongdel.promptserver.domain.exception.AuthException;
import com.gongdel.promptserver.domain.exception.TokenException;
import com.gongdel.promptserver.domain.exception.TokenValidationException;
import com.gongdel.promptserver.domain.logout.LogoutToken;
import com.gongdel.promptserver.domain.refreshtoken.RefreshToken;
import com.gongdel.promptserver.domain.role.Role;
import com.gongdel.promptserver.domain.user.*;
import com.gongdel.promptserver.domain.userauth.UserAuthentication;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 인증 관련 서비스를 제공하는 클래스입니다.
 * 회원가입, 로그인, 토큰 발급/갱신, 로그아웃 기능을 담당합니다.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthCommandService implements AuthCommandUseCase {
    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final SaveUserAuthenticationPort saveUserAuthPort;
    private final SaveUserRolePort saveUserRolePort;
    private final PasswordEncoder passwordEncoder;
    private final UserDomainService userDomainService;
    private final LoadRolePort loadRolePort;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final LogoutPort logoutPort;
    private final SaveRefreshTokenPort saveRefreshTokenPort;
    private final LoadRefreshTokenPort loadRefreshTokenPort;

    @PostConstruct
    public void init() {
        log.info("Injected AuthenticationManager class: {}", authenticationManager.getClass().getName());
    }

    /**
     * 회원가입을 처리합니다.
     *
     * @param command 회원가입 요청 정보
     * @throws AuthException 이메일 중복 또는 비밀번호 정책 위반 시 발생
     */
    @Override
    public void signUp(SignUpCommand command) {
        Assert.notNull(command, "SignUpCommand must not be null");
        try {
            validateSignUpRequest(command);

            User savedUser = createAndSaveUser(command);
            createAndSaveUserAuthentication(savedUser.getId(), command.getPassword());
            assignDefaultRole(savedUser.getId());

            log.info("User signed up successfully: userId={}", savedUser.getUuid());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid signup request: {}", e.getClass().getSimpleName());
            throw new AuthException(AuthErrorType.VALIDATION_ERROR, "회원가입 요청이 유효하지 않습니다.");
        }
    }

    /**
     * 로그인을 처리하고 JWT 토큰을 발급합니다.
     *
     * @param command 로그인 요청 정보
     * @return 로그인 응답 (JWT 토큰 포함)
     * @throws AuthException  인증 실패 시 발생
     * @throws TokenException 토큰 생성/저장 실패 시 발생
     */
    @Override
    public LoginResponse login(LoginCommand command) {
        Assert.notNull(command, "LoginCommand must not be null");
        User user = null;
        try {
            Authentication authentication = authenticateUser(command);
            SecurityUserDetails userDetails = (SecurityUserDetails) authentication.getPrincipal();
            user = userDetails.getUser();

            String accessToken = generateAccessToken(user);
            String refreshToken = generateAndSaveRefreshToken(user);

            log.info("User logged in successfully: userId={}", user.getUuid());
            return new LoginResponse(accessToken, refreshToken, "Bearer");
        } catch (BadCredentialsException e) {
            log.warn("Login failed: invalid credentials");
            throw AuthException.invalidCredentials();
        } catch (Exception e) {
            log.error("Token generation failed: {}", e.getClass().getSimpleName());
            throw TokenException.saveFailed(user != null ? user.getUuid().toString() : "unknown", e);
        }
    }

    /**
     * 리프레시 토큰을 사용하여 액세스 토큰을 재발급합니다.
     *
     * @param command 리프레시 토큰 요청 정보
     * @return 토큰 갱신 응답
     * @throws TokenException 유효하지 않은 리프레시 토큰 또는 토큰 만료 시 발생
     */
    @Override
    public TokenRefreshResponse refresh(TokenRefreshCommand command) {
        Assert.notNull(command, "TokenRefreshCommand must not be null");
        try {
            if (command.getRefreshToken() == null) {
                throw TokenValidationException.nullRefreshToken();
            }

            validateRefreshToken(command.getRefreshToken());
            User user = getUserFromRefreshToken(command.getRefreshToken());
            String newAccessToken = generateAccessToken(user);

            log.info("Access token refreshed successfully: userId={}", user.getUuid());
            return new TokenRefreshResponse(newAccessToken, "Bearer");
        } catch (TokenValidationException e) {
            log.warn("Token validation failed: {}", e.getClass().getSimpleName());
            throw e;
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getClass().getSimpleName());
            throw TokenException.saveFailed(command.getRefreshToken(), e);
        }
    }

    /**
     * 로그아웃을 처리합니다.
     *
     * @param command 로그아웃 요청 정보
     * @throws TokenException 유효하지 않은 토큰 또는 블랙리스트 등록 실패 시 발생
     */
    @Override
    public void logout(LogoutCommand command) {
        Assert.notNull(command, "LogoutCommand must not be null");
        try {
            validateAccessToken(command.getAccessToken());
            LogoutToken logoutToken = createLogoutToken(command.getAccessToken());
            logoutPort.logout(logoutToken);

            log.info("User logged out successfully: userId={}", logoutToken.getUserId());
        } catch (TokenValidationException e) {
            log.warn("Token validation failed: {}", e.getClass().getSimpleName());
            throw e;
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getClass().getSimpleName());
            throw TokenException.blacklistFailed(command.getAccessToken(), e);
        }
    }

    // Private helper methods

    private void validateSignUpRequest(SignUpCommand command) {
        Email email = command.getEmail();
        if (loadUserPort.loadUserByEmail(email).isPresent()) {
            log.warn("Sign up failed: email already exists");
            throw AuthException.duplicateEmail(email.getValue());
        }
        userDomainService.validatePasswordPolicy(command.getPassword());
    }

    private User createAndSaveUser(SignUpCommand command) {
        User user = User.register(
            command.getEmail(),
            command.getName(),
            null,
            UserStatus.ACTIVE,
            LocalDateTime.now(),
            LocalDateTime.now());
        return saveUserPort.saveUser(user);
    }

    private void createAndSaveUserAuthentication(Long userId, Password password) {
        String encodedPassword = passwordEncoder.encode(password.toRaw());
        UserAuthentication userAuth = UserAuthentication.register(
            userId,
            encodedPassword,
            LocalDateTime.now());
        saveUserAuthPort.saveUserAuthentication(userAuth);

    }

    private void assignDefaultRole(Long userId) {
        Role role = loadRolePort.loadRoleByName("ROLE_USER")
            .orElseThrow(() -> new IllegalStateException("기본 권한(ROLE_USER)이 존재하지 않습니다. 데이터베이스를 확인하세요."));
        UserRole userRole = UserRole.register(userId, role.getId());
        saveUserRolePort.saveUserRole(userRole);
    }

    private Authentication authenticateUser(LoginCommand command) {
        return authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                command.getEmail(),
                command.getPassword().toRaw()));
    }

    private String generateAccessToken(User user) {
        return jwtTokenProvider.generateAccessToken(user);
    }

    private String generateAndSaveRefreshToken(User user) {
        try {
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);
            Date refreshTokenExpiry = jwtTokenProvider.getExpiration(refreshToken);
            RefreshToken refreshTokenDomain = RefreshToken.create(
                user.getUuid(),
                refreshToken,
                refreshTokenExpiry.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            saveRefreshTokenPort.saveRefreshToken(refreshTokenDomain);
            return refreshToken;
        } catch (Exception e) {
            throw TokenException.saveFailed(user.getUuid().toString(), e);
        }
    }

    private void validateRefreshToken(String refreshToken) {
        if (!loadRefreshTokenPort.isRefreshTokenValid(refreshToken)) {
            log.warn("Invalid refresh token attempted");
            throw TokenException.notFound(refreshToken);
        }
    }

    private User getUserFromRefreshToken(String refreshToken) {
        UserId userId = jwtTokenProvider.getUserId(refreshToken);
        return loadUserPort.loadUserByUserId(userId)
            .orElseThrow(() -> {
                log.warn("User not found for refresh token");
                return AuthException.userNotFound(userId.toString());
            });
    }

    private void validateAccessToken(String accessToken) {
        if (!jwtTokenProvider.validateToken(accessToken)) {
            log.warn("Invalid token attempted for logout");
            throw TokenValidationException.invalidTokenFormat();
        }
    }

    private LogoutToken createLogoutToken(String accessToken) {
        String tokenId = jwtTokenProvider.getTokenId(accessToken);
        UserId userId = jwtTokenProvider.getUserId(accessToken);
        LocalDateTime expiresAt = jwtTokenProvider.getExpirationDate(accessToken);
        return LogoutToken.create(userId, tokenId, expiresAt);
    }
}
