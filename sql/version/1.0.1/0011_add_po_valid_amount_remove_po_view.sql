BEGIN;

ALTER TABLE production_order
ADD COLUMN valid_amount bigint;

UPDATE production_order po
SET valid_amount = (
    SELECT SUM(cr.increment)
    FROM counter_record cr
    WHERE cr.production_order_id = po.id
    AND cr.is_valid_for_production = true
)
WHERE EXISTS (
    SELECT 1
    FROM counter_record cr
    WHERE cr.production_order_id = po.id
    AND cr.is_valid_for_production = true
);

DROP VIEW IF EXISTS production_order_summary;

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0011_add_po_valid_amount_remove_po_view.sql', '1.0.1', '1.0.1_0011');

COMMIT;