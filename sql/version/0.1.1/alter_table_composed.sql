DROP VIEW composed_summary;

CREATE OR REPLACE VIEW composed_summary AS
SELECT DISTINCT ON (cpo.id)
    cpo.id, cpo.created_at, cpo.code, cpo.approved_at, cpo.hit_inserted_at,
    s.amount, s.reliability,
    po.input_batch, po.source, po.gauge, po.category, po.washing_process,
    b.id AS batch_id,
    b.code AS batch_code,
    s.amount AS sample_amount,
    b.is_approved AS is_batch_approved,
    subquery.amount_of_hits,
    COALESCE(SUM(CAST(crpc.computed_value AS bigint)), 0) AS valid_amount
FROM composed_production_order cpo
LEFT JOIN sample s ON cpo.id = s.composed_production_order_id
LEFT JOIN production_order po ON cpo.id = po.composed_production_order_id
LEFT JOIN batch b ON cpo.id = b.composed_production_order_id
LEFT JOIN counter_record_production_conclusion crpc ON po.id = crpc.production_order_id
LEFT JOIN (
    SELECT s.composed_production_order_id, COUNT(h.id) AS amount_of_hits
    FROM sample s
    JOIN hit h ON s.id = h.sample_id
    GROUP BY s.composed_production_order_id
) AS subquery ON cpo.id = subquery.composed_production_order_id
WHERE crpc.is_valid_for_production = true
GROUP BY
    cpo.id, cpo.created_at, cpo.code,
    s.amount, s.reliability,
    po.input_batch, po.source, po.gauge, po.category, po.washing_process,
    b.id, b.code, b.is_approved,
    subquery.amount_of_hits;