package com.gongdel.promptserver.domain.user;

import com.gongdel.promptserver.domain.team.Team;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDomainService 테스트")
class UserDomainServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private User mockUser;

    @Mock
    private Team mockTeam;

    private final UserDomainService userDomainService = new UserDomainService();

    @Nested
    @DisplayName("validateEmailDuplication 메서드는")
    class ValidateEmailDuplicationTest {

        @Test
        @DisplayName("이메일이 중복되지 않은 경우 정상 동작한다")
        void givenUniqueEmail_whenValidateEmailDuplication_thenSuccess() {
            // Given
            Email email = new Email("test@example.com");
            when(userRepository.existsByEmail(email)).thenReturn(false);

            // When & Then
            userDomainService.validateEmailDuplication(email, userRepository);
            verify(userRepository).existsByEmail(email);
        }

        @Test
        @DisplayName("이메일이 중복된 경우 UserDomainException을 던진다")
        void givenDuplicateEmail_whenValidateEmailDuplication_thenThrowsException() {
            // Given
            Email email = new Email("test@example.com");
            when(userRepository.existsByEmail(email)).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> userDomainService.validateEmailDuplication(email, userRepository))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("이미 사용 중인 이메일입니다.");
        }

        @Test
        @DisplayName("이메일이 null인 경우 IllegalArgumentException을 던진다")
        void givenNullEmail_whenValidateEmailDuplication_thenThrowsException() {
            // When & Then
            assertThatThrownBy(() -> userDomainService.validateEmailDuplication(null, userRepository))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이메일은 null일 수 없습니다.");
        }

        @Test
        @DisplayName("UserRepository가 null인 경우 IllegalArgumentException을 던진다")
        void givenNullRepository_whenValidateEmailDuplication_thenThrowsException() {
            // Given
            Email email = new Email("test@example.com");

            // When & Then
            assertThatThrownBy(() -> userDomainService.validateEmailDuplication(email, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("사용자 리포지토리는 null일 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("validatePasswordPolicy 메서드는")
    class ValidatePasswordPolicyTest {

        @Test
        @DisplayName("유효한 비밀번호인 경우 정상 동작한다")
        void givenValidPassword_whenValidatePasswordPolicy_thenSuccess() {
            // Given
            Password password = new Password("ValidPass123!");

            // When & Then
            userDomainService.validatePasswordPolicy(password);
        }

        @Test
        @DisplayName("비밀번호가 null인 경우 IllegalArgumentException을 던진다")
        void givenNullPassword_whenValidatePasswordPolicy_thenThrowsException() {
            // When & Then
            assertThatThrownBy(() -> userDomainService.validatePasswordPolicy(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("비밀번호는 null일 수 없습니다.");
        }

        @Test
        @DisplayName("비밀번호가 8자 미만인 경우 UserDomainException을 던진다")
        void givenShortPassword_whenValidatePasswordPolicy_thenThrowsException() {
            // Given
            Password password = new Password("Short1!");

            // When & Then
            assertThatThrownBy(() -> userDomainService.validatePasswordPolicy(password))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("비밀번호는 8자 이상이어야 합니다.");
        }

        @Test
        @DisplayName("비밀번호에 영문자가 없는 경우 UserDomainException을 던진다")
        void givenPasswordWithoutLetter_whenValidatePasswordPolicy_thenThrowsException() {
            // Given
            Password password = new Password("12345678!");

            // When & Then
            assertThatThrownBy(() -> userDomainService.validatePasswordPolicy(password))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("비밀번호에는 영문자가 포함되어야 합니다.");
        }

        @Test
        @DisplayName("비밀번호에 숫자가 없는 경우 UserDomainException을 던진다")
        void givenPasswordWithoutNumber_whenValidatePasswordPolicy_thenThrowsException() {
            // Given
            Password password = new Password("Password!");

            // When & Then
            assertThatThrownBy(() -> userDomainService.validatePasswordPolicy(password))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("비밀번호에는 숫자가 포함되어야 합니다.");
        }

        @Test
        @DisplayName("비밀번호에 특수문자가 없는 경우 UserDomainException을 던진다")
        void givenPasswordWithoutSpecialChar_whenValidatePasswordPolicy_thenThrowsException() {
            // Given
            Password password = new Password("Password123");

            // When & Then
            assertThatThrownBy(() -> userDomainService.validatePasswordPolicy(password))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("비밀번호에는 특수문자가 포함되어야 합니다.");
        }
    }
}
