BEGIN;

ALTER TABLE production_order
ADD COLUMN scheduled_time TIMESTAMP;


INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0015_add_total_schedule_time_production_order', '1.0.1', '1.0.1_0015');

COMMIT;