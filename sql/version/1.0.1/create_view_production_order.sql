CREATE OR REPLACE VIEW production_order_view AS
SELECT
    po.id,
    ce.alias AS counting_equipment_alias,
    po.code AS production_order_code,
    ims.code AS ims_code,
    po.input_batch,
    po.source,
    po.gauge,
    po.category,
    po.washing_process,
    po.created_at,
    po.completed_at,
    COALESCE(SUM(CAST(crpc.computed_value AS bigint)), 0) AS valid_amount
FROM
    production_order po
JOIN
    counting_equipment ce ON po.equipment_id = ce.id
LEFT JOIN
    ims ON ce.ims_id = ims.id
LEFT JOIN
    counter_record_production_conclusion crpc ON po.id = crpc.production_order_id
WHERE
    po.is_completed = true
    AND po.composed_production_order_id IS NULL
    AND crpc.is_valid_for_production = true
GROUP BY
    po.id,
    ce.alias,
    po.code,
    ims.code,
    po.input_batch,
    po.source,
    po.gauge,
    po.category,
    po.washing_process,
    po.created_at,
    po.completed_at;
