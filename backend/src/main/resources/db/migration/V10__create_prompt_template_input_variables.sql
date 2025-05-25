CREATE TABLE prompt_template_input_variables (
    prompt_template_id BIGINT NOT NULL,
    input_variable VARCHAR(255),
    CONSTRAINT fk_prompt_template_input_variables_prompt_template
        FOREIGN KEY(prompt_template_id)
        REFERENCES prompt_templates(id)
        ON DELETE CASCADE
);
