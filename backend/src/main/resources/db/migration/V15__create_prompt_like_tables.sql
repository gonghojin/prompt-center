-- 프롬프트 좋아요 테이블 생성
CREATE TABLE prompt_like
(
    id                 BIGSERIAL PRIMARY KEY,
    user_id            BIGINT    NOT NULL,
    prompt_template_id BIGINT    NOT NULL,
    created_at         TIMESTAMP NOT NULL,
    updated_at         TIMESTAMP NOT NULL,
    CONSTRAINT fk_prompt_like_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_prompt_like_prompt FOREIGN KEY (prompt_template_id) REFERENCES prompt_templates (id) ON DELETE CASCADE,
    CONSTRAINT uk_prompt_like_user_prompt UNIQUE (user_id, prompt_template_id)
);

-- 프롬프트 좋아요 인덱스
CREATE INDEX idx_prompt_like_prompt_template_id ON prompt_like (prompt_template_id);
CREATE INDEX idx_prompt_like_user_id_created_at ON prompt_like (user_id, created_at);

-- 프롬프트 좋아요 수 집계 테이블 생성
CREATE TABLE prompt_like_count
(
    prompt_template_id BIGINT PRIMARY KEY,
    like_count         BIGINT    NOT NULL DEFAULT 0,
    updated_at         TIMESTAMP NOT NULL,
    CONSTRAINT fk_prompt_like_count_prompt FOREIGN KEY (prompt_template_id) REFERENCES prompt_templates (id) ON DELETE CASCADE
);

-- 프롬프트 좋아요 수 인덱스
CREATE INDEX idx_prompt_like_count_like_count ON prompt_like_count (like_count DESC);
CREATE INDEX idx_prompt_like_count_updated_at ON prompt_like_count (updated_at DESC);
