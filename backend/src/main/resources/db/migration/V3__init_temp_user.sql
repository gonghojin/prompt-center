-- 임시 시스템 사용자 생성
INSERT INTO users (id, uuid, email, name, password, role, status, team_id, last_login_at, created_at, updated_at)
VALUES (1,
        '00000000-0000-0000-0000-000000000000',
        'temp@system.local',
        'System User',
        '$2a$10$7EqJtq98hPqEX7fNZaFWoOa5r9r6rFQvY5j5A2e5XJ0b5o9Q6F5lK', -- 'temp_password'의 bcrypt 해시
        'ROLE_USER',
        'ACTIVE',
        NULL,
        NULL,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP) ON CONFLICT (id) DO NOTHING;
