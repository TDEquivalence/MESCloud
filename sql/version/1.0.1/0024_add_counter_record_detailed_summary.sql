BEGIN;

-- Drop the view if it exists
-- Drop the view if it exists
DROP VIEW IF EXISTS counter_record_daily_summary;

-- Create the new view
CREATE OR REPLACE VIEW counter_record_daily_summary AS
WITH pre_aggregated_cr AS (
    SELECT
        equipment_output_id,
        production_order_id,
        DATE_TRUNC('day', registered_at) AS registered_at_day,
        is_valid_for_production,
        SUM(increment) AS increment_day,
        SUM(increment_active_time) AS active_time_day,
        MAX(id) AS max_id
    FROM
        counter_record
    GROUP BY
        equipment_output_id,
        production_order_id,
        DATE_TRUNC('day', registered_at),
        is_valid_for_production
)
SELECT
    cr.max_id AS id,
    e.id AS equipment_id,
    e.alias AS equipment_alias,
    e.section_id,
    po.id AS production_order_id,
    po.code AS production_order_code,
    eo.id AS equipment_output_id,
    eo_alias.alias AS equipment_output_alias,
    cr.increment_day,
    cr.active_time_day,
    cr.registered_at_day AS registered_at,
    cr.is_valid_for_production,
    ims.code AS ims,
    po.created_at,
    po.completed_at
FROM
    pre_aggregated_cr cr
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
ORDER BY
    cr.max_id;

-- Create indexes if they do not already exist
CREATE INDEX IF NOT EXISTS idx_counter_record_equipment_output_id ON counter_record(equipment_output_id);
CREATE INDEX IF NOT EXISTS idx_counter_record_production_order_id ON counter_record(production_order_id);
CREATE INDEX IF NOT EXISTS idx_counter_record_registered_at ON counter_record(DATE_TRUNC('day', registered_at));
CREATE INDEX IF NOT EXISTS idx_equipment_output_counting_equipment_id ON equipment_output(counting_equipment_id);
CREATE INDEX IF NOT EXISTS idx_production_order_ims_id ON production_order(ims_id);

DROP VIEW IF EXISTS counter_record_detailed_summary;
-- Create the new view
CREATE OR REPLACE VIEW counter_record_detailed_summary AS
WITH equipment_cte AS (
    SELECT
        e.id AS equipment_id,
        e.alias AS equipment_alias,
        e.section_id
    FROM
        counting_equipment e
),
production_order_cte AS (
    SELECT
        po.id AS production_order_id,
        po.code AS production_order_code,
        ims.code AS ims_code
    FROM
        production_order po
    LEFT JOIN
        ims ON po.ims_id = ims.id
),
equipment_output_cte AS (
    SELECT
        eo.id AS equipment_output_id,
        eo_alias.alias AS equipment_output_alias,
        eo.counting_equipment_id
    FROM
        equipment_output eo
    JOIN
        equipment_output_alias eo_alias ON eo.equipment_output_alias_id = eo_alias.id
),
production_instruction_cte AS (
    SELECT
        pi.production_order_id,
        jsonb_agg(DISTINCT jsonb_build_object('name', pi.name, 'value', pi.value)) FILTER (WHERE pi.id IS NOT NULL) AS instructions
    FROM
        production_instruction pi
    GROUP BY
        pi.production_order_id
)
SELECT
    MAX(cr.id) AS id,
    e_cte.equipment_id,
    e_cte.equipment_alias,
    e_cte.section_id,
    po_cte.production_order_id,
    po_cte.production_order_code,
    eo_cte.equipment_output_id,
    eo_cte.equipment_output_alias,
    cr.registered_at,
    cr.is_valid_for_production,
    cr.computed_value,
    cr.computed_active_time,
    po_cte.ims_code AS ims,
    COALESCE(pi_cte.instructions, '[]'::jsonb) AS instructions
FROM
    counter_record cr
JOIN
    equipment_output_cte eo_cte ON cr.equipment_output_id = eo_cte.equipment_output_id
JOIN
    equipment_cte e_cte ON eo_cte.counting_equipment_id = e_cte.equipment_id
JOIN
    production_order_cte po_cte ON cr.production_order_id = po_cte.production_order_id
LEFT JOIN
    production_instruction_cte pi_cte ON pi_cte.production_order_id = po_cte.production_order_id
GROUP BY
    e_cte.equipment_id,
    e_cte.equipment_alias,
    e_cte.section_id,
    po_cte.production_order_id,
    po_cte.production_order_code,
    eo_cte.equipment_output_id,
    eo_cte.equipment_output_alias,
    cr.registered_at,
    cr.is_valid_for_production,
    cr.computed_value,
    cr.computed_active_time,
    po_cte.ims_code,
    pi_cte.instructions;

-- Create indexes if they do not already exist
CREATE INDEX IF NOT EXISTS idx_counter_record_equipment_output_id ON counter_record(equipment_output_id);
CREATE INDEX IF NOT EXISTS idx_counter_record_production_order_id ON counter_record(production_order_id);
CREATE INDEX IF NOT EXISTS idx_counter_record_registered_at ON counter_record(registered_at);
CREATE INDEX IF NOT EXISTS idx_equipment_output_counting_equipment_id ON equipment_output(counting_equipment_id);
CREATE INDEX IF NOT EXISTS idx_production_order_ims_id ON production_order(ims_id);




INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0024_add_counter_record_detailed_summary.sql', '1.0.1', '1.0.1_0024');

COMMIT;