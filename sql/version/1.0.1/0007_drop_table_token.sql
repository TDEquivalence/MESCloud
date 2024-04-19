BEGIN;

DROP TABLE IF EXISTS token;

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0007_drop_token_table.sql', '1.0.1', '1.0.1_0006');

COMMIT;