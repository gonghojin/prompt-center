-- 프롬프트-태그 연결 테이블 생성 (PromptTemplateTag)
CREATE TABLE prompt_template_tags (
    id BIGSERIAL PRIMARY KEY,
    prompt_template_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uq_prompt_template_tag UNIQUE (prompt_template_id, tag_id)
    -- FK는 나중에 추가
);

CREATE INDEX idx_prompt_template_tag_template ON prompt_template_tags(prompt_template_id);
CREATE INDEX idx_prompt_template_tag_tag ON prompt_template_tags(tag_id);
