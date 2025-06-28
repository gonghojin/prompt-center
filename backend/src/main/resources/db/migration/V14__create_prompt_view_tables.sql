-- 프롬프트 조회 로그 테이블 생성
CREATE TABLE prompt_view_logs
(
    id                 VARCHAR(36) PRIMARY KEY,
    prompt_template_id BIGINT    NOT NULL,
    user_id            BIGINT,
    ip_address         VARCHAR(45),
    anonymous_id       VARCHAR(36),
    viewed_at          TIMESTAMP NOT NULL,
    created_at         TIMESTAMP NOT NULL,
    updated_at         TIMESTAMP NOT NULL,
    CONSTRAINT fk_prompt_view_logs_prompt FOREIGN KEY (prompt_template_id) REFERENCES prompt_templates (id) ON DELETE CASCADE,
    CONSTRAINT fk_prompt_view_logs_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL
);

-- 프롬프트 조회 로그 인덱스
CREATE INDEX idx_prompt_view_logs_prompt_template_id ON prompt_view_logs (prompt_template_id);
CREATE INDEX idx_prompt_view_logs_user_id_viewed_at ON prompt_view_logs (user_id, viewed_at);
CREATE INDEX idx_prompt_view_logs_ip_address_viewed_at ON prompt_view_logs (ip_address, viewed_at);
CREATE INDEX idx_prompt_view_logs_viewed_at ON prompt_view_logs (viewed_at);
CREATE INDEX idx_prompt_view_logs_duplicate_check_user ON prompt_view_logs (prompt_template_id, user_id, viewed_at);
CREATE INDEX idx_prompt_view_logs_duplicate_check_ip ON prompt_view_logs (prompt_template_id, ip_address, viewed_at);
CREATE INDEX idx_prompt_view_logs_duplicate_check_anonymous ON prompt_view_logs (prompt_template_id, anonymous_id, viewed_at);

-- 프롬프트 조회수 집계 테이블 생성
CREATE TABLE prompt_view_counts
(
    prompt_template_id BIGINT PRIMARY KEY,
    total_view_count   BIGINT    NOT NULL DEFAULT 0,
    created_at         TIMESTAMP NOT NULL,
    updated_at         TIMESTAMP NOT NULL,
    CONSTRAINT fk_prompt_view_counts_prompt FOREIGN KEY (prompt_template_id) REFERENCES prompt_templates (id) ON DELETE CASCADE
);

-- 프롬프트 조회수 인덱스
CREATE INDEX idx_prompt_view_counts_total_view_count ON prompt_view_counts (total_view_count DESC);
CREATE INDEX idx_prompt_view_counts_updated_at ON prompt_view_counts (updated_at DESC);
