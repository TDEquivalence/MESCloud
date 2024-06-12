BEGIN;

DROP VIEW IF EXISTS alarm_summary;

CREATE VIEW alarm_summary AS
SELECT
    a.id AS id,
    ac.code AS configuration_code,
    ac.description AS configuration_description,
    ce.alias AS equipment_alias,
    ce.section_id AS section_id,
    po.code AS production_order_code,
    a.status AS status,
    a.comment AS comment,
    a.created_at AS registered_at,
    a.completed_at AS completed_at,
    a.recognized_at AS recognized_at,
    u.first_name AS recognized_by_first_name,
    u.last_name AS recognized_by_last_name,
    CEIL(EXTRACT(EPOCH FROM (a.completed_at - a.created_at))) AS duration
FROM
    alarm a
JOIN
    alarm_configuration ac ON a.alarm_configuration_id = ac.id
JOIN
    counting_equipment ce ON a.equipment_id = ce.id
LEFT JOIN
    production_order po ON a.production_order_id = po.id
LEFT JOIN
    users u ON a.recognized_by = u.id
WHERE
    a.status IN ('ACTIVE', 'INACTIVE', 'RECOGNIZED');


INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0018_add_section_alarm_summary_view.sql', '1.0.1', '1.0.1_0018');

COMMIT;