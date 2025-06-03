-- 임시 팀, 역할, 사용자, 사용자-역할 데이터 생성

-- 1. 실제 회사 팀 데이터 추가
INSERT INTO team (id, uuid, name, description, status, created_at, updated_at)
VALUES (1, '2e1a7c8b-3d4e-4f5a-8b6c-1d2e3f4a5b6c', '연구1팀', NULL, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, '3f2b8d9c-4e5f-5a6b-9c7d-2e3f4a5b6c7d', '연구2팀', NULL, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (3, '4a3c9e0d-5f6a-6b7c-0d8e-3f4a5b6c7d8e', '연구3팀', NULL, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (4, '5b4d0f1e-6a7b-7c8d-1e9f-4a5b6c7d8e9f', '연구4팀', NULL, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (5, '6c5e1a2f-7b8c-8d9e-2f0a-5b6c7d8e9f0a', 'AI팀', NULL, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (6, '7d6f2b3a-8c9d-9e0f-3a1b-6c7d8e9f0a1b', '전략기획팀', NULL, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (7, '8e7a3c4b-9d0e-0f1a-4b2c-7d8e9f0a1b2c', '품질혁신팀', NULL, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (8, '9f8b4d5c-0e1f-1a2b-5c3d-8e9f0a1b2c3d', '경영지원팀', NULL, 'ACTIVE', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP) ON CONFLICT (id) DO NOTHING;

-- 2. 기본 역할 생성
INSERT INTO role (id, uuid, name, description, created_at, updated_at)
VALUES (1, '7e8a9b10-1c2d-4e3f-8a9b-1c2d4e3f8a9b', 'ROLE_ADMIN', '관리자', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, '2f4c6d8e-9b0a-4c2d-8e3f-7a6b5c4d3e2f', 'ROLE_DEVELOPER', '개발자', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (3, '3a5b7c9d-0e1f-4a2b-9c8d-7e6f5d4c3b2a', 'ROLE_DATA_SCIENTIST', '데이터 과학자', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       (4, '4b6c8d0e-1f2a-4b3c-9d8e-6f5e4d3c2b1a', 'ROLE_DESIGNER', '디자이너', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (5, '5c7d9e1f-2a3b-4c5d-8e9f-0a1b2c3d4e5f', 'ROLE_USER', '일반 사용자', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP) ON CONFLICT (id) DO NOTHING;

-- 3. 임시 사용자 생성
INSERT INTO users (id, uuid, email, name, status, team_id, created_at, updated_at)
VALUES (1,
        '00000000-0000-0000-0000-000000000000',
        'temp@system.local',
        'System User',
        'ACTIVE',
        1,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP) ON CONFLICT (id) DO NOTHING;

-- 4. 임시 사용자-역할 매핑 생성
INSERT INTO user_role (id, uuid, user_id, role_id, created_at, updated_at)
VALUES (1,
        '00000000-0000-0000-0000-000000000100',
        1,
        5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON CONFLICT (id) DO NOTHING;

-- 5. 시퀀스 초기화 (JPA IDENTITY 전략과 동일한 동작 보장)
SELECT setval('team_id_seq', (SELECT COALESCE(MAX(id), 0) FROM team), true);
SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 0) FROM users), true);
SELECT setval('role_id_seq', (SELECT COALESCE(MAX(id), 0) FROM role), true);
SELECT setval('user_role_id_seq', (SELECT COALESCE(MAX(id), 0) FROM user_role), true);
