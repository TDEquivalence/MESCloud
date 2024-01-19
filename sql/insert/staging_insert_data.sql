INSERT INTO users (first_name, last_name, username, email, password, role, created_at, is_active, is_not_locked)
VALUES
('alcina', 'toto', 'admin', 'pepe@email.com', '$2a$10$kpCZJKj.gmCOVbxs1XNDY.w/cY405lZtoW7ciWRYPy2F38MayZzMS', 'ADMIN', '2023-06-18 22:50:06.035', true, true),
('Post', 'Man', 'postman', 'postman@example.com', '$2a$10$d3RKgivqOkHQw9b/x4Hnpec.7jwLdIpIYCDKAeiTLWAZAcsn7Z9GS', 'SUPER_ADMIN', '2023-06-23 10:56:53.167', true, true);

INSERT INTO factory
(name)
VALUES
('Test Factory');

INSERT INTO section
(factory_id, name)
VALUES
(1, 'OBO');

INSERT INTO counting_equipment
(code, alias, section_id, equipment_status, p_timer_communication_cycle)
VALUES
('OBO001', 'Máquina 1', 1, 0, 0),
('OBO002', 'Máquina 2', 1, 0, 0),
('OBO003', 'Máquina 3', 1, 0, 0),
('OBO004', 'Máquina 4', 1, 0, 0);

INSERT INTO equipment_output_alias
(alias)
VALUES
('OK'),
('NOTOK');

INSERT INTO equipment_output
(counting_equipment_id, code, equipment_output_alias_id, is_valid_for_production)
VALUES
(1, 'OBO001-001', 1, true),
(1, 'OBO001-002', 2, false),
(2, 'OBO002-001', 1, true),
(2, 'OBO002-002', 2, false),
(3, 'OBO003-001', 1, true),
(3, 'OBO003-002', 2, false),
(4, 'OBO004-001', 1, true),
(4, 'OBO004-002', 2, false);

INSERT INTO alarm_configuration (word_index, bit_index, code, description)
VALUES
(0, 0, 'PLC01', 'High Temperature Alarm'),
(0, 1, 'PLC02', 'Low Speed Alarm'),
(0, 2, 'PLC03', 'Pressure Drop Alarm'),
(0, 3, 'PLC04', 'Voltage Fluctuation Alarm'),
(0, 4, 'PLC05', 'Overload Alarm'),
(0, 5, 'PLC06', 'Emergency Stop Alarm'),
(0, 6, 'PLC07', 'Critical Pressure Alarm'),
(0, 7, 'PLC08', 'Voltage Spike Alarm'),
(0, 8, 'PLC09', 'Temperature Anomaly Alarm'),
(0, 9, 'PLC10', 'Speed Deviation Alarm'),
(0, 10, 'PLC11', 'Low Pressure Alarm'),
(0, 11, 'PLC12', 'Voltage Drop Alarm'),
(0, 12, 'PLC13', 'Temperature Warning'),
(0, 13, 'PLC14', 'Speed Limit Exceeded Alarm'),
(0, 14, 'PLC15', 'Pressure Surge Alarm'),
(0, 15, 'PLC16', 'Voltage Anomaly Alarm'),
(1, 0, 'PLC17', 'Critical Temperature Alarm'),
(1, 1, 'PLC18', 'High Speed Alarm'),
(1, 2, 'PLC19', 'Pressure Buildup Alarm'),
(1, 3, 'PLC20', 'Voltage Imbalance Alarm'),
(1, 4, 'PLC21', 'Temperature Fluctuation Alarm'),
(1, 5, 'PLC22', 'Speed Deviation Warning'),
(1, 6, 'PLC23', 'Pressure Anomaly Alarm'),
(1, 7, 'PLC24', 'Voltage Variation Alarm'),
(1, 8, 'PLC25', 'Overheat Warning'),
(1, 9, 'PLC26', 'Excessive Speed Warning'),
(1, 10, 'PLC27', 'Low Pressure Warning'),
(1, 11, 'PLC28', 'Voltage Instability Alarm'),
(1, 12, 'PLC29', 'Temperature Deviation Alarm'),
(1, 13, 'PLC30', 'Speed Limit Warning'),
(1, 14, 'PLC31', 'Pressure Spike Alarm'),
(1, 15, 'PLC32', 'Voltage Fluctuation Warning'),
(2, 0, 'PLC33', 'Critical Overheat Alarm'),
(2, 1, 'PLC34', 'Maximum Speed Alarm'),
(2, 2, 'PLC35', 'Pressure Drop Warning'),
(2, 3, 'PLC36', 'Voltage Surge Alarm'),
(2, 4, 'PLC37', 'Temperature Emergency'),
(2, 5, 'PLC38', 'Speed Exceedance Alarm'),
(2, 6, 'PLC39', 'Pressure Crisis Alarm'),
(2, 7, 'PLC40', 'Voltage Irregularity Alarm'),
(2, 8, 'PLC41', 'Extreme Temperature Alert'),
(2, 9, 'PLC42', 'Speed Irregularity Alarm'),
(2, 10, 'PLC43', 'Low Pressure Crisis Warning'),
(2, 11, 'PLC44', 'Voltage Crisis Alert'),
(2, 12, 'PLC45', 'Temperature Critical Warning'),
(2, 13, 'PLC46', 'Maximum Speed Exceeded Alarm'),
(2, 14, 'PLC47', 'Pressure Critical Alarm'),
(2, 15, 'PLC48', 'Voltage Anomaly Alert'),
(3, 0, 'PLC49', 'Temperature Fluctuation Warning'),
(3, 1, 'PLC50', 'Speed Emergency Stop Alarm'),
(3, 2, 'PLC51', 'Pressure Buildup Warning'),
(3, 3, 'PLC52', 'Voltage Surge Alert'),
(3, 4, 'PLC53', 'Overheat Emergency'),
(3, 5, 'PLC54', 'Maximum Speed Warning'),
(3, 6, 'PLC55', 'Pressure Spike Warning'),
(3, 7, 'PLC56', 'Voltage Variation Warning'),
(3, 8, 'PLC57', 'Critical Temperature Alert'),
(3, 9, 'PLC58', 'Speed Fluctuation Alarm'),
(3, 10, 'PLC59', 'Low Pressure Crisis Warning'),
(3, 11, 'PLC60', 'Voltage Crisis Alert'),
(3, 12, 'PLC61', 'Temperature Variation Warning'),
(3, 13, 'PLC62', 'Speed Limit Alert'),
(3, 14, 'PLC63', 'Pressure Anomaly Warning'),
(3, 15, 'PLC64', 'Voltage Emergency Stop Alarm');