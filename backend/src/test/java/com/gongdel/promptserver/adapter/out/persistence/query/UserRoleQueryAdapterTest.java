package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.entity.UserRoleEntity;
import com.gongdel.promptserver.adapter.out.persistence.mapper.UserRoleMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.UserRoleRepository;
import com.gongdel.promptserver.domain.user.UserRole;
import com.gongdel.promptserver.domain.user.UserRoleDomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("UserRoleQueryAdapter 테스트")
@ExtendWith(MockitoExtension.class)
class UserRoleQueryAdapterTest {

    @Mock
    private UserRoleRepository userRoleJpaRepository;

    @Mock
    private UserRoleMapper userRoleMapper;

    @InjectMocks
    private UserRoleQueryAdapter userRoleQueryAdapter;

    @Nested
    @DisplayName("loadUserRoleByUserIdAndRoleId 메서드는")
    class LoadUserRoleByUserIdAndRoleIdTest {

        private Long userId;
        private Long roleId;
        private UserRole mockUserRole;
        private UserRoleEntity mockEntity;

        @BeforeEach
        void setUp() {
            userId = 1L;
            roleId = 1L;
            mockUserRole = mock(UserRole.class);
            mockEntity = mock(UserRoleEntity.class);
        }

        @Test
        @DisplayName("사용자-역할 매핑을 성공적으로 조회한다")
        void givenValidIds_whenLoadUserRole_thenReturnsUserRole() {
            // Given
            when(userRoleJpaRepository.findByUserIdAndRoleId(userId, roleId))
                .thenReturn(Optional.of(mockEntity));
            when(userRoleMapper.toDomain(any(UserRoleEntity.class))).thenReturn(mockUserRole);

            // When
            Optional<UserRole> result = userRoleQueryAdapter.loadUserRoleByUserIdAndRoleId(userId, roleId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(mockUserRole);
            verify(userRoleJpaRepository).findByUserIdAndRoleId(userId, roleId);
            verify(userRoleMapper).toDomain(any(UserRoleEntity.class));
        }

        @Test
        @DisplayName("매핑이 존재하지 않으면 빈 Optional을 반환한다")
        void givenNonExistentMapping_whenLoadUserRole_thenReturnsEmpty() {
            // Given
            when(userRoleJpaRepository.findByUserIdAndRoleId(userId, roleId))
                .thenReturn(Optional.empty());

            // When
            Optional<UserRole> result = userRoleQueryAdapter.loadUserRoleByUserIdAndRoleId(userId, roleId);

            // Then
            assertThat(result).isEmpty();
            verify(userRoleJpaRepository).findByUserIdAndRoleId(userId, roleId);
            verify(userRoleMapper, never()).toDomain(any(UserRoleEntity.class));
        }

        @Test
        @DisplayName("null userId가 전달되면 IllegalArgumentException을 던진다")
        void givenNullUserId_whenLoadUserRole_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> userRoleQueryAdapter.loadUserRoleByUserIdAndRoleId(null, roleId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("userId must not be null");
        }

        @Test
        @DisplayName("null roleId가 전달되면 IllegalArgumentException을 던진다")
        void givenNullRoleId_whenLoadUserRole_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> userRoleQueryAdapter.loadUserRoleByUserIdAndRoleId(userId, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("roleId must not be null");
        }

        @Test
        @DisplayName("데이터베이스 오류 발생 시 UserRoleDomainException을 던진다")
        void givenDatabaseError_whenLoadUserRole_thenThrowsUserRoleDomainException() {
            // Given
            when(userRoleJpaRepository.findByUserIdAndRoleId(userId, roleId))
                .thenThrow(new DataAccessException("Database error") {
                });

            // When & Then
            assertThatThrownBy(() -> userRoleQueryAdapter.loadUserRoleByUserIdAndRoleId(userId, roleId))
                .isInstanceOf(UserRoleDomainException.class)
                .hasMessageContaining("Failed to load user role");
        }
    }

    @Nested
    @DisplayName("findUserRolesByUserId 메서드는")
    class FindUserRolesByUserIdTest {

        private Long userId;
        private UserRole mockUserRole;
        private UserRoleEntity mockEntity;

        @BeforeEach
        void setUp() {
            userId = 1L;
            mockUserRole = mock(UserRole.class);
            mockEntity = mock(UserRoleEntity.class);
        }

        @Test
        @DisplayName("사용자의 모든 역할을 성공적으로 조회한다")
        void givenValidUserId_whenFindUserRoles_thenReturnsUserRoles() {
            // Given
            List<UserRoleEntity> mockEntities = Arrays.asList(mockEntity, mockEntity);
            when(userRoleJpaRepository.findByUserId(userId)).thenReturn(mockEntities);
            when(userRoleMapper.toDomain(any(UserRoleEntity.class))).thenReturn(mockUserRole);

            // When
            List<UserRole> result = userRoleQueryAdapter.findUserRolesByUserId(userId);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(role -> role == mockUserRole);
            verify(userRoleJpaRepository).findByUserId(userId);
            verify(userRoleMapper, times(2)).toDomain(any(UserRoleEntity.class));
        }

        @Test
        @DisplayName("사용자의 역할이 없으면 빈 리스트를 반환한다")
        void givenNoRoles_whenFindUserRoles_thenReturnsEmptyList() {
            // Given
            when(userRoleJpaRepository.findByUserId(userId)).thenReturn(List.of());

            // When
            List<UserRole> result = userRoleQueryAdapter.findUserRolesByUserId(userId);

            // Then
            assertThat(result).isEmpty();
            verify(userRoleJpaRepository).findByUserId(userId);
            verify(userRoleMapper, never()).toDomain(any(UserRoleEntity.class));
        }

        @Test
        @DisplayName("null userId가 전달되면 IllegalArgumentException을 던진다")
        void givenNullUserId_whenFindUserRoles_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> userRoleQueryAdapter.findUserRolesByUserId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("userId must not be null");
        }

        @Test
        @DisplayName("데이터베이스 오류 발생 시 UserRoleDomainException을 던진다")
        void givenDatabaseError_whenFindUserRoles_thenThrowsUserRoleDomainException() {
            // Given
            when(userRoleJpaRepository.findByUserId(userId))
                .thenThrow(new DataAccessException("Database error") {
                });

            // When & Then
            assertThatThrownBy(() -> userRoleQueryAdapter.findUserRolesByUserId(userId))
                .isInstanceOf(UserRoleDomainException.class)
                .hasMessageContaining("Failed to find roles");
        }
    }

    @Nested
    @DisplayName("findUserRolesByRoleId 메서드는")
    class FindUserRolesByRoleIdTest {

        private Long roleId;
        private UserRole mockUserRole;
        private UserRoleEntity mockEntity;

        @BeforeEach
        void setUp() {
            roleId = 1L;
            mockUserRole = mock(UserRole.class);
            mockEntity = mock(UserRoleEntity.class);
        }

        @Test
        @DisplayName("역할의 모든 사용자를 성공적으로 조회한다")
        void givenValidRoleId_whenFindUserRoles_thenReturnsUserRoles() {
            // Given
            List<UserRoleEntity> mockEntities = Arrays.asList(mockEntity, mockEntity);
            when(userRoleJpaRepository.findByRoleId(roleId)).thenReturn(mockEntities);
            when(userRoleMapper.toDomain(any(UserRoleEntity.class))).thenReturn(mockUserRole);

            // When
            List<UserRole> result = userRoleQueryAdapter.findUserRolesByRoleId(roleId);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(role -> role == mockUserRole);
            verify(userRoleJpaRepository).findByRoleId(roleId);
            verify(userRoleMapper, times(2)).toDomain(any(UserRoleEntity.class));
        }

        @Test
        @DisplayName("역할의 사용자가 없으면 빈 리스트를 반환한다")
        void givenNoUsers_whenFindUserRoles_thenReturnsEmptyList() {
            // Given
            when(userRoleJpaRepository.findByRoleId(roleId)).thenReturn(List.of());

            // When
            List<UserRole> result = userRoleQueryAdapter.findUserRolesByRoleId(roleId);

            // Then
            assertThat(result).isEmpty();
            verify(userRoleJpaRepository).findByRoleId(roleId);
            verify(userRoleMapper, never()).toDomain(any(UserRoleEntity.class));
        }

        @Test
        @DisplayName("null roleId가 전달되면 IllegalArgumentException을 던진다")
        void givenNullRoleId_whenFindUserRoles_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> userRoleQueryAdapter.findUserRolesByRoleId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("roleId must not be null");
        }

        @Test
        @DisplayName("데이터베이스 오류 발생 시 UserRoleDomainException을 던진다")
        void givenDatabaseError_whenFindUserRoles_thenThrowsUserRoleDomainException() {
            // Given
            when(userRoleJpaRepository.findByRoleId(roleId))
                .thenThrow(new DataAccessException("Database error") {
                });

            // When & Then
            assertThatThrownBy(() -> userRoleQueryAdapter.findUserRolesByRoleId(roleId))
                .isInstanceOf(UserRoleDomainException.class)
                .hasMessageContaining("Failed to find users");
        }
    }
}
