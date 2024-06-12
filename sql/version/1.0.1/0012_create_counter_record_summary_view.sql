BEGIN;

DROP VIEW counter_record_summary;

CREATE VIEW counter_record_summary AS
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
JOIN
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

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0012_create_counter_record_summary_view.sql', '1.0.1', '1.0.1_0012');

COMMIT;