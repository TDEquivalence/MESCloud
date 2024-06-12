BEGIN;

INSERT INTO section_config (section_id, label, "order") VALUES
(2, 'dashboard', 1),
(2, 'machine-center', 2);

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0023_add_section_config_crk.sql', '1.0.1', '1.0.1_0023');

COMMIT;