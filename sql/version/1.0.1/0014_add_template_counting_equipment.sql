BEGIN;

ALTER TABLE counting_equipment
ADD COLUMN template_id BIGINT;

ALTER TABLE counting_equipment
ADD CONSTRAINT fk_template_id
FOREIGN KEY (template_id)
REFERENCES production_order_template(id);

UPDATE counting_equipment
SET template_id = 1;

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0014_add_template_counting_equipment.sql', '1.0.1', '1.0.1_0014');

COMMIT;