-- 프롬프트 템플릿 테이블 생성 (PromptTemplate)
CREATE TABLE prompt_templates (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    current_version_id BIGINT,
    category_id BIGINT,
    created_by_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    visibility VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    description TEXT,
    input_variables TEXT
    -- FK는 나중에 추가
);

CREATE INDEX idx_prompt_template_category ON prompt_templates(category_id);
CREATE INDEX idx_prompt_template_created_by ON prompt_templates(created_by_id);
CREATE INDEX idx_prompt_template_status ON prompt_templates(status);
CREATE INDEX idx_prompt_template_visibility ON prompt_templates(visibility);
CREATE INDEX idx_prompt_template_uuid ON prompt_templates(uuid);
