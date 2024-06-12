BEGIN;

ALTER TABLE company
ADD COLUMN prefix VARCHAR(10);

UPDATE company
SET prefix = 'MASILVA'
WHERE id = 1;

ALTER TABLE factory
ADD COLUMN prefix VARCHAR(10);

UPDATE factory
SET prefix = 'OPO'
WHERE id = 1;

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0016_add_company_prefix', '1.0.1', '1.0.1_0016');

COMMIT;