BEGIN;

ALTER TABLE production_order
ADD COLUMN valid_amount bigint;

UPDATE production_order po
SET valid_amount = ps.valid_amount::bigint
FROM production_order_summary ps
WHERE po.id = ps.id;

DROP VIEW IF EXISTS production_order_summary;

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0011_add_po_valid_amount_remove_po_view', '1.0.1', '1.0.1_0011');

COMMIT;