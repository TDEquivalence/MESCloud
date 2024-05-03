BEGIN;

ALTER TABLE users
DROP COLUMN user_authorities;

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0003_remove_user_authorities.sql', '1.0.1', '1.0.1_0003');

COMMIT;
