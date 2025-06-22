package com.gongdel.promptserver.adapter.out.persistence;

import com.gongdel.promptserver.adapter.out.persistence.entity.TagEntity;
import com.gongdel.promptserver.adapter.out.persistence.mapper.TagMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.TagRepository;
import com.gongdel.promptserver.domain.model.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaveTagAdapterTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private SaveTagAdapter saveTagAdapter;

    @Test
    @DisplayName("태그 저장 테스트")
    void saveTagTest() {
        // given
        Tag tag = Tag.create("테스트태그");

        TagEntity entity = new TagEntity();
        entity.setId(1L);
        entity.setName("테스트태그");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        Tag tagWithId = Tag.of(
            1L,
            "테스트태그",
            entity.getCreatedAt(),
            entity.getUpdatedAt());

        when(tagMapper.toEntity(tag)).thenReturn(entity);
        when(tagMapper.toDomain(entity)).thenReturn(tagWithId);
        when(tagRepository.save(any(TagEntity.class))).thenReturn(entity);

        // when
        Tag savedTag = saveTagAdapter.saveTag(tag);

        // then
        assertThat(savedTag).isNotNull();
        assertThat(savedTag.getId()).isEqualTo(1L);
        assertThat(savedTag.getName()).isEqualTo("테스트태그");
        verify(tagRepository, times(1)).save(any(TagEntity.class));
    }
}
