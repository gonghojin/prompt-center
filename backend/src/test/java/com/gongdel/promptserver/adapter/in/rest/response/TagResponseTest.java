package com.gongdel.promptserver.adapter.in.rest.response;

import com.gongdel.promptserver.domain.model.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TagResponse DTO 클래스에 대한 단위 테스트
 */
class TagResponseTest {

    @Test
    @DisplayName("Tag 도메인 객체로부터 TagResponse DTO를 생성할 수 있다")
    void fromShouldCreateDtoFromDomain() {
        // given
        Long id = 1L;
        String name = "테스트 태그";

        // Tag 객체를 Mock으로 생성
        Tag tag = Mockito.mock(Tag.class);
        Mockito.when(tag.getId()).thenReturn(id);
        Mockito.when(tag.getName()).thenReturn(name);

        // when
        TagResponse tagResponse = TagResponse.from(tag);

        // then
        assertThat(tagResponse).isNotNull();
        assertThat(tagResponse.getId()).isEqualTo(id);
        assertThat(tagResponse.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("TagResponse 빌더를 사용하여 DTO 객체를 생성할 수 있다")
    void builderShouldCreateDto() {
        // given
        Long id = 1L;
        String name = "빌더 테스트";

        // when
        TagResponse tagResponse = TagResponse.builder()
            .id(id)
            .name(name)
            .build();

        // then
        assertThat(tagResponse).isNotNull();
        assertThat(tagResponse.getId()).isEqualTo(id);
        assertThat(tagResponse.getName()).isEqualTo(name);
    }
}
