-- 태그 테이블 생성 (TagEntity, 데이터 모델 설계 기준)
CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

-- 태그 이름에 대한 유니크 인덱스 (JPA, 모델 문서 반영)
CREATE UNIQUE INDEX idx_tag_name ON tags(name);
