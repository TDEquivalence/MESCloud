CREATE VIEW alarm_summary AS
SELECT
    a.id AS id,
    ac.code AS configuration_code,
    ac.description AS configuration_description,
    ce.alias AS equipment_alias,
    po.code AS production_order_code,
    a.status AS status,
    a.comment AS comment,
    a.created_at AS created_at,
    a.completed_at AS completed_at,
    a.recognized_at AS recognized_at,
    u.username AS recognized_by_username  -- Include username here
FROM
    alarm a
JOIN
    alarm_configuration ac ON a.alarm_configuration_id = ac.id
JOIN
    counting_equipment ce ON a.equipment_id = ce.id
LEFT JOIN
    production_order po ON a.production_order_id = po.id
LEFT JOIN
    users u ON a.recognized_by = u.id  -- Join to users table to get the username
WHERE
    a.status IN ('ACTIVE', 'INACTIVE', 'RECOGNIZED');

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0006_create_alarm_summary.sql', '1.0.0', '1.0.0_0006');