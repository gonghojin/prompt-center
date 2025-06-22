package com.gongdel.promptserver.adapter.out.persistence.command;

import com.gongdel.promptserver.adapter.out.persistence.entity.UserAuthenticationEntity;
import com.gongdel.promptserver.adapter.out.persistence.mapper.UserAuthenticationMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.UserAuthenticationRepository;
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
import org.springframework.dao.DataAccessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserAuthenticationCommandAdapter 테스트")
class UserAuthenticationCommandAdapterTest {

    @Mock
    private UserAuthenticationRepository userAuthJpaRepository;

    @Mock
    private UserAuthenticationMapper userAuthMapper;

    @InjectMocks
    private UserAuthenticationCommandAdapter userAuthenticationCommandAdapter;

    @Nested
    @DisplayName("saveUserAuthentication 메서드는")
    class SaveUserAuthenticationTest {

        private UserAuthentication userAuthentication;
        private UserAuthenticationEntity userAuthenticationEntity;
        private UserAuthentication savedUserAuthentication;

        @BeforeEach
        void setUp() {
            userAuthentication = mock(UserAuthentication.class);
            userAuthenticationEntity = mock(UserAuthenticationEntity.class);
            savedUserAuthentication = mock(UserAuthentication.class);
        }

        @Test
        @DisplayName("사용자 인증 정보를 성공적으로 저장한다")
        void givenValidUserAuthentication_whenSaveUserAuthentication_thenReturnsSavedUserAuthentication() {
            // Given
            when(userAuthentication.getUserId()).thenReturn(1L);
            when(userAuthMapper.toEntity(any(UserAuthentication.class))).thenReturn(userAuthenticationEntity);
            when(userAuthJpaRepository.save(any(UserAuthenticationEntity.class))).thenReturn(userAuthenticationEntity);
            when(userAuthMapper.toDomain(any(UserAuthenticationEntity.class))).thenReturn(savedUserAuthentication);

            // When
            UserAuthentication result = userAuthenticationCommandAdapter.saveUserAuthentication(userAuthentication);

            // Then
            assertThat(result).isEqualTo(savedUserAuthentication);
            verify(userAuthMapper).toEntity(userAuthentication);
            verify(userAuthJpaRepository).save(userAuthenticationEntity);
            verify(userAuthMapper).toDomain(userAuthenticationEntity);
        }

        @Test
        @DisplayName("null UserAuthentication이 전달되면 IllegalArgumentException을 던진다")
        void givenNullUserAuthentication_whenSaveUserAuthentication_thenThrowsIllegalArgumentException() {
            // When & Then
            assertThatThrownBy(() -> userAuthenticationCommandAdapter.saveUserAuthentication(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("UserAuthentication must not be null");
        }

        @Test
        @DisplayName("데이터베이스 오류 발생 시 UserAuthenticationDomainException을 던진다")
        void givenDatabaseError_whenSaveUserAuthentication_thenThrowsUserAuthenticationDomainException() {
            // Given
            when(userAuthentication.getUserId()).thenReturn(1L);
            when(userAuthMapper.toEntity(any(UserAuthentication.class))).thenReturn(userAuthenticationEntity);
            when(userAuthJpaRepository.save(any(UserAuthenticationEntity.class)))
                .thenThrow(new DataAccessException("Database error") {
                });

            // When & Then
            assertThatThrownBy(() -> userAuthenticationCommandAdapter.saveUserAuthentication(userAuthentication))
                .isInstanceOf(UserAuthenticationDomainException.class)
                .hasMessageContaining("Failed to save user authentication for user: 1");
        }

        @Test
        @DisplayName("예상치 못한 예외 발생 시 UserAuthenticationDomainException을 던진다")
        void givenUnexpectedError_whenSaveUserAuthentication_thenThrowsUserAuthenticationDomainException() {
            // Given
            when(userAuthentication.getUserId()).thenReturn(1L);
            when(userAuthMapper.toEntity(any(UserAuthentication.class))).thenReturn(userAuthenticationEntity);
            when(userAuthJpaRepository.save(any(UserAuthenticationEntity.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

            // When & Then
            assertThatThrownBy(() -> userAuthenticationCommandAdapter.saveUserAuthentication(userAuthentication))
                .isInstanceOf(UserAuthenticationDomainException.class)
                .hasMessageContaining("Unexpected error while saving user authentication for user: 1");
        }
    }
}
