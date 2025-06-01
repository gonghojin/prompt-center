-- prompt_templates FK 추가
ALTER TABLE prompt_templates
    ADD CONSTRAINT fk_current_version FOREIGN KEY (current_version_id) REFERENCES prompt_versions (id),
    ADD CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories(id),
    ADD CONSTRAINT fk_created_by FOREIGN KEY (created_by_id) REFERENCES users(id);

-- prompt_versions FK 추가
ALTER TABLE prompt_versions
    ADD CONSTRAINT fk_prompt_template FOREIGN KEY (prompt_template_id) REFERENCES prompt_templates (id),
    ADD CONSTRAINT fk_created_by FOREIGN KEY (created_by_id) REFERENCES users(id);

-- prompt_template_tags FK 추가
ALTER TABLE prompt_template_tags
    ADD CONSTRAINT fk_prompt_template FOREIGN KEY (prompt_template_id) REFERENCES prompt_templates (id),
    ADD CONSTRAINT fk_tag FOREIGN KEY (tag_id) REFERENCES tags(id);
