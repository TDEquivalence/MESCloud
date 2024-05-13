BEGIN;

ALTER TABLE production_order ADD COLUMN total_active_time INT;



INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0016_add_total_active_time_production_order', '1.0.1', '1.0.1_0016');

COMMIT;