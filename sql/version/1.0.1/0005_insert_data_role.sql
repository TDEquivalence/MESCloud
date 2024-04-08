INSERT INTO role (name) VALUES ('SUPER_ADMIN'), ('ADMIN'), ('ANALYST'), ('OPERATOR');

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0005_insert_data_role.sql', '1.0.1', '1.0.1_0005');