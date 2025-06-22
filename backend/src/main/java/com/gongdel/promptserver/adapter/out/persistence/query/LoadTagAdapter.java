package com.gongdel.promptserver.adapter.out.persistence.query;

import com.gongdel.promptserver.adapter.out.persistence.mapper.TagMapper;
import com.gongdel.promptserver.adapter.out.persistence.repository.TagRepository;
import com.gongdel.promptserver.application.port.out.query.LoadTagPort;
import com.gongdel.promptserver.domain.model.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * 태그 도메인의 단일 엔티티 조회를 구현하는 어댑터입니다.
 * ID나 이름으로 태그를 조회하는 기능을 제공합니다.
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class LoadTagAdapter implements LoadTagPort {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Tag> loadTagById(Long id) {
        Assert.notNull(id, "Tag ID must not be null");
        log.debug("Loading tag by id: {}", id);
        return tagRepository.findById(id)
            .map(tagMapper::toDomain)
            .map(tag -> {
                log.debug("Found tag with id: {}", id);
                return tag;
            });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Tag> loadTagByName(String name) {
        Assert.hasText(name, "Tag name must not be empty");
        log.debug("Loading tag by name: {}", name);
        return tagRepository.findByName(name)
            .map(tagMapper::toDomain)
            .map(tag -> {
                log.debug("Found tag with name: {}", name);
                return tag;
            });
    }
}
