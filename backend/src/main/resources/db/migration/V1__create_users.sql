CREATE TABLE team (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID         NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    status      VARCHAR(50)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL
);

CREATE INDEX idx_team_uuid ON team (uuid);
CREATE INDEX idx_team_name ON team (name);

CREATE TABLE users
(
    id            BIGSERIAL PRIMARY KEY,
    uuid          UUID         NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    name          VARCHAR(255) NOT NULL,
    team_id       BIGINT,
    status        VARCHAR(50)  NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP    NOT NULL,
    CONSTRAINT fk_user_team FOREIGN KEY (team_id) REFERENCES team(id)
);

CREATE INDEX idx_user_email ON users (email);
CREATE INDEX idx_user_team ON users (team_id);
CREATE INDEX idx_user_uuid ON users (uuid);

CREATE TABLE role (
    id          BIGSERIAL PRIMARY KEY,
    uuid        UUID         NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP    NOT NULL
);

CREATE INDEX idx_role_uuid ON role (uuid);
CREATE INDEX idx_role_name ON role (name);

CREATE TABLE user_role (
    id      BIGSERIAL PRIMARY KEY,
    uuid    UUID      NOT NULL UNIQUE,
    user_id BIGINT    NOT NULL,
    role_id BIGINT    NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP    NOT NULL,
    CONSTRAINT fk_userrole_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_userrole_role FOREIGN KEY (role_id) REFERENCES role(id),
    CONSTRAINT uq_userrole_user_role UNIQUE (user_id, role_id)
);

CREATE INDEX idx_userrole_uuid ON user_role (uuid);
CREATE INDEX idx_userrole_user ON user_role (user_id);
CREATE INDEX idx_userrole_role ON user_role (role_id);
