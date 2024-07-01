BEGIN;

DROP VIEW IF EXISTS counter_record_summary;
CREATE VIEW counter_record_daily_summary AS
SELECT
    MAX(cr.id) AS id,
    e.id AS equipment_id,
    e.alias AS equipment_alias,
    e.section_id,
    po.id AS production_order_id,
    po.code AS production_order_code,
    eo.id AS equipment_output_id,
    eo_alias.alias AS equipment_output_alias,
    SUM(cr.increment) AS increment_day,
    SUM(cr.increment_active_time) AS active_time_day,
    DATE(cr.registered_at) AS registered_at,
    cr.is_valid_for_production,
    ims.code AS ims,
    po.created_at,
    po.completed_at
FROM
    counter_record cr
JOIN
    equipment_output eo ON cr.equipment_output_id = eo.id
JOIN
    counting_equipment e ON eo.counting_equipment_id = e.id
JOIN
    production_order po ON cr.production_order_id = po.id
LEFT JOIN
    ims ON po.ims_id = ims.id
JOIN
    equipment_output_alias eo_alias ON eo.equipment_output_alias_id = eo_alias.id
GROUP BY
    e.id,
    e.alias,
    e.section_id,
    po.id,
    po.code,
    eo.id,
    eo_alias.alias,
    DATE(cr.registered_at),
    cr.is_valid_for_production,
    ims.code,
    po.created_at,
    po.completed_at;

DROP VIEW IF EXISTS counter_record_detailed_summary;
CREATE OR REPLACE VIEW counter_record_detailed_summary AS
SELECT
    MAX(cr.id) AS id,
    e.id AS equipment_id,
    e.alias AS equipment_alias,
    e.section_id,
    po.id AS production_order_id,
    po.code AS production_order_code,
    eo.id AS equipment_output_id,
    eo_alias.alias AS equipment_output_alias,
    cr.registered_at AS registered_at,
    cr.is_valid_for_production,
    cr.computed_value AS computed_value,
    cr.computed_active_time AS computed_active_time,
    ims.code AS ims,
    COALESCE(jsonb_agg(DISTINCT jsonb_build_object('name', pi.name, 'value', pi.value)) FILTER (WHERE pi.id IS NOT NULL), '[]'::jsonb) AS instructions
FROM
    counter_record cr
JOIN
    equipment_output eo ON cr.equipment_output_id = eo.id
JOIN
    counting_equipment e ON eo.counting_equipment_id = e.id
JOIN
    production_order po ON cr.production_order_id = po.id
LEFT JOIN
    ims ON po.ims_id = ims.id
JOIN
    equipment_output_alias eo_alias ON eo.equipment_output_alias_id = eo_alias.id
LEFT JOIN
    production_instruction pi ON pi.production_order_id = po.id
GROUP BY
    e.id,
    e.alias,
    e.section_id,
    po.id,
    po.code,
    eo.id,
    eo_alias.alias,
    cr.registered_at,
    cr.is_valid_for_production,
    cr.computed_value,
    cr.computed_active_time,
    ims.code
ORDER BY
    id;

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0024_add_counter_record_detailed_summary.sql', '1.0.1', '1.0.1_0024');

COMMIT;