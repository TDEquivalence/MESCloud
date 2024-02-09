CREATE OR REPLACE VIEW production_order_summary AS
SELECT po.*, COALESCE(SUM(CAST(crpc.computed_value AS bigint)), 0) AS valid_amount
FROM production_order po
LEFT JOIN counter_record_production_conclusion crpc ON po.id = crpc.production_order_id
WHERE po.is_completed = true AND crpc.is_valid_for_production = true
GROUP BY po.id;

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0004_alter_production_summary_view.sql', '1.0.0', '1.0.0_0004');