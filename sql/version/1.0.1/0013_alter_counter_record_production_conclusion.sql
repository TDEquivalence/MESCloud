BEGIN;

DROP VIEW composed_summary;
DROP VIEW counter_record_production_conclusion;

CREATE OR REPLACE VIEW counter_record_production_conclusion AS
SELECT
    cr.id,
    cr.equipment_output_id,
    cr.equipment_output_alias,
    cr.real_value,
    cr.computed_value,
    cr.production_order_id,
    cr.registered_at,
    cr.is_valid_for_production,
    po.code AS production_order_code,
    jsonb_agg(DISTINCT jsonb_build_object('name', pi.name, 'value', pi.value)) AS instructions
FROM (
    SELECT
        eo.id AS equipment_output_id,
        cr.equipment_output_alias,
        po.id AS production_order_id,
        MAX(cr.id) AS max_counter_record_id
    FROM
        equipment_output eo
    INNER JOIN
        production_order po ON eo.counting_equipment_id = po.equipment_id
    INNER JOIN
        counter_record cr ON eo.id = cr.equipment_output_id AND po.id = cr.production_order_id
    GROUP BY
        eo.id,
        cr.equipment_output_alias,
        po.id
) AS last_counter
JOIN
    counter_record cr ON last_counter.max_counter_record_id = cr.id
JOIN
    production_order po ON cr.production_order_id = po.id
JOIN
    production_instruction pi ON pi.production_order_id = po.id
GROUP BY
    cr.id,
    cr.equipment_output_id,
    cr.equipment_output_alias,
    cr.real_value,
    cr.computed_value,
    cr.production_order_id,
    cr.registered_at,
    cr.is_valid_for_production,
    po.code;


INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0013_counter_record_production_conclusion.sql', '1.0.1', '1.0.1_0013');

COMMIT;