package com.gongdel.promptserver.application.port.out.query;

import com.gongdel.promptserver.domain.model.Tag;

import java.util.Optional;

/**
 * 태그 도메인의 단일 엔티티 조회를 담당하는 포트입니다.
 * ID나 이름과 같은 고유 식별자로 태그를 조회하는 기능을 제공합니다.
 */
public interface LoadTagPort {
    /**
     * ID로 태그를 조회합니다.
     *
     * @param id 조회할 태그 ID
     * @return 조회된 태그 도메인 (Optional)
     * @throws IllegalArgumentException id가 null인 경우
     */
    Optional<Tag> loadTagById(Long id);

    /**
     * 이름으로 태그를 조회합니다.
     *
     * @param name 조회할 태그 이름
     * @return 조회된 태그 도메인 (Optional)
     * @throws IllegalArgumentException name이 null이거나 빈 문자열인 경우
     */
    Optional<Tag> loadTagByName(String name);
}
