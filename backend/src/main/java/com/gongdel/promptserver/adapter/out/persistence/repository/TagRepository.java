package com.gongdel.promptserver.adapter.out.persistence.repository;

import com.gongdel.promptserver.adapter.out.persistence.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 태그 엔티티에 대한 데이터 액세스를 제공하는 레포지토리 인터페이스입니다.
 */
@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {

    /**
     * 태그 이름으로 태그를 조회합니다.
     *
     * @param name 조회할 태그 이름
     * @return 태그 엔티티
     */
    Optional<TagEntity> findByName(String name);

    /**
     * 태그 이름으로 태그 존재 여부를 확인합니다.
     *
     * @param name 확인할 태그 이름
     * @return 존재 여부
     */
    boolean existsByName(String name);
}
