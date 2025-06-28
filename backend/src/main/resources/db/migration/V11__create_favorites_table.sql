-- 즐겨찾기 테이블 생성
CREATE TABLE favorites
(
    id                 BIGSERIAL PRIMARY KEY,
    user_id            BIGINT    NOT NULL,
    prompt_template_id BIGINT    NOT NULL,
    created_at         TIMESTAMP NOT NULL,
    updated_at         TIMESTAMP NOT NULL,
    CONSTRAINT fk_favorites_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_favorites_prompt FOREIGN KEY (prompt_template_id) REFERENCES prompt_templates (id) ON DELETE CASCADE,
    CONSTRAINT uk_favorite_user_prompt UNIQUE (user_id, prompt_template_id)
);

-- 인덱스 생성 (성능 최적화)
CREATE INDEX idx_favorite_user ON favorites (user_id);
CREATE INDEX idx_favorite_prompt ON favorites (prompt_template_id);

-- 생성일시 기준 인덱스 (최신순 정렬용)
CREATE INDEX idx_favorite_created_at ON favorites (created_at DESC);
