BEGIN;

INSERT INTO topic (company_id, section_id, protocol_name)
VALUES (1, 2, 'PROTOCOL_COUNT_V0');

-- IDS CAN BE DIFFERENT
INSERT INTO counting_equipment (code, alias, section_id, equipment_status, p_timer_communication_cycle, operation_status, unrecognized_alarm_duration)
VALUES ('CRK001', 'Máquina CRK', 2, 0, 1, 'IDLE', 10);
INSERT INTO equipment_output_alias (alias)
VALUES ('CRKOK');
INSERT INTO equipment_output (counting_equipment_id, code, equipment_output_alias_id, is_valid_for_production)
VALUES
(9, 'CRK001-001', 3, true),
(9, 'CRK001-002', 3, true),
(9, 'CRK001-003', 3, true),
(9, 'CRK001-004', 3, true),
(9, 'CRK001-005', 3, true),
(9, 'CRK001-006', 3, true),
(9, 'CRK001-007', 3, true),
(9, 'CRK001-008', 3, true);


INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0020_add_section_composed_summary_view.sql', '1.0.1', '1.0.1_0020');

COMMIT;