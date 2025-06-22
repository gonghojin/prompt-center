package com.gongdel.promptserver.adapter.out.persistence.command;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRoleCommandAdapter 테스트")
class UserRoleCommandAdapterTest {

    @Mock
    private UserRoleRepository userRoleJpaRepository;

    @Mock
    private UserRoleMapper userRoleMapper;

    @InjectMocks
    private UserRoleCommandAdapter userRoleCommandAdapter;

    @Nested
    @DisplayName("saveUserRole(UserRole) 메서드는")
    class SaveUserRoleTest {

        private UserRole userRole;
        private UserRoleEntity userRoleEntity;
        private UserRole savedUserRole;

        @BeforeEach
        void setUp() {
            userRole = mock(UserRole.class);
            userRoleEntity = mock(UserRoleEntity.class);
            savedUserRole = mock(UserRole.class);
        }

        @Test
        @DisplayName("사용자 역할을 성공적으로 저장한다")
        void givenValidUserRole_whenSaveUserRole_thenReturnsSavedUserRole() {
            // Given
            when(userRoleMapper.toEntity(userRole)).thenReturn(userRoleEntity);
            when(userRoleJpaRepository.save(userRoleEntity)).thenReturn(userRoleEntity);
            when(userRoleMapper.toDomain(userRoleEntity)).thenReturn(savedUserRole);
            when(savedUserRole.getUserId()).thenReturn(1L);

            // When
            UserRole result = userRoleCommandAdapter.saveUserRole(userRole);

            // Then
            assertThat(result).isEqualTo(savedUserRole);
            verify(userRoleMapper).toEntity(userRole);
            verify(userRoleJpaRepository).save(userRoleEntity);
            verify(userRoleMapper).toDomain(userRoleEntity);
        }

        @Test
        @DisplayName("null UserRole이 전달되면 UserRoleDomainException을 던진다")
        void givenNullUserRole_whenSaveUserRole_thenThrowsUserRoleDomainException() {
            // When & Then
            assertThatThrownBy(() -> userRoleCommandAdapter.saveUserRole(null))
                .isInstanceOf(UserRoleDomainException.class)
                .hasMessageContaining("Invalid user role data");
        }

        @Test
        @DisplayName("데이터베이스 오류 발생 시 UserRoleDomainException을 던진다")
        void givenDatabaseError_whenSaveUserRole_thenThrowsUserRoleDomainException() {
            // Given
            when(userRoleMapper.toEntity(userRole)).thenReturn(userRoleEntity);
            when(userRoleJpaRepository.save(userRoleEntity))
                .thenThrow(new DataAccessException("Database error") {
                });

            // When & Then
            assertThatThrownBy(() -> userRoleCommandAdapter.saveUserRole(userRole))
                .isInstanceOf(UserRoleDomainException.class)
                .hasMessageContaining("Failed to save user role due to database error");
        }

        @Test
        @DisplayName("예상치 못한 오류 발생 시 UserRoleDomainException을 던진다")
        void givenUnexpectedError_whenSaveUserRole_thenThrowsUserRoleDomainException() {
            // Given
            when(userRoleMapper.toEntity(userRole))
                .thenThrow(new RuntimeException("Unexpected error"));

            // When & Then
            assertThatThrownBy(() -> userRoleCommandAdapter.saveUserRole(userRole))
                .isInstanceOf(UserRoleDomainException.class)
                .hasMessageContaining("Unexpected error occurred while saving user role");
        }
    }
}
