BEGIN;

INSERT INTO role (name) VALUES ('ADMIN'), ('ANALYST'), ('OPERATOR');

INSERT INTO user_role (user_id, role_id, section_id) VALUES
(1, 1, 1),
(1, 1, 2),
(2, 1, 1),
(4, 3, 1),
(5, 3, 1),
(6, 1, 1);

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0005_insert_data_role.sql', '1.0.1', '1.0.1_0005');

COMMIT;