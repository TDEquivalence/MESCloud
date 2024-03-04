ALTER TABLE counting_equipment
ADD COLUMN unrecognized_alarm_duration INTEGER;

UPDATE counting_equipment
SET unrecognized_alarm_duration = 10;

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0007_add_unrecognized_alarm_duration.sql', '1.0.0', '1.0.0_0007');