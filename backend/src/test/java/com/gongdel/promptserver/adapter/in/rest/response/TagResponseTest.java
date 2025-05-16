package com.gongdel.promptserver.adapter.in.rest.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.gongdel.promptserver.domain.model.Tag;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * TagResponse DTO 클래스에 대한 단위 테스트
 */
class TagResponseTest {

    @Test
    @DisplayName("Tag 도메인 객체로부터 TagResponse DTO를 생성할 수 있다")
    void fromShouldCreateDtoFromDomain() {
        // given
        UUID id = UUID.randomUUID();
        String name = "테스트 태그";

        Tag tag = Tag.builder()
                .id(id)
                .name(name)
                .build();

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
        UUID id = UUID.randomUUID();
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
