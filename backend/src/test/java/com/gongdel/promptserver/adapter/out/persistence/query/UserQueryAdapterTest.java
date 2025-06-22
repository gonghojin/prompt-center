package com.gongdel.promptserver.adapter.out.persistence.query;

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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserQueryAdapter 테스트")
class UserQueryAdapterTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserQueryAdapter userQueryAdapter;

    @Nested
    @DisplayName("loadUserById(UserId) 메서드는")
    class LoadUserByUuidTest {

        private UserId userId;
        private User mockUser;
        private UserEntity mockEntity;

        @BeforeEach
        void setUp() {
            userId = new UserId(UUID.randomUUID());
            mockUser = mock(User.class);
            mockEntity = mock(UserEntity.class);
        }

        @Test
        @DisplayName("사용자를 성공적으로 조회한다")
        void givenValidUserId_whenLoadUserByUuid_thenReturnsUser() {
            // Given
            when(userJpaRepository.findByUuid(userId.getValue()))
                .thenReturn(Optional.of(mockEntity));
            when(userMapper.toDomain(any(UserEntity.class))).thenReturn(mockUser);

            // When
            Optional<User> result = userQueryAdapter.loadUserByUserId(userId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(mockUser);
            verify(userJpaRepository).findByUuid(userId.getValue());
            verify(userMapper).toDomain(any(UserEntity.class));
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 빈 Optional을 반환한다")
        void givenNonExistentUserId_whenLoadUserByUuid_thenReturnsEmpty() {
            // Given
            when(userJpaRepository.findByUuid(userId.getValue()))
                .thenReturn(Optional.empty());

            // When
            Optional<User> result = userQueryAdapter.loadUserByUserId(userId);

            // Then
            assertThat(result).isEmpty();
            verify(userJpaRepository).findByUuid(userId.getValue());
            verify(userMapper, never()).toDomain(any(UserEntity.class));
        }

        @Test
        @DisplayName("데이터베이스 오류 발생 시 UserOperationException을 던진다")
        void givenDatabaseError_whenLoadUserByUuid_thenThrowsUserOperationException() {
            // Given
            when(userJpaRepository.findByUuid(userId.getValue()))
                .thenThrow(new DataAccessException("Database error") {
                });

            // When & Then
            assertThatThrownBy(() -> userQueryAdapter.loadUserByUserId(userId))
                .isInstanceOf(UserOperationException.class)
                .hasMessageContaining("Failed to load user by uuid");
        }

        @Test
        @DisplayName("null UserId가 전달되면 IllegalArgumentException을 던진다")
        void givenNullUserId_whenLoadUserByUuid_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> userQueryAdapter.loadUserByUserId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("userId must not be null");
        }
    }

    @Nested
    @DisplayName("loadUserByEmail(Email) 메서드는")
    class LoadUserByEmailTest {

        private Email email;
        private User mockUser;
        private UserEntity mockEntity;

        @BeforeEach
        void setUp() {
            email = new Email("test@example.com");
            mockUser = mock(User.class);
            mockEntity = mock(UserEntity.class);
        }

        @Test
        @DisplayName("사용자를 성공적으로 조회한다")
        void givenValidEmail_whenLoadUserByEmail_thenReturnsUser() {
            // Given
            when(userJpaRepository.findByEmail(email.getValue()))
                .thenReturn(Optional.of(mockEntity));
            when(userMapper.toDomain(any(UserEntity.class))).thenReturn(mockUser);

            // When
            Optional<User> result = userQueryAdapter.loadUserByEmail(email);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(mockUser);
            verify(userJpaRepository).findByEmail(email.getValue());
            verify(userMapper).toDomain(any(UserEntity.class));
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 빈 Optional을 반환한다")
        void givenNonExistentEmail_whenLoadUserByEmail_thenReturnsEmpty() {
            // Given
            when(userJpaRepository.findByEmail(email.getValue()))
                .thenReturn(Optional.empty());

            // When
            Optional<User> result = userQueryAdapter.loadUserByEmail(email);

            // Then
            assertThat(result).isEmpty();
            verify(userJpaRepository).findByEmail(email.getValue());
            verify(userMapper, never()).toDomain(any(UserEntity.class));
        }

        @Test
        @DisplayName("데이터베이스 오류 발생 시 UserOperationException을 던진다")
        void givenDatabaseError_whenLoadUserByEmail_thenThrowsUserOperationException() {
            // Given
            when(userJpaRepository.findByEmail(email.getValue()))
                .thenThrow(new DataAccessException("Database error") {
                });

            // When & Then
            assertThatThrownBy(() -> userQueryAdapter.loadUserByEmail(email))
                .isInstanceOf(UserOperationException.class)
                .hasMessageContaining("Failed to load user by email");
        }

        @Test
        @DisplayName("null Email이 전달되면 IllegalArgumentException을 던진다")
        void givenNullEmail_whenLoadUserByEmail_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> userQueryAdapter.loadUserByEmail(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email must not be null");
        }
    }

    @Nested
    @DisplayName("loadUserById(Long) 메서드는")
    class LoadUserByIdLongTest {

        private Long id;
        private User mockUser;
        private UserEntity mockEntity;

        @BeforeEach
        void setUp() {
            id = 1L;
            mockUser = mock(User.class);
            mockEntity = mock(UserEntity.class);
        }

        @Test
        @DisplayName("사용자를 성공적으로 조회한다")
        void givenValidId_whenLoadUserById_thenReturnsUser() {
            // Given
            when(userJpaRepository.findById(id))
                .thenReturn(Optional.of(mockEntity));
            when(userMapper.toDomain(any(UserEntity.class))).thenReturn(mockUser);

            // When
            Optional<User> result = userQueryAdapter.loadUserById(id);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(mockUser);
            verify(userJpaRepository).findById(id);
            verify(userMapper).toDomain(any(UserEntity.class));
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 빈 Optional을 반환한다")
        void givenNonExistentId_whenLoadUserById_thenReturnsEmpty() {
            // Given
            when(userJpaRepository.findById(id))
                .thenReturn(Optional.empty());

            // When
            Optional<User> result = userQueryAdapter.loadUserById(id);

            // Then
            assertThat(result).isEmpty();
            verify(userJpaRepository).findById(id);
            verify(userMapper, never()).toDomain(any(UserEntity.class));
        }

        @Test
        @DisplayName("데이터베이스 오류 발생 시 UserOperationException을 던진다")
        void givenDatabaseError_whenLoadUserById_thenThrowsUserOperationException() {
            // Given
            when(userJpaRepository.findById(id))
                .thenThrow(new DataAccessException("Database error") {
                });

            // When & Then
            assertThatThrownBy(() -> userQueryAdapter.loadUserById(id))
                .isInstanceOf(UserOperationException.class)
                .hasMessageContaining("Failed to load user by id");
        }

        @Test
        @DisplayName("null ID가 전달되면 IllegalArgumentException을 던진다")
        void givenNullId_whenLoadUserById_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> userQueryAdapter.loadUserById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id must not be null");
        }
    }
}
