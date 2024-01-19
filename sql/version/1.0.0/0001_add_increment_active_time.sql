ALTER TABLE counter_record
ADD COLUMN increment_active_time INT;

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0001_add_increment_active_time', '1.0.0', '1.0.0_0001');