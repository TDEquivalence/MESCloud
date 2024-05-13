BEGIN;

CREATE VIEW counter_record_view AS
SELECT
    MAX(cr.id) AS id,
    e.alias AS equipment_alias,
    po.code AS production_order_code,
    eo.id AS equipment_output_id,
    eo_alias.alias AS equipment_output_alias,
    SUM(cr.increment) AS computed_value,
    cr.registered_at,
    cr.is_valid_for_production,
    ims.code AS ims
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
    e.alias,
    po.code,
    eo.id,
    eo_alias.alias,
    cr.registered_at,
    cr.is_valid_for_production,
    ims.code;


INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0012_create_counter_record_view', '1.0.1', '1.0.1_0012');

COMMIT;