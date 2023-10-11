DROP VIEW production_order_summary;
DROP VIEW composed_summary;

ALTER TABLE composed_production_order
ALTER COLUMN created_at TYPE TIMESTAMP;

ALTER TABLE production_order
ALTER COLUMN created_at TYPE TIMESTAMP,
ALTER COLUMN completed_at TYPE TIMESTAMP;

ALTER TABLE production_instruction
ALTER COLUMN created_at TYPE TIMESTAMP;

ALTER TABLE sample
ALTER COLUMN created_at TYPE TIMESTAMP;

CREATE OR REPLACE VIEW production_order_summary AS
SELECT po.*, COALESCE(SUM(CAST(crpc.computed_value AS bigint)), 0) AS valid_amount
FROM production_order po
LEFT JOIN counter_record_production_conclusion crpc ON po.id = crpc.production_order_id
WHERE po.is_completed = true AND po.composed_production_order_id IS NULL AND crpc.is_valid_for_production = true
GROUP BY po.id;

CREATE OR REPLACE VIEW composed_summary AS
SELECT DISTINCT
    cpo.id, cpo.created_at, cpo.code,
    s.amount, s.reliability,
    po.input_batch, po.source, po.gauge, po.category, po.washing_process, po.ims_id,
    b.id AS batch_id,
    b.code AS batch_code,
    s.amount AS sample_amount,
    b.is_approved AS is_batch_approved,
    COUNT(h.id) AS amount_of_hits,
    COALESCE(SUM(CAST(crpc.computed_value AS bigint)), 0) AS valid_amount
FROM composed_production_order cpo
LEFT JOIN sample s ON cpo.id = s.composed_production_order_id
LEFT JOIN hit h ON s.id = h.sample_id
LEFT JOIN production_order po ON cpo.id = po.composed_production_order_id
LEFT JOIN batch b ON cpo.id = b.composed_production_order_id
LEFT JOIN counter_record_production_conclusion crpc ON po.id = crpc.production_order_id
GROUP BY cpo.id, cpo.created_at, cpo.code, s.amount, s.reliability, po.input_batch, po.source, po.gauge, po.category, po.washing_process, po.ims_id, b.id, b.code, b.is_approved;