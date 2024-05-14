BEGIN;

ALTER TABLE production_order
DROP COLUMN input_batch,
DROP COLUMN source,
DROP COLUMN gauge,
DROP COLUMN category,
DROP COLUMN washing_process;

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0014_remove_production_columns.sql', '1.0.1', '1.0.1_0014');

COMMIT;