BEGIN;

INSERT INTO section_config (section_id, label, "order")
VALUES (1, 'report', 5);

UPDATE section_config
SET "order" = 6
WHERE id = 10;

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0006_add_report_section_config.sql', '1.0.1', '1.0.1_0006');

COMMIT;