package com.gongdel.promptserver.common.security;

import com.gongdel.promptserver.domain.exception.AuthException;
import com.gongdel.promptserver.domain.user.User;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("CurrentUserProvider 테스트")
class CurrentUserProviderTest {

    @Nested
    @DisplayName("getCurrentUser() 메서드는")
    class GetCurrentUserTest {
        private SecurityContext securityContext;
        private Authentication authentication;
        private User mockUser;
        private SecurityUserDetails userDetails;
        private CurrentUserProvider currentUserProvider;
        private MockedStatic<SecurityContextHolder> contextHolderMockedStatic;

        @BeforeEach
        void setUp() {
            securityContext = mock(SecurityContext.class);
            authentication = mock(Authentication.class);
            mockUser = mock(User.class);
            userDetails = mock(SecurityUserDetails.class);
            currentUserProvider = new CurrentUserProvider();
            contextHolderMockedStatic = Mockito.mockStatic(SecurityContextHolder.class);
            contextHolderMockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        }

        @AfterEach
        void tearDown() {
            contextHolderMockedStatic.close();
        }

        @Test
        @DisplayName("인증된 사용자가 있으면 User를 반환한다")
        void givenAuthenticatedUser_whenGetCurrentUser_thenReturnsUser() {
            // Given
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUser()).thenReturn(mockUser);

            // When
            User result = currentUserProvider.getCurrentUser();

            // Then
            assertThat(result).isEqualTo(mockUser);
        }

        @Test
        @DisplayName("인증 정보가 없으면 AuthException을 던진다")
        void givenNoAuthentication_whenGetCurrentUser_thenThrowsAuthException() {
            // Given
            when(securityContext.getAuthentication()).thenReturn(null);

            // When & Then
            assertThatThrownBy(() -> currentUserProvider.getCurrentUser())
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("인증 정보가 존재하지 않습니다.");
        }

        @Test
        @DisplayName("isAuthenticated가 false면 AuthException을 던진다")
        void givenNotAuthenticated_whenGetCurrentUser_thenThrowsAuthException() {
            // Given
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> currentUserProvider.getCurrentUser())
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("인증되지 않은 요청입니다.");
        }

        @Test
        @DisplayName("principal이 SecurityUserDetails가 아니면 AuthException을 던진다")
        void givenInvalidPrincipalType_whenGetCurrentUser_thenThrowsAuthException() {
            // Given
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn("not-a-user-details");

            // When & Then
            assertThatThrownBy(() -> currentUserProvider.getCurrentUser())
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("인증된 사용자 정보를 찾을 수 없습니다.");
        }
    }
}
