-- 카테고리 초기 데이터 삽입

-- 상위 카테고리
INSERT INTO categories (name, display_name, description, is_system, parent_category_id, created_at, updated_at)
VALUES
    ('development', '개발', '개발 관련 프롬프트 템플릿', true, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('data', '데이터', '데이터 관련 프롬프트 템플릿', true, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('design', '디자인', '디자인 관련 프롬프트 템플릿', true, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('product_management', '제품 관리', '제품 관리 관련 프롬프트 템플릿', true, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('marketing', '마케팅', '마케팅 관련 프롬프트 템플릿', true, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;

-- 하위 카테고리 (상위 카테고리 id를 참조)
INSERT INTO categories (name, display_name, description, is_system, parent_category_id, created_at, updated_at)
SELECT 'backend', '백엔드', '백엔드 개발 관련 프롬프트', true, c.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'development'
ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name, display_name, description, is_system, parent_category_id, created_at, updated_at)
SELECT 'frontend', '프론트엔드', '프론트엔드 개발 관련 프롬프트', true, c.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'development'
ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name, display_name, description, is_system, parent_category_id, created_at, updated_at)
SELECT 'mobile', '모바일', '모바일 개발 관련 프롬프트', true, c.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'development'
ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name, display_name, description, is_system, parent_category_id, created_at, updated_at)
SELECT 'devops', 'DevOps', 'DevOps 관련 프롬프트', true, c.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'development'
ON CONFLICT (name) DO NOTHING;

INSERT INTO categories (name, display_name, description, is_system, parent_category_id, created_at, updated_at)
SELECT 'data_analysis', '데이터 분석', '데이터 분석 관련 프롬프트', true, c.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'data'
ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name, display_name, description, is_system, parent_category_id, created_at, updated_at)
SELECT 'machine_learning', '머신러닝', '머신러닝 관련 프롬프트', true, c.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'data'
ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name, display_name, description, is_system, parent_category_id, created_at, updated_at)
SELECT 'data_engineering', '데이터 엔지니어링', '데이터 엔지니어링 관련 프롬프트', true, c.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'data'
ON CONFLICT (name) DO NOTHING;

INSERT INTO categories (name, display_name, description, is_system, parent_category_id, created_at, updated_at)
SELECT 'ux_design', 'UX 디자인', 'UX 디자인 관련 프롬프트', true, c.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'design'
ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name, display_name, description, is_system, parent_category_id, created_at, updated_at)
SELECT 'ui_design', 'UI 디자인', 'UI 디자인 관련 프롬프트', true, c.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'design'
ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name, display_name, description, is_system, parent_category_id, created_at, updated_at)
SELECT 'interaction_design', '인터랙션 디자인', '인터랙션 디자인 관련 프롬프트', true, c.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'design'
ON CONFLICT (name) DO NOTHING;

INSERT INTO categories (name, display_name, description, is_system, parent_category_id, created_at, updated_at)
SELECT 'product_planning', '제품 기획', '제품 기획 관련 프롬프트', true, c.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'product_management'
ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name, display_name, description, is_system, parent_category_id, created_at, updated_at)
SELECT 'product_analysis', '제품 분석', '제품 분석 관련 프롬프트', true, c.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'product_management'
ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name, display_name, description, is_system, parent_category_id, created_at, updated_at)
SELECT 'agile_methodology', '애자일 방법론', '애자일 방법론 관련 프롬프트', true, c.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'product_management'
ON CONFLICT (name) DO NOTHING;

INSERT INTO categories (name, display_name, description, is_system, parent_category_id, created_at, updated_at)
SELECT 'content_marketing', '콘텐츠 마케팅', '콘텐츠 마케팅 관련 프롬프트', true, c.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'marketing'
ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name, display_name, description, is_system, parent_category_id, created_at, updated_at)
SELECT 'advertising', '광고', '광고 관련 프롬프트', true, c.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP FROM categories c WHERE c.name = 'marketing'
ON CONFLICT (name) DO NOTHING;
