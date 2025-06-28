-- 토큰 블랙리스트 테이블 생성
CREATE TABLE token_blacklist
(
    id         BIGSERIAL PRIMARY KEY,
    token_id   VARCHAR(255) NOT NULL,
    user_id    VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP    NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
);

-- 토큰 블랙리스트 인덱스
CREATE INDEX idx_token_blacklist_token_id ON token_blacklist (token_id);
CREATE INDEX idx_token_blacklist_user_id ON token_blacklist (user_id);
CREATE INDEX idx_token_blacklist_expires_at ON token_blacklist (expires_at);

-- 리프레시 토큰 테이블 생성
CREATE TABLE refresh_token
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    VARCHAR(255) NOT NULL,
    token      VARCHAR(512) NOT NULL,
    expires_at TIMESTAMP    NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
);

-- 리프레시 토큰 인덱스
CREATE INDEX idx_refresh_token_user_id ON refresh_token (user_id);
CREATE INDEX idx_refresh_token_token ON refresh_token (token);
CREATE INDEX idx_refresh_token_expires_at ON refresh_token (expires_at);
