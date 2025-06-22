package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.entity.RoleEntity;
import com.gongdel.promptserver.adapter.out.persistence.mapper.RoleMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.RoleRepository;
import com.gongdel.promptserver.domain.role.Role;
import com.gongdel.promptserver.domain.role.RoleDomainException;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("RoleQueryAdapter 테스트")
class RoleQueryAdapterTest {

    @Mock
    private RoleRepository roleJpaRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleQueryAdapter roleQueryAdapter;

    @Nested
    @DisplayName("loadRoleById(Long) 메서드는")
    class LoadRoleByIdTest {

        private Long roleId;
        private Role mockRole;
        private RoleEntity mockEntity;

        @BeforeEach
        void setUp() {
            roleId = 1L;
            mockRole = mock(Role.class);
            mockEntity = mock(RoleEntity.class);
        }

        @Test
        @DisplayName("역할을 성공적으로 조회한다")
        void givenValidRoleId_whenLoadRoleById_thenReturnsRole() {
            // Given
            when(roleJpaRepository.findById(roleId))
                .thenReturn(Optional.of(mockEntity));
            when(roleMapper.toDomain(any(RoleEntity.class))).thenReturn(mockRole);

            // When
            Optional<Role> result = roleQueryAdapter.loadRoleById(roleId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(mockRole);
            verify(roleJpaRepository).findById(roleId);
            verify(roleMapper).toDomain(mockEntity);
        }

        @Test
        @DisplayName("역할이 존재하지 않으면 빈 Optional을 반환한다")
        void givenNonExistentRoleId_whenLoadRoleById_thenReturnsEmpty() {
            // Given
            when(roleJpaRepository.findById(roleId))
                .thenReturn(Optional.empty());

            // When
            Optional<Role> result = roleQueryAdapter.loadRoleById(roleId);

            // Then
            assertThat(result).isEmpty();
            verify(roleJpaRepository).findById(roleId);
            verify(roleMapper, never()).toDomain(any(RoleEntity.class));
        }

        @Test
        @DisplayName("데이터베이스 오류 발생 시 RoleDomainException을 던진다")
        void givenDatabaseError_whenLoadRoleById_thenThrowsRoleDomainException() {
            // Given
            when(roleJpaRepository.findById(roleId))
                .thenThrow(new DataAccessException("Database error") {
                });

            // When & Then
            assertThatThrownBy(() -> roleQueryAdapter.loadRoleById(roleId))
                .isInstanceOf(RoleDomainException.class)
                .hasMessageContaining("Database error while loading role");
        }

        @Test
        @DisplayName("null RoleId가 전달되면 IllegalArgumentException을 던진다")
        void givenNullRoleId_whenLoadRoleById_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> roleQueryAdapter.loadRoleById(null))
                .isInstanceOf(RoleDomainException.class)
                .hasMessageContaining("Failed to load role by ID");
        }
    }

    @Nested
    @DisplayName("loadRoleByName(String) 메서드는")
    class LoadRoleByNameTest {

        private String roleName;
        private Role mockRole;
        private RoleEntity mockEntity;

        @BeforeEach
        void setUp() {
            roleName = "ADMIN";
            mockRole = mock(Role.class);
            mockEntity = mock(RoleEntity.class);
        }

        @Test
        @DisplayName("역할을 성공적으로 조회한다")
        void givenValidRoleName_whenLoadRoleByName_thenReturnsRole() {
            // Given
            when(roleJpaRepository.findByName(roleName))
                .thenReturn(Optional.of(mockEntity));
            when(roleMapper.toDomain(any(RoleEntity.class))).thenReturn(mockRole);

            // When
            Optional<Role> result = roleQueryAdapter.loadRoleByName(roleName);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(mockRole);
            verify(roleJpaRepository).findByName(roleName);
            verify(roleMapper).toDomain(mockEntity);
        }

        @Test
        @DisplayName("역할이 존재하지 않으면 빈 Optional을 반환한다")
        void givenNonExistentRoleName_whenLoadRoleByName_thenReturnsEmpty() {
            // Given
            when(roleJpaRepository.findByName(roleName))
                .thenReturn(Optional.empty());

            // When
            Optional<Role> result = roleQueryAdapter.loadRoleByName(roleName);

            // Then
            assertThat(result).isEmpty();
            verify(roleJpaRepository).findByName(roleName);
            verify(roleMapper, never()).toDomain(any(RoleEntity.class));
        }

        @Test
        @DisplayName("데이터베이스 오류 발생 시 RoleDomainException을 던진다")
        void givenDatabaseError_whenLoadRoleByName_thenThrowsRoleDomainException() {
            // Given
            when(roleJpaRepository.findByName(roleName))
                .thenThrow(new DataAccessException("Database error") {
                });

            // When & Then
            assertThatThrownBy(() -> roleQueryAdapter.loadRoleByName(roleName))
                .isInstanceOf(RoleDomainException.class)
                .hasMessageContaining("Database error while loading role");
        }

        @Test
        @DisplayName("빈 이름이 전달되면 IllegalArgumentException을 던진다")
        void givenEmptyRoleName_whenLoadRoleByName_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> roleQueryAdapter.loadRoleByName(""))
                .isInstanceOf(RoleDomainException.class)
                .hasMessageContaining("Failed to load role by name");
        }
    }

    @Nested
    @DisplayName("findAllRoles() 메서드는")
    class FindAllRolesTest {

        private Role mockRole1;
        private Role mockRole2;
        private RoleEntity mockEntity1;
        private RoleEntity mockEntity2;

        @BeforeEach
        void setUp() {
            mockRole1 = mock(Role.class);
            mockRole2 = mock(Role.class);
            mockEntity1 = mock(RoleEntity.class);
            mockEntity2 = mock(RoleEntity.class);
        }

        @Test
        @DisplayName("모든 역할을 성공적으로 조회한다")
        void givenRolesExist_whenFindAllRoles_thenReturnsAllRoles() {
            // Given
            List<RoleEntity> entities = Arrays.asList(mockEntity1, mockEntity2);
            when(roleJpaRepository.findAll()).thenReturn(entities);
            when(roleMapper.toDomain(mockEntity1)).thenReturn(mockRole1);
            when(roleMapper.toDomain(mockEntity2)).thenReturn(mockRole2);

            // When
            List<Role> result = roleQueryAdapter.findAllRoles();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(mockRole1, mockRole2);
            verify(roleJpaRepository).findAll();
            verify(roleMapper).toDomain(mockEntity1);
            verify(roleMapper).toDomain(mockEntity2);
        }

        @Test
        @DisplayName("역할이 없으면 빈 리스트를 반환한다")
        void givenNoRoles_whenFindAllRoles_thenReturnsEmptyList() {
            // Given
            when(roleJpaRepository.findAll()).thenReturn(List.of());

            // When
            List<Role> result = roleQueryAdapter.findAllRoles();

            // Then
            assertThat(result).isEmpty();
            verify(roleJpaRepository).findAll();
            verify(roleMapper, never()).toDomain(any(RoleEntity.class));
        }

        @Test
        @DisplayName("데이터베이스 오류 발생 시 RoleDomainException을 던진다")
        void givenDatabaseError_whenFindAllRoles_thenThrowsRoleDomainException() {
            // Given
            when(roleJpaRepository.findAll())
                .thenThrow(new DataAccessException("Database error") {
                });

            // When & Then
            assertThatThrownBy(() -> roleQueryAdapter.findAllRoles())
                .isInstanceOf(RoleDomainException.class)
                .hasMessageContaining("Database error while finding roles");
        }
    }
}
