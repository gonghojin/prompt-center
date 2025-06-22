package com.gongdel.promptserver.adapter.out.persistence.command;

import com.gongdel.promptserver.adapter.out.persistence.repository.TagRepository;
import com.gongdel.promptserver.domain.exception.TagErrorType;
import com.gongdel.promptserver.domain.exception.TagOperationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("TagCommandAdapter 단위 테스트")
class TagCommandAdapterTest {

    @Mock
    TagRepository tagRepository;

    @InjectMocks
    TagCommandAdapter tagCommandAdapter;

    @Nested
    @DisplayName("deleteById 메서드")
    class DeleteById {
        @Test
        @DisplayName("Given 유효한 ID, When 삭제 요청, Then 정상적으로 삭제된다")
        void givenValidId_whenDelete_thenSuccess() {
            // Given
            Long tagId = 1L;

            // When & Then
            assertThatCode(() -> tagCommandAdapter.deleteById(tagId))
                .doesNotThrowAnyException();
            verify(tagRepository).deleteById(tagId);
        }

        @Test
        @DisplayName("Given null ID, When 삭제 요청, Then IllegalArgumentException 발생")
        void givenNullId_whenDelete_thenIllegalArgumentException() {
            // Given
            Long tagId = null;

            // When & Then
            assertThatThrownBy(() -> tagCommandAdapter.deleteById(tagId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tag ID must not be null");
        }

        @Test
        @DisplayName("Given DB 예외 발생, When 삭제 요청, Then TagOperationException 발생")
        void givenDbException_whenDelete_thenTagOperationException() {
            // Given
            Long tagId = 2L;
            doThrow(new DataAccessException("DB error") {
            }).when(tagRepository).deleteById(tagId);

            // When & Then
            assertThatThrownBy(() -> tagCommandAdapter.deleteById(tagId))
                .isInstanceOf(TagOperationException.class)
                .hasMessageContaining("Failed to delete tag")
                .hasFieldOrPropertyWithValue("errorCode", TagErrorType.OPERATION_ERROR);
        }
    }
}
