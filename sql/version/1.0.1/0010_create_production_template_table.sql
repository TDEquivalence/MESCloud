BEGIN;

CREATE TABLE production_order_template (
    id SERIAL PRIMARY KEY,
    template_name TEXT
);

CREATE TABLE template_field_mapping (
    id SERIAL PRIMARY KEY,
    template_id BIGINT,
    field_name TEXT,
    FOREIGN KEY (template_id) REFERENCES production_order_template(id)
);


INSERT INTO production_order_template (template_name) VALUES ('default');

INSERT INTO template_field_mapping (template_id, field_name) VALUES (1, 'input_batch');
INSERT INTO template_field_mapping (template_id, field_name) VALUES (1, 'source');
INSERT INTO template_field_mapping (template_id, field_name) VALUES (1, 'gauge');
INSERT INTO template_field_mapping (template_id, field_name) VALUES (1, 'category');
INSERT INTO template_field_mapping (template_id, field_name) VALUES (1, 'washing_process');

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0010_create_production_template_table.sql', '1.0.1', '1.0.1_0010');

COMMIT;