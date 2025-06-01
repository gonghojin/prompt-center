CREATE TABLE users
(
    id            BIGSERIAL PRIMARY KEY,
    uuid          UUID         NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    name          VARCHAR(255) NOT NULL,
    password      VARCHAR(255) NOT NULL,
    role          VARCHAR(50)  NOT NULL,
    team_id       UUID,
    last_login_at TIMESTAMP,
    status        VARCHAR(50)  NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP    NOT NULL
);

CREATE INDEX idx_user_email ON users (email);
CREATE INDEX idx_user_team ON users (team_id);
CREATE INDEX idx_user_uuid ON users (uuid);
