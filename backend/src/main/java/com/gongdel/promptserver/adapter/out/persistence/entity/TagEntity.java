package com.gongdel.promptserver.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 태그 정보를 저장하는 JPA 엔티티입니다.
 */
@Entity
@Table(name = "tags", indexes = {
    @Index(name = "idx_tag_name", columnList = "name", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
public class TagEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    /**
     * 태그 엔티티 생성을 위한 오버로딩 팩토리 메서드
     *
     * @param id   태그 ID
     * @param name 태그 이름
     * @return 새로운 TagEntity 인스턴스
     */
    public static TagEntity create(Long id, String name) {
        return create(id, name, null, null);
    }

    /**
     * 태그 엔티티 생성을 위한 팩토리 메서드
     *
     * @param id        태그 ID
     * @param name      태그 이름
     * @param createdAt 생성 시간
     * @param updatedAt 업데이트 시간
     * @return 새로운 TagEntity 인스턴스
     */
    public static TagEntity create(Long id, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (id == null && name == null) {
            return null;
        }
        TagEntity entity = new TagEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setCreatedAt(createdAt != null ? createdAt : LocalDateTime.now());
        entity.setUpdatedAt(updatedAt != null ? updatedAt : LocalDateTime.now());
        return entity;
    }

    // 도메인 모델로 변환
    public com.gongdel.promptserver.domain.model.Tag toDomain() {
        return com.gongdel.promptserver.domain.model.Tag.of(
            this.id,
            this.name,
            this.getCreatedAt(),
            this.getUpdatedAt());
    }
}
