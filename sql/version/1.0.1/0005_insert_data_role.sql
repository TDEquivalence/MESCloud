INSERT INTO role (name) VALUES ('SUPER_ADMIN'), ('ADMIN'), ('ANALYST'), ('OPERATOR');

INSERT INTO user_role (user_id, role_id, section_id) VALUES
(1, 2, 1),
(2, 2, 1),
(3, 2, 1),
(4, 2, 1),
(5, 2, 1),
(6, 2, 1),
(7, 2, 1),
(8, 2, 1),
(9, 2, 1),
(10, 2, 1),
(11, 2, 1);

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0005_insert_data_role.sql', '1.0.1', '1.0.1_0005');