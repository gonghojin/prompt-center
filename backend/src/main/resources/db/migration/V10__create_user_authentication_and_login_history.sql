-- 사용자 인증 테이블 생성
CREATE TABLE user_authentication (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    password_hash VARCHAR(255),
    last_password_change_at TIMESTAMP,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    CONSTRAINT fk_user_auth_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_user_auth_user ON user_authentication (user_id);

-- 로그인 이력 테이블 생성
CREATE TABLE login_history (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    login_at    TIMESTAMP    NOT NULL,
    ip_address  VARCHAR(45),
    user_agent  VARCHAR(255),
    status      VARCHAR(20)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    CONSTRAINT fk_login_history_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_login_history_user ON login_history (user_id);
CREATE INDEX idx_login_history_login_at ON login_history (login_at);
