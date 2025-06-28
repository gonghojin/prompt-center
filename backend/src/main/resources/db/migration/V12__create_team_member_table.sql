-- 팀 멤버 테이블 생성
CREATE TABLE team_member
(
    id         BIGSERIAL PRIMARY KEY,
    team_id    BIGINT      NOT NULL,
    user_id    BIGINT      NOT NULL,
    role_id    BIGINT      NOT NULL,
    status     VARCHAR(50) NOT NULL,
    created_at TIMESTAMP   NOT NULL,
    updated_at TIMESTAMP   NOT NULL,
    CONSTRAINT fk_team_member_team FOREIGN KEY (team_id) REFERENCES team (id) ON DELETE CASCADE,
    CONSTRAINT fk_team_member_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_team_member_role FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE,
    CONSTRAINT uk_team_user UNIQUE (team_id, user_id)
);

-- 인덱스 생성
CREATE INDEX idx_team_member_team ON team_member (team_id);
CREATE INDEX idx_team_member_user ON team_member (user_id);
CREATE INDEX idx_team_member_role ON team_member (role_id);
CREATE INDEX idx_team_member_status ON team_member (status);
