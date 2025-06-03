package com.gongdel.promptserver.common.security;

import com.gongdel.promptserver.application.port.out.query.*;
import com.gongdel.promptserver.domain.user.User;
import com.gongdel.promptserver.domain.user.Email;
import com.gongdel.promptserver.domain.user.UserRole;
import com.gongdel.promptserver.domain.user.UserDomainException;
import com.gongdel.promptserver.domain.user.UserRoleDomainException;
import com.gongdel.promptserver.domain.role.Role;
import com.gongdel.promptserver.domain.userauth.UserAuthentication;
import com.gongdel.promptserver.domain.userauth.UserAuthenticationDomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService 테스트")
class CustomUserDetailsServiceTest {

    @Mock
    private LoadUserPort loadUserPort;
    @Mock
    private LoadUserAuthenticationPort loadUserAuthenticationPort;
    @Mock
    private LoadRolePort loadRolePort;
    @Mock
    private FindUserRolesPort findUserRolesPort;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private static final String VALID_EMAIL = "test@example.com";
    private static final Long USER_ID = 1L;
    private static final Long ROLE_ID = 1L;
    private static final String ROLE_NAME = "ROLE_USER";

    @Nested
    @DisplayName("loadUserByUsername 메서드는")
    class LoadUserByUsernameTest {

        @Nested
        @DisplayName("정상적인 이메일이 주어지면")
        class WhenValidEmailProvided {

            private User user;
            private UserAuthentication userAuth;
            private Role role;

            @BeforeEach
            void setUp() {
                user = User.builder()
                        .id(USER_ID)
                        .email(new Email(VALID_EMAIL))
                        .build();

                userAuth = UserAuthentication.builder()
                        .userId(USER_ID)
                        .build();

                role = Role.builder()
                        .id(ROLE_ID)
                        .name(ROLE_NAME)
                        .build();

                when(loadUserPort.loadUserByEmail(any(Email.class)))
                        .thenReturn(Optional.of(user));
                when(loadUserAuthenticationPort.loadUserAuthenticationByUserId(USER_ID))
                        .thenReturn(Optional.of(userAuth));
                when(findUserRolesPort.findUserRolesByUserId(USER_ID))
                        .thenReturn(Collections.singletonList(UserRole.register(USER_ID, ROLE_ID)));
                when(loadRolePort.loadRoleById(ROLE_ID))
                        .thenReturn(Optional.of(role));
            }

            @Test
            @DisplayName("UserDetails를 반환한다")
            void shouldReturnUserDetails() {
                // when
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(VALID_EMAIL);

                // then
                assertThat(userDetails).isNotNull();
                assertThat(userDetails.getUsername()).isEqualTo(VALID_EMAIL);
            }
        }

        @Nested
        @DisplayName("존재하지 않는 이메일이 주어지면")
        class WhenInvalidEmailProvided {

            @BeforeEach
            void setUp() {
                when(loadUserPort.loadUserByEmail(any(Email.class)))
                        .thenReturn(Optional.empty());
            }

            @Test
            @DisplayName("UsernameNotFoundException을 발생시킨다")
            void shouldThrowUsernameNotFoundException() {
                // when & then
                assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(VALID_EMAIL))
                        .isInstanceOf(UsernameNotFoundException.class)
                        .hasMessageContaining("User not found for email");
            }
        }

        @Nested
        @DisplayName("null 이메일이 주어지면")
        class WhenNullEmailProvided {

            @Test
            @DisplayName("UsernameNotFoundException을 발생시킨다")
            void shouldThrowUsernameNotFoundException() {
                // when & then
                assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(null))
                        .isInstanceOf(UsernameNotFoundException.class)
                        .hasMessageContaining("Username cannot be null or empty");
            }
        }
    }

    @Nested
    @DisplayName("권한 조회 관련")
    class RoleRetrievalTest {

        @Nested
        @DisplayName("사용자의 권한이 없으면")
        class WhenUserHasNoRoles {

            @BeforeEach
            void setUp() {
                when(loadUserPort.loadUserByEmail(any(Email.class)))
                        .thenReturn(Optional.of(User.builder().id(USER_ID).email(new Email(VALID_EMAIL)).build()));
                when(loadUserAuthenticationPort.loadUserAuthenticationByUserId(USER_ID))
                        .thenReturn(Optional.of(UserAuthentication.builder().userId(USER_ID).build()));
                when(findUserRolesPort.findUserRolesByUserId(USER_ID))
                        .thenReturn(Collections.emptyList());
            }

            @Test
            @DisplayName("기본 권한을 반환한다")
            void shouldReturnDefaultRole() {
                // when
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(VALID_EMAIL);

                // then
                assertThat(userDetails.getAuthorities())
                        .hasSize(1)
                        .extracting("authority")
                        .containsExactly("ROLE_USER");
            }
        }

        @Nested
        @DisplayName("사용자의 권한 조회 중 오류가 발생하면")
        class WhenErrorOccursDuringRoleRetrieval {

            @BeforeEach
            void setUp() {
                when(loadUserPort.loadUserByEmail(any(Email.class)))
                        .thenReturn(Optional.of(User.builder().id(USER_ID).email(new Email(VALID_EMAIL)).build()));
                when(loadUserAuthenticationPort.loadUserAuthenticationByUserId(USER_ID))
                        .thenReturn(Optional.of(UserAuthentication.builder().userId(USER_ID).build()));
                when(findUserRolesPort.findUserRolesByUserId(USER_ID))
                        .thenThrow(new RuntimeException("Database error"));
            }

            @Test
            @DisplayName("UsernameNotFoundException을 발생시킨다")
            void shouldThrowUsernameNotFoundException() {
                // when & then
                assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(VALID_EMAIL))
                        .isInstanceOf(UsernameNotFoundException.class)
                        .hasMessageContaining("Failed to fetch user roles");
            }
        }
    }

    @Nested
    @DisplayName("인증 정보 조회 관련")
    class AuthenticationRetrievalTest {

        @Nested
        @DisplayName("사용자의 인증 정보가 없으면")
        class WhenUserHasNoAuthentication {

            @BeforeEach
            void setUp() {
                when(loadUserPort.loadUserByEmail(any(Email.class)))
                        .thenReturn(Optional.of(User.builder().id(USER_ID).email(new Email(VALID_EMAIL)).build()));
                when(loadUserAuthenticationPort.loadUserAuthenticationByUserId(USER_ID))
                        .thenReturn(Optional.empty());
            }

            @Test
            @DisplayName("UsernameNotFoundException을 발생시킨다")
            void shouldThrowUsernameNotFoundException() {
                // when & then
                assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(VALID_EMAIL))
                        .isInstanceOf(UsernameNotFoundException.class)
                        .hasMessageContaining("UserAuthentication not found for userId");
            }
        }
    }
}
