BEGIN;

DROP VIEW composed_summary;
CREATE OR REPLACE VIEW composed_summary AS
SELECT DISTINCT ON (cpo.id)
    cpo.id, cpo.created_at, cpo.code, cpo.approved_at, cpo.hit_inserted_at,
    s.amount, s.reliability,
    jsonb_agg(DISTINCT po.id) AS production_order_ids,
    b.id AS batch_id,
    b.code AS batch_code,
    s.amount AS sample_amount,
    b.is_approved AS is_batch_approved,
    subquery.amount_of_hits,
    jsonb_agg(DISTINCT jsonb_build_object('name', pi.name, 'value', pi.value)) AS instructions,
    COALESCE(SUM(CASE WHEN cr.is_valid_for_production THEN CAST(cr.increment AS bigint) ELSE 0 END), 0) AS valid_amount,  -- Modified valid_amount calculation
    ce.section_id  -- Added section_id column
FROM composed_production_order cpo
LEFT JOIN sample s ON cpo.id = s.composed_production_order_id
LEFT JOIN production_order po ON cpo.id = po.composed_production_order_id
LEFT JOIN batch b ON cpo.id = b.composed_production_order_id
LEFT JOIN (
    SELECT s.composed_production_order_id, COUNT(h.id) AS amount_of_hits
    FROM sample s
    JOIN hit h ON s.id = h.sample_id
    GROUP BY s.composed_production_order_id
) AS subquery ON cpo.id = subquery.composed_production_order_id
LEFT JOIN counter_record cr ON po.id = cr.production_order_id
LEFT JOIN production_instruction pi ON po.id = pi.production_order_id
LEFT JOIN counting_equipment ce ON po.equipment_id = ce.id
WHERE cr.is_valid_for_production = true
GROUP BY
    cpo.id, cpo.created_at, cpo.code,
    s.amount, s.reliability,
    b.id, b.code, b.is_approved,
    subquery.amount_of_hits,
    ce.section_id;


INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0020_add_section_composed_summary_view.sql', '1.0.1', '1.0.1_0020');

COMMIT;