package com.gongdel.promptserver.adapter.out.persistence.entity;

import com.gongdel.promptserver.domain.model.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TagEntityTest {

    @Test
    @DisplayName("도메인 모델에서 엔티티 변환 테스트")
    void fromDomainTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Tag tag = Tag.of(1L, "테스트태그", now, now);

        // when
        TagEntity entity = TagEntity.create(tag.getId(), tag.getName(), tag.getCreatedAt(), tag.getUpdatedAt());

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getName()).isEqualTo("테스트태그");
        assertThat(entity.getCreatedAt()).isEqualTo(now);
        assertThat(entity.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("null 도메인 모델에서 엔티티 변환 테스트")
    void fromNullDomainTest() {
        // when
        TagEntity entity = TagEntity.create(null, null);

        // then
        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("새로운 도메인 모델에서 엔티티 변환 테스트 (ID 없음)")
    void fromNewDomainTest() {
        // given
        Tag tag = Tag.create("새태그");

        // when
        TagEntity entity = TagEntity.create(tag.getId(), tag.getName());

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo("새태그");
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("엔티티에서 도메인 모델 변환 테스트")
    void toDomainTest() {
        // given
        TagEntity entity = new TagEntity();
        entity.setId(1L);
        entity.setName("엔티티태그");
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        // when
        Tag tag = Tag.of(entity.getId(), entity.getName(), entity.getCreatedAt(), entity.getUpdatedAt());

        // then
        assertThat(tag).isNotNull();
        assertThat(tag.getId()).isEqualTo(1L);
        assertThat(tag.getName()).isEqualTo("엔티티태그");
        assertThat(tag.getCreatedAt()).isEqualTo(now);
        assertThat(tag.getUpdatedAt()).isEqualTo(now);
    }
}
