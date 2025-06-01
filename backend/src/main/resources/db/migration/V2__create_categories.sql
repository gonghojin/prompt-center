;
CREATE TABLE categories
(
    id                 BIGSERIAL PRIMARY KEY,
    name               VARCHAR(100) NOT NULL UNIQUE,
    display_name       VARCHAR(100) NOT NULL,
    description        TEXT,
    is_system          BOOLEAN      NOT NULL DEFAULT false,
    parent_category_id BIGINT       REFERENCES categories (id) ON DELETE SET NULL,
    created_at         TIMESTAMP    NOT NULL,
    updated_at         TIMESTAMP    NOT NULL
);

CREATE INDEX idx_category_name ON categories (name);
CREATE INDEX idx_category_parent ON categories (parent_category_id);
