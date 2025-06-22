package com.gongdel.promptserver.adapter.out.persistence.command;

import com.gongdel.promptserver.adapter.out.persistence.entity.UserEntity;
import com.gongdel.promptserver.adapter.out.persistence.mapper.UserMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.UserJpaRepository;
import com.gongdel.promptserver.domain.exception.UserOperationException;
import com.gongdel.promptserver.domain.user.Email;
import com.gongdel.promptserver.domain.user.User;
import com.gongdel.promptserver.domain.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserCommandAdapter 테스트")
class UserCommandAdapterTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserCommandAdapter userCommandAdapter;

    @Nested
    @DisplayName("saveUser(User) 메서드는")
    class SaveUserTest {

        private User mockUser;
        private UserEntity mockEntity;
        private UserEntity savedEntity;

        @BeforeEach
        void setUp() {
            mockUser = mock(User.class);
            mockEntity = mock(UserEntity.class);
            savedEntity = mock(UserEntity.class);
        }

        @Test
        @DisplayName("사용자를 성공적으로 저장한다")
        void givenValidUser_whenSaveUser_thenReturnsSavedUser() {
            // Given
            Email email = new Email("test@example.com");
            when(mockUser.getEmail()).thenReturn(email);
            when(userMapper.toEntity(any(User.class))).thenReturn(mockEntity);
            when(userJpaRepository.save(any(UserEntity.class))).thenReturn(savedEntity);
            when(userMapper.toDomain(any(UserEntity.class))).thenReturn(mockUser);
            when(savedEntity.getId()).thenReturn(1L);

            // When
            User result = userCommandAdapter.saveUser(mockUser);

            // Then
            assertThat(result).isEqualTo(mockUser);
            verify(userMapper).toEntity(mockUser);
            verify(userJpaRepository).save(mockEntity);
            verify(userMapper).toDomain(savedEntity);
        }

        @Test
        @DisplayName("null User가 전달되면 IllegalArgumentException을 던진다")
        void givenNullUser_whenSaveUser_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> userCommandAdapter.saveUser(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User must not be null");
        }

        @Test
        @DisplayName("데이터베이스 오류 발생 시 UserOperationException을 던진다")
        void givenDatabaseError_whenSaveUser_thenThrowsUserOperationException() {
            // Given
            Email email = new Email("test@example.com");
            when(mockUser.getEmail()).thenReturn(email);
            when(userMapper.toEntity(any(User.class))).thenReturn(mockEntity);
            when(userJpaRepository.save(any(UserEntity.class)))
                .thenThrow(new DataAccessException("Database error") {
                });

            // When & Then
            assertThatThrownBy(() -> userCommandAdapter.saveUser(mockUser))
                .isInstanceOf(UserOperationException.class)
                .hasMessageContaining("Failed to save user")
                .hasMessageContaining(email.getValue());
        }
    }

    @Nested
    @DisplayName("updateUser(User) 메서드는")
    class UpdateUserTest {

        private User mockUser;
        private UserEntity mockEntity;
        private UserEntity updatedEntity;

        @BeforeEach
        void setUp() {
            mockUser = mock(User.class);
            mockEntity = mock(UserEntity.class);
            updatedEntity = mock(UserEntity.class);
        }

        @Test
        @DisplayName("사용자 정보를 성공적으로 수정한다")
        void givenValidUser_whenUpdateUser_thenReturnsUpdatedUser() {
            // Given
            Email email = new Email("test@example.com");
            when(mockUser.getEmail()).thenReturn(email);
            when(userMapper.toEntity(any(User.class))).thenReturn(mockEntity);
            when(userJpaRepository.save(any(UserEntity.class))).thenReturn(updatedEntity);
            when(userMapper.toDomain(any(UserEntity.class))).thenReturn(mockUser);
            when(updatedEntity.getId()).thenReturn(1L);

            // When
            User result = userCommandAdapter.updateUser(mockUser);

            // Then
            assertThat(result).isEqualTo(mockUser);
            verify(userMapper).toEntity(mockUser);
            verify(userJpaRepository).save(mockEntity);
            verify(userMapper).toDomain(updatedEntity);
        }

        @Test
        @DisplayName("null User가 전달되면 IllegalArgumentException을 던진다")
        void givenNullUser_whenUpdateUser_thenThrowsIlUserAuthenticationCommandAdapterlegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> userCommandAdapter.updateUser(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User must not be null");
        }

        @Test
        @DisplayName("데이터베이스 오류 발생 시 UserOperationException을 던진다")
        void givenDatabaseError_whenUpdateUser_thenThrowsUserOperationException() {
            // Given
            Email email = new Email("test@example.com");
            when(mockUser.getEmail()).thenReturn(email);
            when(userMapper.toEntity(any(User.class))).thenReturn(mockEntity);
            when(userJpaRepository.save(any(UserEntity.class)))
                .thenThrow(new DataAccessException("Database error") {
                });

            // When & Then
            assertThatThrownBy(() -> userCommandAdapter.updateUser(mockUser))
                .isInstanceOf(UserOperationException.class)
                .hasMessageContaining("Failed to update user")
                .hasMessageContaining(email.getValue());
        }
    }

    @Nested
    @DisplayName("deleteUser(UserId) 메서드는")
    class DeleteUserTest {

        private UserId userId;

        @BeforeEach
        void setUp() {
            userId = new UserId(UUID.randomUUID());
        }

        @Test
        @DisplayName("사용자를 성공적으로 삭제한다")
        void givenValidUserId_whenDeleteUser_thenDeletesUser() {
            // When
            userCommandAdapter.deleteUser(userId);

            // Then
            verify(userJpaRepository).deleteByUuid(userId.getValue());
        }

        @Test
        @DisplayName("null UserId가 전달되면 IllegalArgumentException을 던진다")
        void givenNullUserId_whenDeleteUser_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> userCommandAdapter.deleteUser(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID must not be null");
        }

        @Test
        @DisplayName("데이터베이스 오류 발생 시 UserOperationException을 던진다")
        void givenDatabaseError_whenDeleteUser_thenThrowsUserOperationException() {
            // Given
            doThrow(new DataAccessException("Database error") {
            })
                .when(userJpaRepository).deleteByUuid(any(UUID.class));

            // When & Then
            assertThatThrownBy(() -> userCommandAdapter.deleteUser(userId))
                .isInstanceOf(UserOperationException.class)
                .hasMessageContaining("Failed to delete user")
                .hasMessageContaining(userId.getValue().toString());
        }
    }
}
