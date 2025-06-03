package com.gongdel.promptserver.application.usecase.command;

import com.gongdel.promptserver.adapter.in.rest.response.LoginResponse;
import com.gongdel.promptserver.adapter.in.rest.response.TokenRefreshResponse;
import com.gongdel.promptserver.application.port.in.command.*;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthCommandService 테스트")
class AuthCommandServiceTest {

    @Mock
    private LoadUserPort loadUserPort;
    @Mock
    private SaveUserPort saveUserPort;
    @Mock
    private SaveUserAuthenticationPort saveUserAuthPort;
    @Mock
    private SaveUserRolePort saveUserRolePort;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserDomainService userDomainService;
    @Mock
    private LoadRolePort loadRolePort;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private LogoutPort logoutPort;
    @Mock
    private SaveRefreshTokenPort saveRefreshTokenPort;
    @Mock
    private LoadRefreshTokenPort loadRefreshTokenPort;

    @InjectMocks
    private AuthCommandService authCommandService;

    @Nested
    @DisplayName("signUp 메서드는")
    class SignUpTest {
        private SignUpCommand signUpCommand;
        private User mockUser;
        private Role mockRole;

        @BeforeEach
        void setUp() {
            signUpCommand = SignUpCommand.builder()
                    .email("test@example.com")
                    .password("password123")
                    .name("Test User")
                    .build();
            mockUser = mock(User.class);
            mockRole = mock(Role.class);
        }

        @Test
        @DisplayName("회원가입을 성공적으로 처리한다")
        void givenValidSignUpCommand_whenSignUp_thenSuccess() {
            // Given
            when(loadUserPort.loadUserByEmail(any(Email.class))).thenReturn(Optional.empty());
            when(saveUserPort.saveUser(any(User.class))).thenReturn(mockUser);
            when(loadRolePort.loadRoleByName("ROLE_USER")).thenReturn(Optional.of(mockRole));
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

            // When
            authCommandService.signUp(signUpCommand);

            // Then
            verify(loadUserPort).loadUserByEmail(any(Email.class));
            verify(saveUserPort).saveUser(any(User.class));
            verify(saveUserAuthPort).saveUserAuthentication(any(UserAuthentication.class));
            verify(saveUserRolePort).saveUserRole(any(UserRole.class));
        }

        @Test
        @DisplayName("이메일이 중복되면 AuthException을 던진다")
        void givenDuplicateEmail_whenSignUp_thenThrowsAuthException() {
            // Given
            when(loadUserPort.loadUserByEmail(any(Email.class))).thenReturn(Optional.of(mockUser));

            // When & Then
            assertThatThrownBy(() -> authCommandService.signUp(signUpCommand))
                    .isInstanceOf(AuthException.class)
                    .hasMessage("이미 사용 중인 이메일입니다: " + signUpCommand.getEmail());

            verify(loadUserPort).loadUserByEmail(any(Email.class));
            verify(saveUserPort, never()).saveUser(any(User.class));
            verify(saveUserAuthPort, never()).saveUserAuthentication(any(UserAuthentication.class));
            verify(saveUserRolePort, never()).saveUserRole(any(UserRole.class));
        }

        @Test
        @DisplayName("비밀번호 정책을 위반하면 AuthException을 던진다")
        void givenInvalidPassword_whenSignUp_thenThrowsAuthException() {
            // Given
            when(loadUserPort.loadUserByEmail(any(Email.class))).thenReturn(Optional.empty());
            doThrow(new IllegalArgumentException("Invalid password")).when(userDomainService)
                    .validatePasswordPolicy(any(Password.class));

            // When & Then
            assertThatThrownBy(() -> authCommandService.signUp(signUpCommand))
                    .isInstanceOf(AuthException.class)
                    .hasMessage("회원가입 요청이 유효하지 않습니다.");

            verify(loadUserPort).loadUserByEmail(any(Email.class));
            verify(userDomainService).validatePasswordPolicy(any(Password.class));
            verify(saveUserPort, never()).saveUser(any(User.class));
            verify(saveUserAuthPort, never()).saveUserAuthentication(any(UserAuthentication.class));
            verify(saveUserRolePort, never()).saveUserRole(any(UserRole.class));
        }
    }

    @Nested
    @DisplayName("login 메서드는")
    class LoginTest {
        private LoginCommand loginCommand;
        private Authentication authentication;
        private SecurityUserDetails userDetails;
        private User mockUser;

        @BeforeEach
        void setUp() {
            loginCommand = LoginCommand.builder()
                    .email("test@example.com")
                    .password("password123")
                    .build();
            mockUser = mock(User.class);
            userDetails = mock(SecurityUserDetails.class);
            authentication = mock(Authentication.class);
        }

        @Test
        @DisplayName("로그인을 성공적으로 처리하고 토큰을 발급한다")
        void givenValidCredentials_whenLogin_thenReturnsTokens() {
            // Given
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUser()).thenReturn(mockUser);
            when(mockUser.getUuid()).thenReturn(new UserId(UUID.randomUUID()));
            when(mockUser.getEmail()).thenReturn(new Email("test@example.com"));
            when(jwtTokenProvider.generateAccessToken(any(), anyString())).thenReturn("accessToken");
            when(jwtTokenProvider.generateRefreshToken(any(UserId.class))).thenReturn("refreshToken");
            when(jwtTokenProvider.getExpiration(anyString())).thenReturn(new Date());

            // When
            LoginResponse response = authCommandService.login(loginCommand);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("accessToken");
            assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
            assertThat(response.getTokenType()).isEqualTo("Bearer");
        }

        @Test
        @DisplayName("잘못된 자격증명으로 로그인하면 AuthException을 던진다")
        void givenInvalidCredentials_whenLogin_thenThrowsAuthException() {
            // Given
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Invalid credentials"));

            // When & Then
            assertThatThrownBy(() -> authCommandService.login(loginCommand))
                    .isInstanceOf(AuthException.class);
        }
    }

    @Nested
    @DisplayName("refresh 메서드는")
    class RefreshTest {
        private TokenRefreshCommand refreshCommand;
        private User mockUser;
        private UserId userId;
        private Email email;

        @BeforeEach
        void setUp() {
            refreshCommand = TokenRefreshCommand.builder()
                    .refreshToken("refreshToken")
                    .build();
            mockUser = mock(User.class);
            userId = new UserId(UUID.randomUUID());
            email = new Email("test@example.com");
        }

        @Test
        @DisplayName("리프레시 토큰으로 액세스 토큰을 성공적으로 갱신한다")
        void givenValidRefreshToken_whenRefresh_thenReturnsNewAccessToken() {
            // Given
            when(loadRefreshTokenPort.isRefreshTokenValid(anyString())).thenReturn(true);
            when(jwtTokenProvider.getUserId(anyString())).thenReturn(userId);
            when(loadUserPort.loadUserByUserId(userId)).thenReturn(Optional.of(mockUser));
            when(mockUser.getEmail()).thenReturn(email);
            when(mockUser.getUuid()).thenReturn(userId);
            when(jwtTokenProvider.generateAccessToken(userId, email.getValue())).thenReturn("newAccessToken");

            // When
            TokenRefreshResponse response = authCommandService.refresh(refreshCommand);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("newAccessToken");
            assertThat(response.getTokenType()).isEqualTo("Bearer");
            verify(loadRefreshTokenPort).isRefreshTokenValid(refreshCommand.getRefreshToken());
            verify(jwtTokenProvider).getUserId(refreshCommand.getRefreshToken());
            verify(loadUserPort).loadUserByUserId(userId);
            verify(jwtTokenProvider).generateAccessToken(userId, email.getValue());
        }

        @Test
        @DisplayName("유효하지 않은 리프레시 토큰으로 갱신하면 TokenException을 던진다")
        void givenInvalidRefreshToken_whenRefresh_thenThrowsTokenException() {
            // Given
            when(loadRefreshTokenPort.isRefreshTokenValid(anyString())).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> authCommandService.refresh(refreshCommand))
                    .isInstanceOf(TokenException.class);
            verify(loadRefreshTokenPort).isRefreshTokenValid(refreshCommand.getRefreshToken());
            verify(jwtTokenProvider, never()).getUserId(anyString());
            verify(loadUserPort, never()).loadUserByUserId(any(UserId.class));
        }
    }

    @Nested
    @DisplayName("logout 메서드는")
    class LogoutTest {
        private LogoutCommand logoutCommand;

        @BeforeEach
        void setUp() {
            logoutCommand = LogoutCommand.builder()
                    .accessToken("validAccessToken")
                    .build();
        }

        @Test
        @DisplayName("로그아웃을 성공적으로 처리한다")
        void givenValidAccessToken_whenLogout_thenSuccess() {
            // Given
            when(jwtTokenProvider.validateToken(anyString())).thenReturn(true);
            when(jwtTokenProvider.getTokenId(anyString())).thenReturn("tokenId");
            when(jwtTokenProvider.getUserId(anyString())).thenReturn(new UserId(UUID.randomUUID()));
            when(jwtTokenProvider.getExpirationDate(anyString())).thenReturn(LocalDateTime.now());

            // When
            authCommandService.logout(logoutCommand);

            // Then
            verify(logoutPort).logout(any(LogoutToken.class));
        }

        @Test
        @DisplayName("유효하지 않은 액세스 토큰으로 로그아웃하면 TokenValidationException을 던진다")
        void givenInvalidAccessToken_whenLogout_thenThrowsTokenValidationException() {
            // Given
            when(jwtTokenProvider.validateToken(anyString())).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> authCommandService.logout(logoutCommand))
                    .isInstanceOf(TokenValidationException.class);
        }
    }
}
