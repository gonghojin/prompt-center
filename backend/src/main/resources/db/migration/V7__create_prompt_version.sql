-- 프롬프트 버전 테이블 생성 (PromptVersion)
CREATE TABLE prompt_versions
(
    id                 BIGSERIAL PRIMARY KEY,
    uuid               UUID        NOT NULL UNIQUE,
    prompt_template_id BIGINT      NOT NULL,
    version_number     INTEGER     NOT NULL,
    content            TEXT        NOT NULL,
    changes            TEXT,
    created_by_id      BIGINT      NOT NULL,
    created_at         TIMESTAMP   NOT NULL DEFAULT now(),
    input_variables    JSONB,
    action_type        VARCHAR(32) NOT NULL
    -- FK는 나중에 추가
);

CREATE INDEX idx_prompt_version_template ON prompt_versions (prompt_template_id);
CREATE INDEX idx_prompt_version_number ON prompt_versions (version_number);
