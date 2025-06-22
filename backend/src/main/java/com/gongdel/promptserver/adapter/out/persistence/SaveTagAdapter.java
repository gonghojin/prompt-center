package com.gongdel.promptserver.adapter.out.persistence;

import com.gongdel.promptserver.adapter.out.persistence.entity.TagEntity;
import com.gongdel.promptserver.adapter.out.persistence.mapper.TagMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.TagRepository;
import com.gongdel.promptserver.application.port.out.SaveTagPort;
import com.gongdel.promptserver.domain.model.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 태그 저장을 위한 어댑터 구현체
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class SaveTagAdapter implements SaveTagPort {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    /**
     * 태그를 저장합니다.
     *
     * @param tag 저장할 태그
     * @return 저장된 태그
     */
    @Override
    public Tag saveTag(Tag tag) {
        log.debug("태그 저장. 태그 이름: {}", tag.getName());

        TagEntity entity = tagMapper.toEntity(tag);
        TagEntity savedEntity = tagRepository.save(entity);

        log.debug("태그 저장 완료. 태그 ID: {}", savedEntity.getId());
        return tagMapper.toDomain(savedEntity);
    }
}
