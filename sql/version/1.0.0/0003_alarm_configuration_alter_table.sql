DELETE FROM alarm_configuration;

ALTER TABLE alarm_configuration
DROP CONSTRAINT word_bit_indexes_unique,
DROP CONSTRAINT alarm_configuration_code_key,
ADD COLUMN equipment_id int NOT NULL,
ADD CONSTRAINT alarm_config_unique UNIQUE (equipment_id, word_index, bit_index, code),
ADD FOREIGN KEY (equipment_id) REFERENCES counting_equipment (id);

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0003_alarm_configuration_alter_table.sql', '1.0.0', '1.0.0_0003');