package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.entity.UserAuthenticationEntity;
import com.gongdel.promptserver.adapter.out.persistence.mapper.UserAuthenticationMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.UserAuthenticationRepository;
import com.gongdel.promptserver.domain.exception.UserOperationException;
import com.gongdel.promptserver.domain.userauth.UserAuthentication;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserAuthenticationQueryAdapter 테스트")
class UserAuthenticationQueryAdapterTest {

    @Mock
    private UserAuthenticationRepository userAuthJpaRepository;

    @Mock
    private UserAuthenticationMapper userAuthMapper;

    @InjectMocks
    private UserAuthenticationQueryAdapter userAuthenticationQueryAdapter;

    @Nested
    @DisplayName("loadUserAuthenticationByUserId(Long) 메서드는")
    class LoadUserAuthenticationByUserIdTest {

        private final Long validUserId = 1L;
        private final Long nonExistentUserId = 999L;
        private UserAuthentication mockUserAuth;

        @BeforeEach
        void setUp() {
            mockUserAuth = mock(UserAuthentication.class);
        }

        @Test
        @DisplayName("사용자 인증 정보를 성공적으로 조회한다")
        void givenValidUserId_whenLoadUserAuthenticationByUserId_thenReturnsUserAuthentication() {
            // Given
            UserAuthenticationEntity mockEntity = mock(UserAuthenticationEntity.class);
            when(userAuthJpaRepository.findByUserId(validUserId))
                .thenReturn(Optional.of(mockEntity));
            when(userAuthMapper.toDomain(any(UserAuthenticationEntity.class))).thenReturn(mockUserAuth);

            // When
            Optional<UserAuthentication> result = userAuthenticationQueryAdapter
                .loadUserAuthenticationByUserId(validUserId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(mockUserAuth);
            verify(userAuthJpaRepository).findByUserId(validUserId);
            verify(userAuthMapper).toDomain(any(UserAuthenticationEntity.class));
        }

        @Test
        @DisplayName("사용자 인증 정보가 존재하지 않으면 빈 Optional을 반환한다")
        void givenNonExistentUserId_whenLoadUserAuthenticationByUserId_thenReturnsEmpty() {
            // Given
            when(userAuthJpaRepository.findByUserId(nonExistentUserId))
                .thenReturn(Optional.empty());

            // When
            Optional<UserAuthentication> result = userAuthenticationQueryAdapter
                .loadUserAuthenticationByUserId(nonExistentUserId);

            // Then
            assertThat(result).isEmpty();
            verify(userAuthJpaRepository).findByUserId(nonExistentUserId);
            verify(userAuthMapper, never()).toDomain(any());
        }

        @Test
        @DisplayName("null userId가 전달되면 IllegalArgumentException을 던진다")
        void givenNullUserId_whenLoadUserAuthenticationByUserId_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> userAuthenticationQueryAdapter.loadUserAuthenticationByUserId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("userId must not be null");
            verify(userAuthJpaRepository, never()).findByUserId(any());
            verify(userAuthMapper, never()).toDomain(any());
        }

        @Test
        @DisplayName("데이터베이스 오류 발생 시 UserOperationException을 던진다")
        void givenDatabaseError_whenLoadUserAuthenticationByUserId_thenThrowsUserOperationException() {
            // Given
            when(userAuthJpaRepository.findByUserId(validUserId))
                .thenThrow(new DataAccessException("Database error") {
                });

            // When & Then
            assertThatThrownBy(() -> userAuthenticationQueryAdapter.loadUserAuthenticationByUserId(validUserId))
                .isInstanceOf(UserOperationException.class)
                .hasMessageContaining("Failed to load user authentication by userId");
            verify(userAuthJpaRepository).findByUserId(validUserId);
            verify(userAuthMapper, never()).toDomain(any());
        }
    }
}
